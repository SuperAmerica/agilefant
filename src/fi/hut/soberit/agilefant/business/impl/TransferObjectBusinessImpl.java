package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Schedulable;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;
import fi.hut.soberit.agilefant.transfer.ScheduleStatus;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO.TaskClass;

@Service("transferObjectBusiness")
@Transactional(readOnly = true)
public class TransferObjectBusinessImpl implements TransferObjectBusiness {

    @Autowired
    private BacklogBusiness backlogBusiness;
    
    @Autowired
    private ProductBusiness productBusiness;
    
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    
    @Autowired
    private UserBusiness userBusiness;
    
    @Autowired
    private TeamBusiness teamBusiness;

    @Autowired
    private IterationBusiness iterationBusiness;
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Collection<StoryTO> constructBacklogData(
            Backlog backlog) {
        Collection<StoryTO> iterationStories = new ArrayList<StoryTO>();
        
        for (Story story : backlog.getStories()) {
            StoryTO storyTO = this.constructStoryTO(story);
            storyTO.setTasks(new HashSet<Task>());
            
            for (Task task : story.getTasks()) {
                TaskTO taskTO = this.constructTaskTO(task);
                taskTO.setEffortSpent(hourEntryBusiness.calculateSum(taskTO.getHourEntries()));
                storyTO.getTasks().add(taskTO);
            }
            iterationStories.add(storyTO);
        }
        return iterationStories;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public TaskTO constructTaskTO(Task task) {
        TaskTO taskTO = new TaskTO(task);
        taskTO.setEffortSpent(hourEntryBusiness.calculateSum(taskTO.getHourEntries()));       
        return taskTO;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public StoryTO constructStoryTO(Story story) {
        return new StoryTO(story);
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public List<AutocompleteDataNode> constructUserAutocompleteData() {
        Collection<User> allUsers = this.userBusiness.retrieveAll();
        List<AutocompleteDataNode> autocompleteData = new ArrayList<AutocompleteDataNode>();
        for(User user : allUsers) {
            AutocompleteDataNode curNode = new AutocompleteDataNode(User.class,
                    user.getId(), user.getFullName(), user.isEnabled());
            curNode.setMatchedString(user.getFullName() + " " + user.getLoginName());
            curNode.setOriginalObject(user);
            autocompleteData.add(curNode);
        }
        return autocompleteData;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public List<AutocompleteDataNode> constructTeamAutocompleteData() {
        Collection<Team> allTeams = this.teamBusiness.retrieveAll();
        List<AutocompleteDataNode> autocompleteData = new ArrayList<AutocompleteDataNode>();
        for(Team team : allTeams) {
            Set<Integer> userIds = new HashSet<Integer>();
            for(User user : team.getUsers()) {
                userIds.add(user.getId());
            }
            AutocompleteDataNode curNode = new AutocompleteDataNode(Team.class, team.getId(), team.getName(), userIds);
            curNode.setMatchedString(team.getName());
            autocompleteData.add(curNode);
        }
        return autocompleteData;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public List<AutocompleteDataNode> constructBacklogAutocompleteData() {
        Collection<Backlog> allBacklogs = this.backlogBusiness.retrieveAll();
        List<AutocompleteDataNode> autocompleteData = new ArrayList<AutocompleteDataNode>();
        for (Backlog blog : allBacklogs) {
            String name = recurseBacklogNameWithParents(blog);
            AutocompleteDataNode node = new AutocompleteDataNode(Backlog.class,
                    blog.getId(), name);
            node.setMatchedString(name);
            autocompleteData.add(node);
            node.setOriginalObject(blog);
        }
        return autocompleteData; 
    }
    
    private String recurseBacklogNameWithParents(Backlog blog) {
        Backlog parent = blog.getParent();
        String name = blog.getName();
        while (parent != null) {
            name = parent.getName() + " > " + name;
            parent = parent.getParent();
        }
        return name;
    }
    
    @Transactional(readOnly = true)
    public List<AutocompleteDataNode> constructProductAutocompleteData() {
        List<AutocompleteDataNode> nodes = new ArrayList<AutocompleteDataNode>();
        for (Product prod : this.productBusiness.retrieveAll()) {
            AutocompleteDataNode node = new AutocompleteDataNode(Product.class,
                    prod.getId(), prod.getName());
            node.setOriginalObject(prod);
            nodes.add(node);
        }
        return nodes;
    }
    
   
    @Transactional(readOnly = true)
    public ScheduleStatus getBacklogScheduleStatus(Backlog backlog) {
        if (backlog instanceof Product) {
            return ScheduleStatus.ONGOING;
        }
        Schedulable blog = (Schedulable)backlog;
        Interval interval = new Interval(blog.getStartDate(), blog.getEndDate());
        if (interval.isBeforeNow()) {
            return ScheduleStatus.PAST;
        }
        else if (interval.isAfterNow()) {
            return ScheduleStatus.FUTURE;
        }
        return ScheduleStatus.ONGOING;
    }
    
    @Transactional(readOnly = true)
    public List<AutocompleteDataNode> constructCurrentIterationAutocompleteData() {
        Collection<Iteration> currentAndFutureIterations = this.iterationBusiness.retrieveCurrentAndFutureIterations();
        List<AutocompleteDataNode> autocompleteData = new ArrayList<AutocompleteDataNode>();
        for (Backlog blog : currentAndFutureIterations) {
            String name = recurseBacklogNameWithParents(blog);
            AutocompleteDataNode node = new AutocompleteDataNode(Backlog.class,
                    blog.getId(), name);
            autocompleteData.add(node);
        }
        return autocompleteData; 
    }

    /** {@inheritDoc} */

    private void fillInDailyWorkItemContextData(DailyWorkTaskTO transferObj) {
        Backlog backlog = null;
        Story story = transferObj.getStory();

        String contextName = null;
        int parentStoryId = 0;
        int backlogId = 0;

        if (story != null) {
            parentStoryId = story.getId();

            backlog = story.getBacklog();
            if (backlog != null) {
                contextName  = "" + String.valueOf(backlog.getName()) + " > " + String.valueOf(story.getName());
                backlogId = backlog.getId();
            }
        }
        else {
            backlog = transferObj.getIteration();
            if (backlog != null) {
                contextName  = "" + String.valueOf(backlog.getName());
                backlogId = backlog.getId();
            }
        }
        
        transferObj.setBacklogId(backlogId);
        transferObj.setContextName(contextName);
        transferObj.setParentStoryId(parentStoryId);
    }
    
    @Transactional(readOnly = true)
    public DailyWorkTaskTO constructUnqueuedDailyWorkTaskTO(Task task) {
        DailyWorkTaskTO toReturn = new DailyWorkTaskTO(task);
        
        fillInDailyWorkItemContextData(toReturn);

        toReturn.setWorkQueueRank(-1);
        toReturn.setTaskClass(TaskClass.ASSIGNED);

        return toReturn;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public DailyWorkTaskTO constructQueuedDailyWorkTaskTO(WhatsNextEntry entry) {
        Task task = entry.getTask();
        DailyWorkTaskTO toReturn = new DailyWorkTaskTO(task);
        fillInDailyWorkItemContextData(toReturn);
        toReturn.setWorkQueueRank(entry.getRank());

        Collection<User> responsibles = toReturn.getResponsibles();
        if (responsibles != null && responsibles.contains(entry.getUser())) {
            toReturn.setTaskClass(TaskClass.NEXT_ASSIGNED);
        }
        else {
            toReturn.setTaskClass(TaskClass.NEXT);
        }
        
        return toReturn;
    }
    
    /*
     * GETTERS AND SETTERS
     */
    
    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public void setTeamBusiness(TeamBusiness teamBusiness) {
        this.teamBusiness = teamBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setIterationBusiness(IterationBusiness iterationBusiness) {
        this.iterationBusiness = iterationBusiness;
    }

    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }
}
