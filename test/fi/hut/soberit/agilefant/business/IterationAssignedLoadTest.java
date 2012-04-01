package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.IterationBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.AssignmentTO;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
@SuppressWarnings("unused")
public class IterationAssignedLoadTest extends MockedTestCase {

    @TestedBean
    private IterationBusinessImpl iterationBusiness;

    @Mock(strict = true)
    private IterationDAO iterationDAO;
    @Mock(strict = true)
    private TransferObjectBusiness transferObjectBusiness;
    @Mock(strict = true)
    private StoryBusiness storyBusiness;
    @Mock(strict = true)
    private HourEntryBusiness hourEntryBusiness;
    @Mock(strict = true)
    private BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    @Mock(strict = true)
    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    @Mock(strict = true)
    private IterationHistoryEntryDAO iterationHistoryEntryDAO;
    @Mock(strict = true)
    private BacklogBusiness backlogBusiness;
    @Mock(strict = true)
    private AssignmentBusiness assignmentBusiness;
    @Mock(strict = true)
    private SettingBusiness settingBusiness;
    @Mock(strict=true)
    private StoryRankBusiness storyRankBusiness;
    @Mock(strict=true)
    private TaskBusiness taskBusiness;

    private User user1;
    private User user2;
    private Assignment assign1;
    private Assignment assign2;
    private Iteration iteration;
    private Story story1;
    private Story story2;
    private Task taskInStory11;
    private Task taskInStory12;
    private Task taskInStory21;
    private Task taskInStory22;
    private Task taskWithoutStory;
    private List<Task> allTasks;
    private static float u1fraction = 100f / 110f;
    private static float u2fraction = 10f / 110f;

    @Before
    public void setupData() {
        iteration = new Iteration();
        iteration.setStartDate(new DateTime().plusDays(5));
        iteration.setEndDate(iteration.getStartDate().plusDays(10));
        iteration.setBacklogSize(new ExactEstimate(500));
        iteration.setBaselineLoad(new ExactEstimate(50));

        user1 = new User();
        user1.setId(1);

        user2 = new User();
        user2.setId(2);

        assign1 = new Assignment();
        assign1.setUser(user1);
        assign1.setId(1);
        assign1.setBacklog(iteration);
        assign1.setAvailability(100);

        assign2 = new Assignment();
        assign2.setUser(user2);
        assign2.setId(2);
        assign2.setBacklog(iteration);
        assign2.setAvailability(10);

        story1 = new Story();
        story1.setIteration(iteration);

        story2 = new Story();
        story2.setIteration(iteration);

        taskInStory11 = new Task();
        taskInStory11.setStory(story1);
        taskInStory11.setEffortLeft(new ExactEstimate(10L));

        taskInStory12 = new Task();
        taskInStory12.setStory(story1);
        taskInStory12.setEffortLeft(new ExactEstimate(100L));

        taskInStory21 = new Task();
        taskInStory21.setStory(story2);
        taskInStory21.setEffortLeft(new ExactEstimate(1000L));

        taskInStory22 = new Task();
        taskInStory22.setStory(story2);
        taskInStory22.setEffortLeft(new ExactEstimate(10000L));

        taskWithoutStory = new Task();
        taskWithoutStory.setIteration(iteration);
        taskWithoutStory.setEffortLeft(new ExactEstimate(100000L));

        Task nullTask = new Task();
        nullTask.setEffortLeft(null);

        story1.getTasks().add(taskInStory11);
        story1.getTasks().add(taskInStory12);

        story1.getTasks().add(taskInStory21);
        story1.getTasks().add(taskInStory22);

        iteration.getStories().add(story1);
        iteration.getStories().add(story2);
        iteration.getTasks().add(taskWithoutStory);
        iteration.getTasks().add(nullTask);

        allTasks = Arrays.asList(taskInStory11, taskInStory12, taskInStory21,
                taskInStory22, taskWithoutStory);
    }

    private AssignmentTO findById(Set<AssignmentTO> assignments, int id) {
        for (AssignmentTO ass : assignments) {
            if (ass.getId() == id) {
                return ass;
            }
        }
        return null;
    }

    @Test
    @DirtiesContext
    public void testNoAssignees_noResponsbilities() {
        expect(iterationDAO.getAllTasksForIteration(iteration)).andReturn(
                allTasks);
        Set<AssignmentTO> actual;
        expect(backlogBusiness.calculateBacklogTimeframePercentageLeft(iteration)).andReturn(100.0f);
        expect(backlogBusiness.daysLeftInSchedulableBacklog(iteration)).andReturn(Days.days(10));
        replayAll();
        actual = iterationBusiness.calculateAssignedLoadPerAssignee(iteration);
        verifyAll();
        assertEquals(0, actual.size());
    }

    @Test
    @DirtiesContext
    public void testNoAssignees_withResponsbilities() {
        story1.getResponsibles().add(user1);
        taskInStory21.getResponsibles().add(user2);
        expect(iterationDAO.getAllTasksForIteration(iteration)).andReturn(
                allTasks);
        expect(backlogBusiness.calculateBacklogTimeframePercentageLeft(iteration)).andReturn(100.0f);
        expect(backlogBusiness.daysLeftInSchedulableBacklog(iteration)).andReturn(Days.days(10));
        Set<AssignmentTO> actual;
        replayAll();
        actual = iterationBusiness.calculateAssignedLoadPerAssignee(iteration);
        verifyAll();
        assertEquals(2, actual.size());
        assertNotNull(this.findById(actual, -1));
        assertNotNull(this.findById(actual, -2));
    }

