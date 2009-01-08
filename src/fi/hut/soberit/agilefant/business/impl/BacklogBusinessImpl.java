package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationGoalBusiness;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.HistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BacklogComparator;
import fi.hut.soberit.agilefant.util.BacklogLoadData;
import fi.hut.soberit.agilefant.util.BacklogMetrics;
import fi.hut.soberit.agilefant.util.EffortSumData;
import flexjson.JSONSerializer;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogBusinessImpl implements BacklogBusiness {

    private BacklogItemDAO backlogItemDAO;

    private HistoryBusiness historyBusiness;

    private BacklogDAO backlogDAO;

    private UserDAO userDAO;

    private AssignmentDAO assignmentDAO;

    private IterationGoalDAO iterationGoalDAO;

    private HourEntryBusiness hourEntryBusiness;

    private IterationGoalBusiness iterationGoalBusiness;

    private BusinessThemeBusiness businessThemeBusiness;

    private ProductDAO productDAO;

    private BusinessThemeDAO businessThemeDAO;

    /** {@inheritDoc} */
    public Backlog getBacklog(int backlogId) throws ObjectNotFoundException {
        Backlog backlog;
        if ((backlog = backlogDAO.get(backlogId)) == null) {
            throw new ObjectNotFoundException();
        }
        return backlog;
    }

    // @Override
    public void deleteMultipleItems(int backlogId, int[] backlogItemIds)
            throws ObjectNotFoundException, OperationNotPermittedException {
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog == null) {
            throw new ObjectNotFoundException("Backlog id " + backlogId
                    + " was invalid.");
        }
        for (int id : backlogItemIds) {
            if(backlogItemDAO.backlogItemChildren(id).size() > 0)
                throw new OperationNotPermittedException("Backlog item " +
                        backlogItemDAO.get(id).getName() + " has children, cannot delete.");
        }
        for (int id : backlogItemIds) {
            Collection<BacklogItem> items = backlog.getBacklogItems();
            Iterator<BacklogItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                BacklogItem item = iterator.next();
                if (item.getId() == id) {
                    // Remove all hour entries inside the backlogItem in
                    // question
                    hourEntryBusiness.removeHourEntriesByParent(item);
                    iterator.remove();
                    backlogItemDAO.remove(id);
                }
            }
        }
        historyBusiness.updateBacklogHistory(backlog.getId());
    }

    public BacklogItem createBacklogItemToBacklog(int backlogId) {
        BacklogItem backlogItem = new BacklogItem();
        backlogItem = new BacklogItem();
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog == null)
            return null;
        backlogItem.setBacklog(backlog);
        backlog.getBacklogItems().add(backlogItem);
        return backlogItem;
    }

    /**
     * {@inheritDoc}
     */
    public void changePriorityOfMultipleItems(int[] backlogItemIds,
            Priority priority) throws ObjectNotFoundException {

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);
            if (bli == null) {
                throw new ObjectNotFoundException(
                        "Could not change priority. Object with id " + id
                                + " was not found.");
            }
            bli.setPriority(priority);
        }
    }

    /** {@inheritDoc} */
    public void changeStateOfMultipleItems(int[] backlogItemIds, State state)
            throws ObjectNotFoundException {

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);

            if (bli == null) {
                throw new ObjectNotFoundException(
                        "Could not change priority. Object with id " + id
                                + "was not found.");
            }

            bli.setState(state);
            // If State is done, effort left is 0h.
            if (state == State.DONE)
                bli.setEffortLeft(new AFTime(0));
        }
    }

    /** {@inheritDoc} */
    public void changeIterationGoalOfMultipleItems(int[] backlogItemIds,
            int iterationGoalId) throws ObjectNotFoundException {

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);

            if (bli == null) {
                throw new ObjectNotFoundException(
                        "Could not change iteration goal. Object with id " + id
                                + "was not found.");
            }
            if (iterationGoalId == -2) {
                bli.setIterationGoal(null);
            } else {
                bli.setIterationGoal(iterationGoalDAO.get(iterationGoalId));
            }
        }

    }

    /** {@inheritDoc} */
    public void setResponsiblesForMultipleBacklogItems(int[] backlogItemIds,
            Set<Integer> responsibleIds) throws ObjectNotFoundException {

        // Generate the list of responsibles
        Set<User> users = new HashSet<User>();

        for (int uid : responsibleIds) {
            User user = userDAO.get(uid);
            users.add(user);
        }

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);

            if (bli == null) {
                throw new ObjectNotFoundException(
                        "Could not change responsibles. Backlog item with id "
                                + id + "was not found.");
            }

            bli.setResponsibles(users);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void changeBusinessThemesOfMultipleBacklogItems(
            int[] backlogItemIds, Set<Integer> businessThemeIds)
            throws ObjectNotFoundException {

        ArrayList<Integer> notFoundItems = new ArrayList<Integer>();
        
        // Generate the list of business themes
        Set<BusinessTheme> themes = new HashSet<BusinessTheme>();

        for (int bid : businessThemeIds) {
            BusinessTheme theme = businessThemeDAO.get(bid);
            if (theme != null) {
                themes.add(theme);
            } else {
                notFoundItems.add(bid);
            }
            
        }

        // go through all selected BLI's
        for (int currentBacklogId : backlogItemIds) {
            BacklogItem currentBacklog = backlogItemDAO.get(currentBacklogId);

            if (currentBacklog != null) {
                currentBacklog.setBusinessThemes(themes);
            } else {
                notFoundItems.add(currentBacklogId);
            }
        }
        
        
        
        if (!notFoundItems.isEmpty()) {
            Collections.sort(notFoundItems);
            String exceptionMessage = "Items with ids: ";
                for (Integer notFoundItemId : notFoundItems) {
                    exceptionMessage += "" + notFoundItemId +" ";
                }
            exceptionMessage += "were not found.";
            throw new ObjectNotFoundException(exceptionMessage);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void moveMultipleBacklogItemsToBacklog(int backlogItemIds[],
            int targetBacklogId) throws ObjectNotFoundException {
        Backlog targetBacklog = backlogDAO.get(targetBacklogId);

        // Store source backlogs of the backlog items to be able to update their
        // history data.

        Set<Integer> sourceBacklogIds = new HashSet<Integer>();

        if (targetBacklog == null) {
            throw new ObjectNotFoundException("Target backlog with id: "
                    + targetBacklogId + " was not found.");
        }

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);
            if (bli == null) {
                throw new ObjectNotFoundException("Backlog item with id: " + id
                        + " was not found.");
            }
            Backlog sourceBacklog = bli.getBacklog();

            if (sourceBacklog.getId() != targetBacklog.getId()) {

                // Store the source backlog ids into the set
                sourceBacklogIds.add(bli.getBacklog().getId());

                // Set originalestimate to current effortleft
                // bli.setOriginalEstimate(bli.getEffortLeft());

                // Remove iteration goal
                if (bli.getIterationGoal() != null) {
                    bli.getIterationGoal().getBacklogItems().remove(bli);
                    bli.setIterationGoal(null);
                }

                // if item is moved under another product, remove themes
                if (!isUnderSameProduct(bli.getBacklog(), targetBacklog)) {
                    if (bli.getBusinessThemes() != null) {
                        bli.getBusinessThemes().clear();
                    }
                }

                // Set backlog item's backlog to target backlog
                bli.setBacklog(targetBacklog);
                backlogItemDAO.store(bli);

                // Remove BLI from source backlog
                sourceBacklog.getBacklogItems().remove(bli);

                // Store source backlog
                backlogDAO.store(sourceBacklog);

                // Add backlog item to new Backlog's backlog item list
                targetBacklog.getBacklogItems().add(bli);
            }
        }

        backlogDAO.store(targetBacklog);

        // Update history data for source backlogs
        for (Integer sourceBacklogId : sourceBacklogIds) {
            historyBusiness.updateBacklogHistory(sourceBacklogId);

        }

        // Update history data for target backlog
        historyBusiness.updateBacklogHistory(targetBacklog.getId());
    }

    /** {@inheritDoc} */
    public EffortSumData getEffortLeftSum(Collection<BacklogItem> bliList) {
        EffortSumData data = new EffortSumData();
        AFTime hours = new AFTime(0);
        int nonEstimatedBLIs = 0;
        for (BacklogItem bli : bliList) {
            if (bli.getEffortLeft() == null)
                nonEstimatedBLIs++;
            else
                hours.add(bli.getEffortLeft());
        }
        data.setEffortHours(hours);
        data.setNonEstimatedItems(nonEstimatedBLIs);
        return data;
    }

    /** {@inheritDoc} */
    public EffortSumData getEffortLeftResponsibleDividedSum(
            Collection<BacklogItem> bliList) {
        EffortSumData data = new EffortSumData();
        AFTime hours = new AFTime(0);
        int nonEstimatedBLIs = 0;

        for (BacklogItem bli : bliList) {
            if (bli.getEffortLeft() != null) {
                if (bli.getResponsibles() != null) {
                    if (bli.getResponsibles().size() != 0) {
                        hours.add(new AFTime(bli.getEffortLeft().getTime()
                                / bli.getResponsibles().size()));
                    } else {
                        hours.add(bli.getEffortLeft());
                    }
                } else {
                    hours.add(bli.getEffortLeft());
                }
            } else {
                nonEstimatedBLIs++;
            }
        }
        data.setEffortHours(hours);
        data.setNonEstimatedItems(nonEstimatedBLIs);
        return data;
    }

    /** {@inheritDoc} */
    public EffortSumData getOriginalEstimateSum(Collection<BacklogItem> bliList) {
        EffortSumData data = new EffortSumData();
        AFTime hours = new AFTime(0);
        int nonEstimatedBLIs = 0;
        for (BacklogItem bli : bliList) {
            if (bli.getOriginalEstimate() == null)
                nonEstimatedBLIs++;
            else
                hours.add(bli.getOriginalEstimate());
        }
        data.setEffortHours(hours);
        data.setNonEstimatedItems(nonEstimatedBLIs);
        return data;
    }

    public void setAssignments(int[] selectedUserIds,
            Map<String, Assignment> assignmentData, Backlog backlog) {
        if (backlog != null) {
            // Edit project assignments: remove all assignments, then create
            // some.
            Collection<Assignment> oldAssignments = backlog.getAssignments();
            for (Assignment ass : oldAssignments) {
                assignmentDAO.remove(ass);
            }
            Collection<User> users = getUsers(backlog, true);
            for (User user : users) {
                user.getAssignments().removeAll(oldAssignments);
                userDAO.store(user);
            }
            backlog.getAssignments().clear();
            backlogDAO.store(backlog);

            if (selectedUserIds != null) {
                for (int id : selectedUserIds) {

                    User user = userDAO.get(id);
                    if (user != null) {
                        Assignment assignment = new Assignment(user, backlog);
                        if (assignmentData != null) {
                            Assignment ass = assignmentData.get(id + "");
                            if (ass != null) {
                                assignment.setDeltaOverhead(ass
                                        .getDeltaOverhead());
                            }
                        }
                        user.getAssignments().add(assignment);
                        backlog.getAssignments().add(assignment);
                        assignmentDAO.store(assignment);
                        userDAO.store(user);
                        backlogDAO.store(backlog);
                    }
                }
            }
        }
    }

    public void removeAssignments(User user) {
        if (user != null) {
            Collection<Assignment> assignments = assignmentDAO.getAll();
            for (Assignment ass : assignments) {
                if (ass.getUser().getId() == user.getId()) {
                    user.getAssignments().remove(ass);
                    ass.getBacklog().getAssignments().remove(ass);
                    userDAO.store(user);
                    backlogDAO.store(ass.getBacklog());
                    assignmentDAO.remove(ass);
                }
            }
        }
    }

    public Collection<User> getUsers(Backlog backlog, boolean areAssigned) {
        Collection<User> users;
        Collection<Assignment> assignments = backlog.getAssignments();
        users = new HashSet<User>();
        for (Assignment ass : assignments) {
            users.add(ass.getUser());
        }
        if (areAssigned)
            return users;
        else {
            Collection<User> allUsers = userDAO.getAll();
            allUsers.removeAll(users);
            return allUsers;
        }

    }

    /** {@inheritDoc} */
    @SuppressWarnings("deprecation")
    public int getWeekdaysLeftInBacklog(Backlog backlog, Date from) {
        Date startDate = new Date(0);
        Date endDate = new Date(0);
        GregorianCalendar current = new GregorianCalendar();
        int numberOfWeekdays = 0;

        // Backlog shouldn't be a product
        if (backlog instanceof Product) {
            return 0;
        } else if (backlog instanceof Project) {
            startDate = (Date) ((Project) backlog).getStartDate().clone();
            endDate = (Date) ((Project) backlog).getEndDate().clone();
        } else {
            startDate = (Date) ((Iteration) backlog).getStartDate().clone();
            endDate = (Date) ((Iteration) backlog).getEndDate().clone();
        }

        // If project or iteration is past
        if (from.after(endDate)) {
            return 0;
        }

        // Check, which is later, start date or from date
        if (from.after(startDate)) {
            current.setTime(from);
        } else {
            current.setTime(startDate);
        }

        Date currentTime = current.getTime();

        // Add 1 to endDate to correct the time offset
        endDate.setDate(endDate.getDate() + 1);

        // Loop through the dates
        while (currentTime.before(endDate)) {
            if (current.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SUNDAY
                    && current.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.SATURDAY) {
                numberOfWeekdays++;
            }
            current.add(GregorianCalendar.DATE, 1);
            currentTime = current.getTime();
        }

        return numberOfWeekdays;

    }

    /** {@inheritDoc} */
    public int getNumberOfDaysForBacklogOnWeek(Backlog backlog, Date time) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        cal.setTime(time);

        while (cal.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.MONDAY) {
            cal.add(GregorianCalendar.DATE, -1);
        }

        return getNumberOfDaysLeftForBacklogOnWeek(backlog, cal.getTime());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("deprecation")
    public int getNumberOfDaysLeftForBacklogOnWeek(Backlog backlog, Date time) {
        Date startDate = new Date();
        Date endDate = new Date();

        // Should not be a product
        if (backlog instanceof Product) {
            return 0;
        } else if (backlog instanceof Project) {
            startDate = (Date) ((Project) backlog).getStartDate().clone();
            endDate = (Date) ((Project) backlog).getEndDate().clone();
        } else if (backlog instanceof Iteration) {
            startDate = (Date) ((Iteration) backlog).getStartDate().clone();
            endDate = (Date) ((Iteration) backlog).getEndDate().clone();
        }

        // Set the time to start from
        GregorianCalendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        cal.setTime(time);
        cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
        cal.set(GregorianCalendar.MINUTE, 0);
        cal.set(GregorianCalendar.SECOND, 2);

        // Get the number of week
        int numberOfWeek = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        int numberOfDays = 0;

        // Set the startdate to be at the start of the day
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);

        // Loop through the week
        while (cal.get(GregorianCalendar.WEEK_OF_YEAR) == numberOfWeek) {
            // Break the loop on weekend
            if (cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY
                    || cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
                break;
            }

            // Check, if the date is in the backlog's timeframe
            if (startDate.before(cal.getTime()) && endDate.after(cal.getTime())) {
                numberOfDays++;
            }

            cal.add(GregorianCalendar.DATE, 1);
        }

        return numberOfDays;
    }

    /** {@inheritDoc} */
    public BacklogLoadData calculateBacklogLoadData(Backlog backlog, User user,
            Date from, int numberOfWeeks) {

        // Create the new data storage
        BacklogLoadData data = new BacklogLoadData();

        data.setBacklog(backlog);

        Collection<BacklogItem> bliList = new ArrayList<BacklogItem>();

        // Loop through the backlog items
        for (BacklogItem bli : backlog.getBacklogItems()) {
            if (bli.getResponsibles().contains(user)) {
                bliList.add(bli);
            }
        }

        // System.out.println("Number of backlog items: " + bliList.size());

        // Get the effort sum
        EffortSumData effortSum = getEffortLeftResponsibleDividedSum(bliList);
        // data.setTotalEffort(effortSum.getEffortHours());

        // Check if there are unestimated items
        if (effortSum.getNonEstimatedItems() > 0) {
            data.setUnestimatedItems(true);
        }

        // Get total number of days left in backlog
        int numberOfDaysLeft = getWeekdaysLeftInBacklog(backlog, from);
        if (numberOfDaysLeft == 0) {
            numberOfDaysLeft = 1;
        }

        // Calculate the effort per day
        long effortPerDay = (effortSum.getEffortHours().getTime())
                / numberOfDaysLeft;

        // Loop through the weeks
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(from);
        for (int i = 0; i < numberOfWeeks; i++) {
            int daysOnWeek = getNumberOfDaysLeftForBacklogOnWeek(backlog, cal
                    .getTime());
            AFTime effort = new AFTime(daysOnWeek * effortPerDay);
            AFTime totals = new AFTime(effort.getTime());

            /*
             * System.out.println("Week " +
             * cal.get(GregorianCalendar.WEEK_OF_YEAR) + " effort: " + effort);
             */

            // Insert the week number
            data.getWeekNumbers().add(cal.get(GregorianCalendar.WEEK_OF_YEAR));

            // Set the weekly effort
            data.getEfforts().put(cal.get(GregorianCalendar.WEEK_OF_YEAR),
                    effort);
            data.getTotalEffort().add(effort);

            // Set the weekly overhead
            if (backlog instanceof Project) {
                AFTime overhead = getOverheadForWeek((Project) backlog, user,
                        daysOnWeek);
                data.getOverheads().put(
                        cal.get(GregorianCalendar.WEEK_OF_YEAR), overhead);

                totals.add(overhead);
                data.getTotalOverhead().add(overhead);
                // System.out.println("Overhead: " + overhead + "\nTotal
                // overhead: " + data.getTotalOverhead());
            }

            // Set the weekly total
            data.getWeeklyTotals().put(cal.get(GregorianCalendar.WEEK_OF_YEAR),
                    totals);

            // Next week
            cal.add(GregorianCalendar.WEEK_OF_YEAR, 1);
            // Roll to monday
            while (cal.get(GregorianCalendar.DAY_OF_WEEK) != GregorianCalendar.MONDAY) {
                cal.add(GregorianCalendar.DATE, -1);
            }
        }

        // Calculate the absolute total
        /*
         * data.getAbsoluteTotal().add(data.getTotalEffort());
         * data.getAbsoluteTotal().add(data.getTotalOverhead());
         */

        /*
         * System.out.println("Week\tEffort\tOverhead\tTotal"); // Print loop
         * for debugging for (Integer weekno : data.getWeekNumbers()) {
         * System.out.println(weekno + "\t" + data.getEfforts().get(weekno) +
         * "\t" + data.getOverheads().get(weekno) + "\t" +
         * data.getWeeklyTotals().get(weekno)); }
         * 
         * System.out.println("Total\t" + data.getTotalEffort() + "\t" +
         * data.getTotalOverhead() + "\t" + data.getAbsoluteTotal());
         */

        return data;
    }

    /** {@inheritDoc} */
    public AFTime getOverheadForWeek(Project project, User user, int daysOnWeek) {
        AFTime totalOverhead = new AFTime(0);

        // Check that the user is assigned
        for (Assignment ass : project.getAssignments()) {
            if (ass.getUser().equals(user)) {
                if (project.getDefaultOverhead() != null) {
                    totalOverhead.add(project.getDefaultOverhead());
                }
                if (ass.getDeltaOverhead() != null) {
                    totalOverhead.add(ass.getDeltaOverhead());
                }
                break;
            }
        }

        // Calculate overhead per day, 5 days a week
        long overheadPerDay = (totalOverhead.getTime()) / 5;

        return new AFTime(daysOnWeek * overheadPerDay);
    }

    /** {@inheritDoc} */
    public List<Backlog> getUserBacklogs(User user, Date now, int weeksAhead) {
        ArrayList<Project> projects = new ArrayList<Project>();
        ArrayList<Backlog> backlogs = new ArrayList<Backlog>();

        GregorianCalendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        cal.setTime(now);
        Date endDate = cal.getTime();
        cal.add(GregorianCalendar.WEEK_OF_YEAR, weeksAhead);
        Date startDate = cal.getTime();

        if (user == null) {
            return backlogs;
        }

        // Iterate through users assignments
        for (Assignment ass : user.getAssignments()) {
            // If backlog is not a project, skip it
            if (!ass.getBacklog().getClass().equals(Project.class)) {
                continue;
            }

            projects.add((Project) ass.getBacklog());
        }

        Collections.sort(projects, new BacklogComparator());

        for (Project blog : projects) {
            if (!backlogs.contains(blog)
                    && blog.getStartDate().before(startDate)
                    && blog.getEndDate().after(endDate)) {
                backlogs.add(blog);

                // Get the ongoing iterations of the project
                for (Iteration it : blog.getIterations()) {
                    if (it.getStartDate().before(startDate)
                            && it.getEndDate().after(endDate)) {
                        backlogs.add(it);
                    }
                }

            }
        }

        return backlogs;
    }

    /** {@inheritDoc} */
    public BacklogMetrics getBacklogMetrics(Backlog backlog) {
        /* Metrics for products are not calculated */
        if (backlog instanceof Product) {
            return null;
        }

        BacklogMetrics metrics = new BacklogMetrics();

        /* Get the history data */
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, -1);
        BacklogHistory history = backlog.getBacklogHistory();
        HistoryEntry<BacklogHistory> latestEntry = history.getDateEntry(cal
                .getTime());

        /* Calculate the values */
        AFTime effortLeft = new AFTime(latestEntry.getEffortLeft().getTime());
        effortLeft.add(latestEntry.getDeltaEffortLeft());

        metrics.setDailyVelocity(historyBusiness.calculateDailyVelocity(backlog
                .getId()));
        metrics.setScheduleVariance(historyBusiness.calculateScheduleVariance(
                backlog, effortLeft, metrics.getDailyVelocity()));
        metrics.setScopingNeeded(historyBusiness.calculateScopingNeeded(
                backlog, effortLeft, metrics.getDailyVelocity()));
        metrics.setEffortLeft(getEffortLeftSum(backlog.getBacklogItems())
                .getEffortHours());
        metrics.setOriginalEstimate(getOriginalEstimateSum(
                backlog.getBacklogItems()).getEffortHours());
        /* Get the done and not done backlog items */
        metrics.setTotalItems(new Integer(backlog.getBacklogItems().size()));
        metrics.setCompletedItems(backlogDAO
                .getNumberOfDoneBacklogItems(backlog));
        int percentDone = (int) Math
                .round(((double) metrics.getCompletedItems() / (double) metrics
                        .getTotalItems()) * 100.0);
        metrics.setPercentDone(percentDone);

        if (backlog.getEndDate().before(new Date())
                || backlog.getStartDate().after(new Date())) {
            metrics.setBacklogOngoing(false);
        } else {
            metrics.setBacklogOngoing(true);
        }

        return metrics;
    }

    /** {@inheritDoc} */
    public BacklogMetrics calculateLimitedBacklogMetrics(Backlog backlog) {
        BacklogMetrics metrics = backlogDAO.getBacklogMetrics(backlog);
        if (metrics == null) {
            return new BacklogMetrics();
        }
        int completedItems = backlogDAO.getNumberOfDoneBacklogItems(backlog);
        if (metrics.getTotalItems() > 0) {
            metrics.setPercentDone(Math.round(100f * (float) completedItems
                    / (float) metrics.getTotalItems()));
        }
        return metrics;
    }

    /**
     * Checks, if two backlogs are under the same product. We already know that
     * the backlogs are not null. Tests may lead to backlogs without products,
     * therefore check that.
     * 
     * @param backlog1
     * @param backlog2
     * @return
     */
    public boolean isUnderSameProduct(Backlog backlog1, Backlog backlog2) {
        Product product1 = null;
        Product product2 = null;
        if (backlog1 instanceof Product) {
            product1 = (Product) backlog1;
        } else if (backlog1 instanceof Project) {
            product1 = ((Project) backlog1).getProduct();
        } else {
            Project proj1 = ((Iteration) backlog1).getProject();
            if (proj1 != null) {
                product1 = proj1.getProduct();
            }
        }

        if (backlog2 instanceof Product) {
            product2 = (Product) backlog2;
        } else if (backlog2 instanceof Project) {
            product2 = ((Project) backlog2).getProduct();
        } else {
            Project proj2 = ((Iteration) backlog2).getProject();
            if (proj2 != null) {
                product2 = proj2.getProduct();
            }
        }

        if (product1 != null && product2 != null) {
            return product1.equals(product2);
        } else {
            return false;
        }
    }
    
    public boolean isUnderSameProduct(int backlogId1, int backlogId2) {
        Backlog backlog1  = backlogDAO.get(backlogId1);
        Backlog backlog2 = backlogDAO.get(backlogId2);
        if(backlog1 == null || backlog2 == null) {
            return false;
        }
        return isUnderSameProduct(backlog1, backlog2);
    }

    public String getAllBacklogsAsJSON() {
        return new JSONSerializer().serialize(backlogDAO.getAll());
    }

    public String getAllProductsAsJSON() {
        return new JSONSerializer().serialize(productDAO.getAll());
    }

    public String getBacklogAsJSON(Backlog backlog) {
        if (backlog == null) {
            return "{}";
        }
        return new JSONSerializer().serialize(backlog);
    }

    public String getBacklogAsJSON(int backlogId) {
        return getBacklogAsJSON(backlogDAO.get(backlogId));
    }

    /** {@inheritDoc} */
    public String getIterationGoalsAsJSON(int backlogId) {
        return getIterationGoalsAsJSON(backlogDAO.get(backlogId));
    }

    /** {@inheritDoc} */
    public String getIterationGoalsAsJSON(Backlog backlog) {
        if (backlog == null || !(backlog instanceof Iteration)) {
            return "[]";
        }
        Collection<IterationGoal> list = ((Iteration) backlog)
                .getIterationGoals();
        return new JSONSerializer().serialize(list);
    }

    /** {@inheritDoc} */
    public void removeThemeBindings(Backlog backlog) {
        if (backlog != null) {
            for (BacklogThemeBinding bind : backlog.getBusinessThemeBindings()) {
                businessThemeBusiness.removeThemeBinding(bind);
            }
        }
    }

    public int getNumberOfAssignedUsers(Backlog backlog) {
        return getUsers(backlog, true).size();
    }

    /*
     * Autogenerated list of getters and setters
     */

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setBusinessThemeDAO(BusinessThemeDAO businessThemeDAO) {
        this.businessThemeDAO = businessThemeDAO;
    }

    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }

    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public IterationGoalBusiness getIterationGoalBusiness() {
        return iterationGoalBusiness;
    }

    public void setIterationGoalBusiness(
            IterationGoalBusiness iterationGoalBusiness) {
        this.iterationGoalBusiness = iterationGoalBusiness;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void setBusinessThemeBusiness(
            BusinessThemeBusiness businessThemeBusiness) {
        this.businessThemeBusiness = businessThemeBusiness;
    }

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }
    
    public List<BacklogItem> getProductTopLevelBacklogItems(int productId) {
        return (List<BacklogItem>) backlogItemDAO
                .productNonDoneTopLevelBacklogItems(productId);

    }
    
    public String getProductTopLevelBacklogItemsAsJson(int productId) {
        List<BacklogItem> list = this.getProductTopLevelBacklogItems(productId);
        return new JSONSerializer().serialize(list);
    }

}
