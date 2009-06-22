package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;


import fi.hut.soberit.agilefant.business.impl.TimesheetBusinessImpl;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;
import fi.hut.soberit.agilefant.util.StoryTimesheetNode;
import fi.hut.soberit.agilefant.util.TaskTimesheetNode;
import fi.hut.soberit.agilefant.util.TimesheetData;

public class TimesheetBusinessTest extends TimesheetBusinessImpl {
    
    private HourEntryDAO hourEntryDAO;
    
    private Set<Integer> backlogIds;
    private Set<Integer> userIds;
    private DateTime startDate;
    private DateTime endDate;
    private BacklogHourEntry backlogHE;
    private StoryHourEntry storyHE;
    private TaskHourEntry taskHE;
    private TaskHourEntry iterTaskHE;
    private TimesheetData sheetData;
    
    private class TimesheetTestNode extends BacklogTimesheetNode {

        public TimesheetTestNode() {
            super(null);
        }
        @Override
        public long getEffortSum() {
            return 500L;
        }
    }
    
    @Before
    public void setUp() {
        this.hourEntryDAO = createMock(HourEntryDAO.class);
        super.setHourEntryDAO(this.hourEntryDAO);
        
        backlogIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        userIds = new HashSet<Integer>(Arrays.asList(1,2));
        startDate  = new DateTime(2009,5,5,1,1,0,0);
        endDate = new DateTime(2009,10,10,1,1,0,0);
        
        Product prod = new Product();
        prod.setId(1);
        Project proj = new Project();
        proj.setId(2);
        prod.setParent(prod);
        Iteration iter = new Iteration();
        iter.setId(3);
        iter.setParent(proj);
        Story story = new Story();
        story.setId(1);
        story.setBacklog(iter);
        Task task = new Task();
        task.setId(1);
        task.setStory(story);
        Task iterationTask = new Task();
        iterationTask.setId(2);
        iterationTask.setIteration(iter);
        
        backlogHE = new BacklogHourEntry();
        backlogHE.setBacklog(proj);
        storyHE = new StoryHourEntry();
        storyHE.setStory(story);
        taskHE = new TaskHourEntry();
        taskHE.setTask(task);
        iterTaskHE = new TaskHourEntry();
        iterTaskHE.setTask(iterationTask);
        
        sheetData = new TimesheetData();
        sheetData.addEntry(backlogHE);
        sheetData.addEntry(iterTaskHE);
        sheetData.addEntry(storyHE);
        sheetData.addEntry(taskHE);
        sheetData.addNode(new BacklogTimesheetNode(iter));
        sheetData.addNode(new BacklogTimesheetNode(prod));
    }
    
    @Test
    public void getRootNodeSum_noNodes() {
        TimesheetBusiness tsb = new TimesheetBusinessImpl();
        assertEquals(0L, tsb.getRootNodeSum(null));
        List<BacklogTimesheetNode> nodes = Collections.emptyList();
        assertEquals(0L, tsb.getRootNodeSum(nodes));
    }
    
    @Test
    public void getRootNodeSum_oneNode() {
        List<BacklogTimesheetNode> nodes = Arrays.asList((BacklogTimesheetNode)new TimesheetTestNode());
        TimesheetBusiness tsb = new TimesheetBusinessImpl();
        assertEquals(500L, tsb.getRootNodeSum(nodes));
    }
    
    @Test
    public void getRootNodeSum_multipleNodes() {
        List<BacklogTimesheetNode> nodes = Arrays.asList((BacklogTimesheetNode)new TimesheetTestNode(), (BacklogTimesheetNode)new TimesheetTestNode());
        TimesheetBusiness tsb = new TimesheetBusinessImpl();
        assertEquals(1000L, tsb.getRootNodeSum(nodes));
    }
    
    @Test
    public void testGetRootNodes_simple() {
        TimesheetData sheetData = new TimesheetData();
        Product prod = new Product();
        BacklogTimesheetNode rootNode = new BacklogTimesheetNode(prod);
        sheetData.addNode(rootNode);
        TimesheetBusiness tsb = new TimesheetBusinessImpl();
        List<BacklogTimesheetNode> nodes = tsb.findRootNodes(sheetData);
        assertEquals(1, nodes.size());
    }
    @Test
    public void testGetRootNodes_noRoots() {
        TimesheetData sheetData = new TimesheetData();
        TimesheetBusiness tsb = new TimesheetBusinessImpl();
        List<BacklogTimesheetNode> nodes = tsb.findRootNodes(sheetData);
        assertEquals(0, nodes.size());
    }
    
