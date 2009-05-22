package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.BacklogItemPriorityComparator;
import fi.hut.soberit.agilefant.util.StartedItemsComparator;
import flexjson.JSONSerializer;

/**
 * 
 * @author rjokelai
 * 
 */
public class UserBusinessImpl implements UserBusiness {
    private UserDAO userDAO;
    private ProjectDAO projectDAO;
    private IterationDAO iterationDAO;
    private BacklogItemDAO backlogItemDAO;

    /** {@inheritDoc} */
    public boolean hasUserCreatedItems(User user) {
        //this might be slow
        for(BacklogItem bli : backlogItemDAO.getAll()) {
            if(bli.getCreator() != null && bli.getCreator().getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }
    
    /** {@inheritDoc} * */
    public List<BacklogItem> getBacklogItemsInProgress(User user) {
        if (user == null) {
            user = SecurityUtil.getLoggedUser();
        }
        List<BacklogItem> userItems = (List<BacklogItem>)user.getBacklogItems();
        List<BacklogItem> returnItems = new ArrayList<BacklogItem>();
        
        /*
         * Sort out the not started and done tasks and tasks from not current
         * iterations
         */
        Iterator<BacklogItem> iter = userItems.iterator();
        while (iter.hasNext()) {
            BacklogItem bli = iter.next();

            if (bli.getState() != State.NOT_STARTED
                    && bli.getState() != State.DONE) {

                returnItems.add(bli);
            }
        }

        /* Sort the list */
        Collections.sort(returnItems, new BacklogItemPriorityComparator());
        Collections.sort(returnItems, new StartedItemsComparator());

        return returnItems;
    }

    /** {@inheritDoc} */
    public Map<Backlog, List<BacklogItem>> getBacklogItemsAssignedToUser(
            User user) {
        
        /* If no user is set, get logged user */
        if (user == null) {
            user = SecurityUtil.getLoggedUser();
        }
        /* Create the returned map */
        Map<Backlog, List<BacklogItem>> bliMap = new HashMap<Backlog, List<BacklogItem>>();

        /* Get all backlog items for user in progress */
        List<BacklogItem> userItems = (List<BacklogItem>)user.getBacklogItems();

        Set<Backlog> ongoingBacklogs = new HashSet<Backlog>();
        ongoingBacklogs.addAll(projectDAO.getOngoingProjects());
        ongoingBacklogs.addAll(iterationDAO.getOngoingIterations());

        Set<Backlog> okBacklogs = new HashSet<Backlog>();
        Iterator<Backlog> iter1 = ongoingBacklogs.iterator();

        while (iter1.hasNext()) {
            Backlog bl = iter1.next();
            if (!(bl instanceof Project)
                    || ((Project) bl).getIterations().isEmpty()) {
                okBacklogs.add(bl);
            }
        }
        /* Sort the backlog items by their priorities */
        Collections.sort(userItems, new BacklogItemPriorityComparator());
        Iterator<BacklogItem> iter2 = userItems.iterator();

        while (iter2.hasNext()) {
            BacklogItem bli = iter2.next();
            Backlog backlog = bli.getBacklog();
            if (bli.getState() != State.DONE && backlog != null
                    && okBacklogs.contains(backlog)) {
                List<BacklogItem> bliList = bliMap.get(backlog);
                if (bliList == null) {
                    bliList = new ArrayList<BacklogItem>();
                }
                bliList.add(bli);
                bliMap.put(backlog, bliList);
            }
        }

        return bliMap;
    }

    /** {@inheritDoc} */
    public Map<Backlog, List<BacklogItem>> getAllBacklogItemsAssignedToUser(
            User user) {
        /* If no user is set, get logged user */
        if (user == null) {
            user = SecurityUtil.getLoggedUser();
        }
        /* Create the returned map */
        Map<Backlog, List<BacklogItem>> bliMap = new HashMap<Backlog, List<BacklogItem>>();

        /* Get all backlog items for user in progress */
        List<BacklogItem> userItems = (List<BacklogItem>)user.getBacklogItems();

        Iterator<BacklogItem> iter2 = userItems.iterator();

        while (iter2.hasNext()) {
            BacklogItem bli = iter2.next();
            Backlog backlog = bli.getBacklog();
            if (bli.getState() != State.DONE && backlog != null
                    && (backlog instanceof Iteration || backlog instanceof Project)) {
                List<BacklogItem> bliList = bliMap.get(backlog);
                if (bliList == null) {
                    bliList = new ArrayList<BacklogItem>();
                }
                bliList.add(bli);
                bliMap.put(backlog, bliList);
            }
        }

        return bliMap;
    }
    
    /** {@inheritDoc} */
    public String getAllUsersAsJSON() {
        return new JSONSerializer().serialize(userDAO.getAll());
    }
    
    /** {@inheritDoc} */
    public String getUserJSON(int userId) {
        return getUserJSON(userDAO.get(userId));
    }
    
    /** {@inheritDoc} */
    public String getUserJSON(User user) {
        return new JSONSerializer().serialize(user);
    }
    
    /** {@inheritDoc} */
    public List<User> getEnabledUsers() {       
        return userDAO.getEnabledUsers();
        
    }
    
    /** {@inheritDoc} */
    public List<User> getDisabledUsers() {
        return userDAO.getDisabledUsers();
    }

    public List<User> getAllUsers() {
        return (List<User>) userDAO.getAll();
    }

    public User getUser(String name) {
        return userDAO.getUser(name);
    }

    public User getUser(int userId) {
        return userDAO.get(userId);
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public IterationDAO getIterationDAO() {
        return iterationDAO;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public List<Backlog> getUsersBacklogs(User user) {
        Collection<Project> list = this.projectDAO.getAll();
        List<Backlog> assignedProjects = new ArrayList<Backlog>();
        
        for(Project pro : list){
            if(pro.getResponsibles().contains(user)){
                assignedProjects.add(pro);                
            }
        }
        return assignedProjects;
    }
    
    public List<Backlog> getOngoingBacklogsByUserAndInterval(int userId, Date start, Date end) {
        User user = userDAO.get(userId);
        if(user == null) {
            return null;
        }
        return getOngoingBacklogsByUserAndInterval(user,start,end);
    }

    /**
     * helper method for backlog date filtering.
     * 
     * @return true if start or end is null and the one which is not null is
     *         within the backlog time-frame.
     */
    private boolean singleDateBacklogFilter(Backlog bl, Date start, Date end) {
        if (bl.getStartDate() == null || bl.getEndDate() == null) {
            return false;
        }
        if (start == null && end != null && end.after(bl.getEndDate())) {
            return true;
        } else if (end == null && start != null
                && start.before(bl.getStartDate())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper method for backlog interval filtering.
     * 
     * @return true if either start or end stamp fits within the backlog
     *         time-frame or if backlog time-frame is within the given
     *         filter-frame.
     */
    private boolean dualDateBacklogFilter(Backlog bl, Date start, Date end) {
        if (bl.getStartDate() == null || bl.getEndDate() == null) {
            return false;
        }
        if (end.before(start)) {
            return false;
        }
        if (start.after(bl.getStartDate()) && start.before(bl.getEndDate())) {
            return true;
        }
        if (end.after(bl.getStartDate()) && end.before(bl.getEndDate())) {
            return true;
        }
        if (bl.getStartDate().after(start) && bl.getEndDate().before(end)) {
            return true;
        }
        return false;
    }

    public List<Backlog> getOngoingBacklogsByUserAndInterval(User user,
            Date start, Date end) {
        Collection<Assignment> allAssignments = user.getAssignments();
        List<Backlog> ongoingBacklogs = new ArrayList<Backlog>();
        for (Assignment ass : allAssignments) {
            Backlog bl = ass.getBacklog();
            if (bl instanceof Project) {
                if (start == null && end == null) {
                    ongoingBacklogs.add(bl);
                } else if ((start == null ^ end == null)
                        && singleDateBacklogFilter(bl, start, end)) {
                    ongoingBacklogs.add(bl);
                } else if (start != null && end != null
                        && dualDateBacklogFilter(bl, start, end)) {
                    ongoingBacklogs.add(bl);

                }
            }
        }
        return ongoingBacklogs;
    }
    
    /** {@inheritDoc} */
    public void enableUser(User user) {
        if (user != null) {
            user.setEnabled(true);
        }
    }
    
    /** {@inheritDoc} */
    public void disableUser(User user) {
        if (user != null) {
            user.setEnabled(false);
        }
    }

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }
}
