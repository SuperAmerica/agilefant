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
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Schedulable;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;
import fi.hut.soberit.agilefant.transfer.ScheduleStatus;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

@Service("transferObjectBusiness")
@Transactional(readOnly = true)
public class TransferObjectBusinessImpl implements TransferObjectBusiness {

    @Autowired
    private BacklogBusiness backlogBusiness;
    
    @Autowired
    private ProjectBusiness projectBusiness;
    
    @Autowired
    private StoryBusiness storyBusiness;
    
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    
    @Autowired
    private UserBusiness userBusiness;
    
    @Autowired
    private TeamBusiness teamBusiness;
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Collection<StoryTO> constructBacklogDataWithUserData(
            Backlog backlog, Collection<User> assignedUsers) {
        Collection<StoryTO> iterationStories = new ArrayList<StoryTO>();
        
        for (Story story : backlog.getStories()) {
            StoryTO storyTO = this.constructStoryTO(story, assignedUsers);
            storyTO.setTasks(new ArrayList<Task>());
            
            for (Task task : story.getTasks()) {
                TaskTO taskTO = this.constructTaskTO(task, assignedUsers);
                taskTO.setEffortSpent(hourEntryBusiness.calculateSum(taskTO.getHourEntries()));
                storyTO.getTasks().add(taskTO);
            }
            iterationStories.add(storyTO);
        }
        return iterationStories;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public TaskTO constructTaskTO(Task task, Collection<User> assignedUsers) {
        TaskTO taskTO = new TaskTO(task);
        taskTO.setEffortSpent(hourEntryBusiness.calculateSum(taskTO.getHourEntries()));
        
        for (User responsible : taskTO.getResponsibles()) {
            ResponsibleContainer rc
                = new ResponsibleContainer(responsible,
                        assignedUsers.contains(responsible));
            taskTO.getUserData().add(rc);
        }
        
        return taskTO;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public StoryTO constructStoryTO(Story story, Collection<User> assignedUsers) {
        StoryTO storyTO = new StoryTO(story);
        
        for (User responsible : storyTO.getResponsibles()) {
            ResponsibleContainer rc
                = new ResponsibleContainer(responsible,
                        assignedUsers.contains(responsible));
            storyTO.getUserData().add(rc);
        }
        
        return storyTO;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public List<AutocompleteDataNode> constructUserAutocompleteData() {
        Collection<User> allUsers = this.userBusiness.retrieveAll();
        List<AutocompleteDataNode> autocompleteData = new ArrayList<AutocompleteDataNode>();
        for(User user : allUsers) {
            AutocompleteDataNode curNode = new AutocompleteDataNode(User.class, user.getId(), user.getFullName(), user.isEnabled());
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
            autocompleteData.add(node);
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
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public TaskTO constructTaskTO(Task task) {
        Collection<User> assignedUsers;
        if (task.getStory() != null) {
            assignedUsers = storyBusiness.getStorysProjectResponsibles(task.getStory());
        }
        else {
            assignedUsers = projectBusiness.getAssignedUsers((Project)task.getIteration().getParent());
        }
            
        return this.constructTaskTO(task, assignedUsers);
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
    

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
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
    
}