    @Test
    public void testGetRootNodes_muptipleBacklogs() {
        TimesheetData sheetData = new TimesheetData();
        Product prod = new Product();
        prod.setId(1);
        BacklogTimesheetNode rootNode = new BacklogTimesheetNode(prod);
        Project proj = new Project();
        proj.setId(2);
        BacklogTimesheetNode nonRootNode = new BacklogTimesheetNode(proj);

        sheetData.addNode(rootNode);
        sheetData.addNode(nonRootNode);
        TimesheetBusiness tsb = new TimesheetBusinessImpl();
        List<BacklogTimesheetNode> nodes = tsb.findRootNodes(sheetData);
        assertEquals(1, nodes.size());
    }
    
    @Test
    public void testGetUnlinkedTimesheetData_emptyDataset() {
        List<BacklogHourEntry> emptyBacklogHEList = new ArrayList<BacklogHourEntry>();
        List<StoryHourEntry> emptyStoryHEList = new ArrayList<StoryHourEntry>();
        List<TaskHourEntry> emptyTaskHEList = new ArrayList<TaskHourEntry>();
        
        expect(this.hourEntryDAO.getBacklogHourEntriesByFilter(backlogIds, startDate, endDate, userIds)).andReturn(emptyBacklogHEList);
        expect(this.hourEntryDAO.getStoryHourEntriesByFilter(backlogIds, startDate, endDate, userIds)).andReturn(emptyStoryHEList);
        expect(this.hourEntryDAO.getTaskHourEntriesByFilter(backlogIds, startDate, endDate, userIds)).andReturn(emptyTaskHEList);
        
        replay(this.hourEntryDAO);
        
        TimesheetData actualData = super.getUnlinkedTimesheetData(backlogIds, startDate, endDate, userIds);
        assertNotNull(actualData);
        verify(this.hourEntryDAO);
    }
    @Test
    public void testGetUnlinkedTimesheetData_withData() {
        List<BacklogHourEntry> emptyBacklogHEList = Arrays.asList(this.backlogHE);
        List<StoryHourEntry> emptyStoryHEList = Arrays.asList(this.storyHE);
        List<TaskHourEntry> emptyTaskHEList = Arrays.asList(this.taskHE);
        
        expect(this.hourEntryDAO.getBacklogHourEntriesByFilter(backlogIds, startDate, endDate, userIds)).andReturn(emptyBacklogHEList);
        expect(this.hourEntryDAO.getStoryHourEntriesByFilter(backlogIds, startDate, endDate, userIds)).andReturn(emptyStoryHEList);
        expect(this.hourEntryDAO.getTaskHourEntriesByFilter(backlogIds, startDate, endDate, userIds)).andReturn(emptyTaskHEList);
        
        replay(this.hourEntryDAO);
        
        TimesheetData actualData = super.getUnlinkedTimesheetData(backlogIds, startDate, endDate, userIds);
        assertNotNull(actualData);
        assertNotNull(actualData.getBacklogNode(2));
        assertNotNull(actualData.getStoryNode(1));
        assertNotNull(actualData.getTaskNode(1));
        assertNull(actualData.getBacklogNode(1));
        assertNull(actualData.getStoryNode(2));
        assertNull(actualData.getTaskNode(2));
        verify(this.hourEntryDAO);
    }
    
    @Test
    public void testLinkTasks() {
        StoryTimesheetNode parentStoryNode = sheetData.getStoryNode(1);
        BacklogTimesheetNode parentIterationNode = sheetData.getBacklogNode(3);
        assertFalse(parentStoryNode.getHasChildren());
        assertFalse(parentIterationNode.getHasChildren());
        super.linkTasks(sheetData);
        assertTrue(parentStoryNode.getHasChildren());
        assertEquals(1, parentStoryNode.getChildren().size());  
        assertTrue(parentIterationNode.getHasChildren());
        assertEquals(1, parentIterationNode.getTaskNodes().size());
    }
    
    @Test
    public void testAttachTaskNodeToStoryNode_notInData() {
        Story story = new Story();
        story.setId(42);
        Task task = new Task();
        task.setStory(story);
        TaskTimesheetNode node = new TaskTimesheetNode(task);
        TimesheetData data = new TimesheetData();
        super.attachTaskNodeToStoryNode(data, node);
        assertNotNull(data.getStoryNode(42));
        assertEquals(1, data.getStoryNode(42).getChildren().size());
        assertEquals(node, data.getStoryNode(42).getChildren().get(0));
    }
    