    @Test
    @DirtiesContext
    public void testOneAssignee() {
        story1.getResponsibles().add(user1);
        taskInStory21.getResponsibles().add(user2);
        taskInStory22.getResponsibles().add(user1);
        taskWithoutStory.getResponsibles().add(user1);
        iteration.getAssignments().add(assign1);
        expect(iterationDAO.getAllTasksForIteration(iteration)).andReturn(
                allTasks);
        expect(backlogBusiness.calculateBacklogTimeframePercentageLeft(iteration)).andReturn(1.0f);
        expect(backlogBusiness.daysLeftInSchedulableBacklog(iteration)).andReturn(Days.days(10));
        Set<AssignmentTO> actual;
        replayAll();
        actual = iterationBusiness.calculateAssignedLoadPerAssignee(iteration);
        verifyAll();
        AssignmentTO ass = this.findById(actual, 1);
        assertEquals(2, actual.size());
        assertEquals(taskInStory11.getEffortLeft().longValue()
                + taskInStory12.getEffortLeft().longValue()
                + taskInStory22.getEffortLeft().longValue()
                + taskWithoutStory.getEffortLeft().longValue(), ass
                .getAssignedLoad().longValue());
        assertEquals(iteration.getBacklogSize().longValue(), ass
                .getAvailableWorktime().longValue());
    }

    @Test
    @DirtiesContext
    public void testOneAssignee_someUnassigned() {
        story1.getResponsibles().add(user1);
        taskInStory22.getResponsibles().add(user1);
        taskWithoutStory.getResponsibles().add(user1);
        iteration.getAssignments().add(assign1);
        expect(iterationDAO.getAllTasksForIteration(iteration)).andReturn(
                allTasks);
        expect(backlogBusiness.calculateBacklogTimeframePercentageLeft(iteration)).andReturn(100.0f);
        expect(backlogBusiness.daysLeftInSchedulableBacklog(iteration)).andReturn(Days.days(5));
        Set<AssignmentTO> actual;
        replayAll();
        actual = iterationBusiness.calculateAssignedLoadPerAssignee(iteration);
        verifyAll();
        assertEquals(1, actual.size());
        AssignmentTO assignment = this.findById(actual, 1);
        assertEquals(taskInStory11.getEffortLeft().longValue()
                + taskInStory12.getEffortLeft().longValue()
                + taskInStory22.getEffortLeft().longValue()
                + taskWithoutStory.getEffortLeft().longValue(), assignment
                .getAssignedLoad().longValue());
        assertEquals(taskInStory21.getEffortLeft().longValue(), assignment
                .getUnassignedLoad().longValue());
    }

    @Test
    @DirtiesContext
    public void testBothAssignees() {
        iteration.getAssignments().add(assign1);
        iteration.getAssignments().add(assign2);

        story1.getResponsibles().add(user1);
        taskInStory21.getResponsibles().add(user2);
        taskInStory22.getResponsibles().add(user1);
        taskWithoutStory.getResponsibles().add(user2);

        expect(iterationDAO.getAllTasksForIteration(iteration)).andReturn(
                allTasks);
        expect(backlogBusiness.calculateBacklogTimeframePercentageLeft(iteration)).andReturn(1.0f);
        expect(backlogBusiness.daysLeftInSchedulableBacklog(iteration)).andReturn(Days.days(5));
        Set<AssignmentTO> actual;
        replayAll();
        actual = iterationBusiness.calculateAssignedLoadPerAssignee(iteration);
        verifyAll();
        assertEquals(2, actual.size());

        AssignmentTO u1 = this.findById(actual, 1);
        AssignmentTO u2 = this.findById(actual, 2);

        assertEquals(10110l, u1.getAssignedLoad().longValue());
        assertEquals(0L, u1.getUnassignedLoad().longValue());
        assertEquals(101000l, u2.getAssignedLoad().longValue());
        assertEquals(0L, u2.getUnassignedLoad().longValue());

        assertEquals(45, u2.getAvailableWorktime().longValue());
        assertEquals(455, u1.getAvailableWorktime().longValue());
        
        assertEquals(10145l, u1.getTotalLoad().longValue());
        assertEquals(101035l, u2.getTotalLoad().longValue());
        
        assertEquals(2230, u1.getLoadPercentage());
        assertEquals(224522, u2.getLoadPercentage());
    }

    @Test
    @DirtiesContext
    public void testBothAssignees_unassigned() {
        iteration.getAssignments().add(assign1);
        iteration.getAssignments().add(assign2);
        iteration.setBaselineLoad(null);
        assign1.setPersonalLoad(null);

        expect(iterationDAO.getAllTasksForIteration(iteration)).andReturn(
                allTasks);
        expect(backlogBusiness.calculateBacklogTimeframePercentageLeft(iteration)).andReturn(100.0f);
        expect(backlogBusiness.daysLeftInSchedulableBacklog(iteration)).andReturn(Days.days(5));
        Set<AssignmentTO> actual;
        replayAll();
        actual = iterationBusiness.calculateAssignedLoadPerAssignee(iteration);
        verifyAll();
        assertEquals(2, actual.size());

        AssignmentTO u1 = this.findById(actual, 1);
        AssignmentTO u2 = this.findById(actual, 2);

        assertEquals(0L, u1.getAssignedLoad().longValue());
        assertEquals(101009L, u1.getUnassignedLoad().longValue());
        assertEquals(0L, u2.getAssignedLoad().longValue());
        assertEquals(10100L, u2.getUnassignedLoad().longValue());

    }
}
