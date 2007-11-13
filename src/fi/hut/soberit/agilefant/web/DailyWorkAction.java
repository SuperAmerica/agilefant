//TODO: re-factor this class so that the business layer is used and
//      the backlogitems are retrieved only once from the database

package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.BacklogValueInjector;

public class DailyWorkAction extends ActionSupport {
    private static final long serialVersionUID = 5732278003634700787L;

    private ArrayList<Iteration> iterations;

    private int bliIndex = 0;

    private int effLeftIndex = 0;

    private HashMap<Iteration, ArrayList<BacklogItem>> bliMap;

    private HashMap<Iteration, AFTime> effortLeftMap;

    private IterationDAO iterationDAO;

    private BacklogItemDAO backlogItemDAO;

    private TaskEventDAO taskEventDAO;

    private User user;

    private UserBusiness userBusiness;

    private int userId;

    private List<BacklogItem> backlogItemsForUserInProgress;

    private List<User> userList;

    private void generateIterations() {
        bliMap = new HashMap<Iteration, ArrayList<BacklogItem>>();
        effortLeftMap = new HashMap<Iteration, AFTime>();
        iterations = new ArrayList<Iteration>();

        if (user == null) {
            userId = SecurityUtil.getLoggedUser().getId();
        }

        Collection<Iteration> ongoingIterations = getOngoingIterations();
        Iterator<Iteration> iIt = ongoingIterations.iterator();
        while (iIt.hasNext()) {
            Iteration currentIteration = iIt.next();
            Collection<BacklogItem> blItems = currentIteration
                    .getSortedBacklogItems();
            Iterator<BacklogItem> bliIt = blItems.iterator();
            ArrayList<BacklogItem> unfinishedBlis = new ArrayList<BacklogItem>();
            while (bliIt.hasNext()) {
                BacklogItem currentBli = bliIt.next();
                if (currentBli.getAssignee() != null
                        && currentBli.getAssignee().getId() == userId
                        && currentBli.getPlaceHolder().getStatus() != fi.hut.soberit.agilefant.model.TaskStatus.DONE) {
                    unfinishedBlis.add(currentBli);
                }
            }

            if (!unfinishedBlis.isEmpty()) {
                BacklogValueInjector.injectMetrics(currentIteration,
                        currentIteration.getStartDate(), taskEventDAO,
                        backlogItemDAO);
                AFTime effLeft = new AFTime("0");
                Iterator<BacklogItem> iter = unfinishedBlis.iterator();
                while (iter.hasNext()) {
                    BacklogItem bli = iter.next();
                    effLeft.add(bli.getTotalEffortLeft());
                }
                bliMap.put(currentIteration, unfinishedBlis);
                effortLeftMap.put(currentIteration, effLeft);
                iterations.add(currentIteration);
            }
        }
        return;
    }

    @Override
    public String execute() throws Exception {
        if (userId == 0) {
            userId = SecurityUtil.getLoggedUserId();
        }

        user = userBusiness.getUser(userId);

        generateIterations();

        backlogItemsForUserInProgress = userBusiness
                .getBacklogItemsInProgress(user);

        userList = userBusiness.getAllUsers();

        return super.execute();
    }

    public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
        this.taskEventDAO = taskEventDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    private Collection<Iteration> getOngoingIterations() {
        return iterationDAO.getOngoingIterations();
    }

    public Collection<Iteration> getIterations() {
        return iterations;
    }

    public void setIterations(ArrayList<Iteration> iterations) {
        this.iterations = iterations;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public HashMap<Iteration, ArrayList<BacklogItem>> getBliMap() {
        return bliMap;
    }

    public void setBliMap(HashMap<Iteration, ArrayList<BacklogItem>> bliMap) {
        this.bliMap = bliMap;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public List<BacklogItem> getBacklogItemsForUserInProgress() {
        return backlogItemsForUserInProgress;
    }

    public void setBacklogItemsForUserInProgress(
            List<BacklogItem> backlogItemsForUserInProgress) {
        this.backlogItemsForUserInProgress = backlogItemsForUserInProgress;
    }

    public ArrayList<BacklogItem> getBliList() {
        return bliMap.get(iterations.get(bliIndex++));
    }

    public AFTime getUserEffortLeft() {
        return effortLeftMap.get(iterations.get(effLeftIndex++));
    }

    public HashMap<Iteration, AFTime> getEffortLeftMap() {
        return effortLeftMap;
    }

    public void setEffortLeftMap(HashMap<Iteration, AFTime> effortLeftMap) {
        this.effortLeftMap = effortLeftMap;
    }

}