    @Test
    public void testAttachTaskNodeToStoryNode_inData() {
        TaskTimesheetNode taskNode = sheetData.getTaskNode(1);
        super.attachTaskNodeToStoryNode(sheetData, taskNode);
        assertEquals(taskNode, sheetData.getStoryNode(1).getChildren().get(0));
    }
    @Test
    public void testAttachTaskNodeToStoryNode_nullStory() {
        Task task = new Task();
        task.setStory(null);
        TaskTimesheetNode node = new TaskTimesheetNode(task);
        super.attachTaskNodeToStoryNode(sheetData, node);
    }
    
    @Test
    public void testAttachTaskNodeToStoryNode_duplicate() {
        TaskTimesheetNode taskNode = sheetData.getTaskNode(1);
        super.attachTaskNodeToStoryNode(sheetData, taskNode);
        super.attachTaskNodeToStoryNode(sheetData, taskNode);
        assertEquals(1, sheetData.getStoryNode(1).getChildren().size());
    }
    
    @Test
    public void testAttachTaskNodeToIterationNode_notInData() {
        Iteration iteration = new Iteration();
        iteration.setId(42);
        Task task = new Task();
        task.setIteration(iteration);
        TaskTimesheetNode node = new TaskTimesheetNode(task);
        TimesheetData data = new TimesheetData();
        super.attachTaskNodeToIterationNode(data, node);
        assertNotNull(data.getBacklogNode(42));
        assertEquals(1, data.getBacklogNode(42).getTaskNodes().size());
        assertEquals(node, data.getBacklogNode(42).getTaskNodes().get(0));

    }
    
    @Test
    public void testAttachTaskNodeToIterationNode_inData() {
        Iteration iter = new Iteration();
        iter.setId(6);
        BacklogTimesheetNode iterNode = new BacklogTimesheetNode(iter);
        sheetData.addNode(iterNode);
        Task task = new Task();
        task.setId(3);
        task.setIteration(iter);
        TaskTimesheetNode taskNode = new TaskTimesheetNode(task);
        super.attachTaskNodeToIterationNode(sheetData, taskNode);
        assertEquals(taskNode, sheetData.getBacklogNode(6).getTaskNodes().get(0));
    }
    
    @Test
    public void testAttachTaskNodeToIterationNode_duplicate() {
        Iteration iter = new Iteration();
        iter.setId(6);
        BacklogTimesheetNode iterNode = new BacklogTimesheetNode(iter);
        sheetData.addNode(iterNode);
        Task task = new Task();
        task.setId(3);
        task.setIteration(iter);
        TaskTimesheetNode taskNode = new TaskTimesheetNode(task);
        super.attachTaskNodeToIterationNode(sheetData, taskNode);
        super.attachTaskNodeToIterationNode(sheetData, taskNode);
        assertEquals(1, sheetData.getBacklogNode(6).getTaskNodes().size());
    }
    @Test
    public void testAttachTaskNodeToIterationNode_nullIteration() {
        Task task = new Task();
        task.setIteration(null);
        TaskTimesheetNode node = new TaskTimesheetNode(task);
        super.attachTaskNodeToIterationNode(sheetData, node);
    }
    
    @Test
    public void testAttachStoryNodeToBacklogNode_notInData() {
        Project proj = new Project();
        proj.setId(42);
        Story story = new Story();
        story.setId(52);
        story.setBacklog(proj);
        StoryTimesheetNode storyNode = new StoryTimesheetNode(story);
        TimesheetData sheetData = new TimesheetData();
        super.attachStoryNodeToBacklogNode(sheetData, storyNode);
        assertNotNull(sheetData.getBacklogNode(42));
        assertEquals(1, sheetData.getBacklogNode(42).getStoryNodes().size());
        assertEquals(storyNode, sheetData.getBacklogNode(42).getStoryNodes().get(0));
    }
    @Test
    public void testAttachStoryNodeToBacklogNode_inData() {
        StoryTimesheetNode storyNode = sheetData.getStoryNode(1);
        super.attachStoryNodeToBacklogNode(sheetData, storyNode);
        assertEquals(storyNode, sheetData.getBacklogNode(3).getStoryNodes().get(0));
    }
    @Test
    public void testAttachStoryNodeToBacklogNode_duplicate() {
        StoryTimesheetNode storyNode = sheetData.getStoryNode(1);
        super.attachStoryNodeToBacklogNode(sheetData, storyNode);
        super.attachStoryNodeToBacklogNode(sheetData, storyNode);
        assertEquals(1, sheetData.getBacklogNode(3).getStoryNodes().size());
    }
    @Test
    public void testAttachStoryNodeToBacklogNode_nullBacklog() {
        Story story = new Story();
        story.setId(5);
        story.setBacklog(null);
        StoryTimesheetNode node = new StoryTimesheetNode(story);
        super.attachStoryNodeToBacklogNode(sheetData, node);
    }
    
    @Test
    public void testLinkStoriesToBacklogs() {
        super.linkStories(sheetData);
        assertEquals(1, sheetData.getBacklogNode(3).getStoryNodes().size());
    }
    
    @Test
    public void testAttachBacklogNodeToBacklogNode_notInData() {
        Project project = new Project();
        project.setId(10);
        Iteration iteration = new Iteration();
        iteration.setId(11);
        iteration.setParent(project);
        BacklogTimesheetNode blNode = new BacklogTimesheetNode(iteration);
        TimesheetData sheetData = new TimesheetData();
        super.attachBacklogNodeToBacklogNode(sheetData, blNode);
        assertNotNull(sheetData.getBacklogNode(10));
        assertEquals(blNode, sheetData.getBacklogNode(10).getBacklogNodes().get(0));
    }
    @Test
    public void testAttachBacklogNodeToBacklogNode_inData() {
        BacklogTimesheetNode iterNode = sheetData.getBacklogNode(3);
        Backlog proj = iterNode.getBacklog().getParent();
        BacklogTimesheetNode blNode = new BacklogTimesheetNode(proj);
        sheetData.addNode(blNode);
        super.attachBacklogNodeToBacklogNode(sheetData, iterNode);
        assertEquals(iterNode, blNode.getBacklogNodes().get(0));
    }
    @Test
    public void testAttachBacklogNodeToBacklogNode_duplicate() {
        BacklogTimesheetNode iterNode = sheetData.getBacklogNode(3);
        Backlog proj = iterNode.getBacklog().getParent();
        BacklogTimesheetNode blNode = new BacklogTimesheetNode(proj);
        sheetData.addNode(blNode);
        super.attachBacklogNodeToBacklogNode(sheetData, iterNode);
        super.attachBacklogNodeToBacklogNode(sheetData, iterNode);
        assertEquals(1, blNode.getBacklogNodes().size());
    }
    @Test
    public void testAttachBacklogNodeToBacklogNode_null() {
        Iteration iteration = new Iteration();
        iteration.setId(11);
        iteration.setParent(null);
        BacklogTimesheetNode blNode = new BacklogTimesheetNode(iteration);
        TimesheetData sheetData = new TimesheetData();
        super.attachBacklogNodeToBacklogNode(sheetData, blNode);

    }
    
    @Test
    public void testLinkBacklogs() {
        Product product = new Product();
        product.setId(1);
        Project project = new Project();
        project.setId(2);
        project.setParent(product);
        Iteration iteration = new Iteration();
        iteration.setId(3);
        iteration.setParent(project);
        BacklogTimesheetNode iterationNode = new BacklogTimesheetNode(iteration);
        TimesheetData sheetData = new TimesheetData();
        sheetData.addNode(iterationNode);
        super.linkBacklogs(sheetData);
        assertNotNull(sheetData.getBacklogNode(1));
        assertNotNull(sheetData.getBacklogNode(2));
        assertTrue(sheetData.getBacklogNode(1).getBacklog() instanceof Product);
        assertTrue(sheetData.getBacklogNode(2).getBacklog() instanceof Project);
        assertTrue(sheetData.getBacklogNode(3).getBacklog() instanceof Iteration);
        assertEquals(iterationNode, sheetData.getBacklogNode(2).getBacklogNodes().get(0));
        BacklogTimesheetNode projectNode = sheetData.getBacklogNode(2);
        BacklogTimesheetNode productNode = sheetData.getBacklogNode(1);
        assertTrue(productNode.getBacklogNodes().contains(projectNode));
        assertTrue(projectNode.getBacklogNodes().contains(iterationNode));
    }
}
