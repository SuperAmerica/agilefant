package fi.hut.soberit.agilefant.business;

import java.util.Date;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;

public interface HistoryBusiness {
    public void updateBacklogHistory(int backlogId);
    
    
    /**
     * Calculate daily velocity for a project or an iteration.
     * <p>
     * Substract the current original estimate from current effort
     * left and divide by spent days.
     * @param backlogId
     * @return the daily velocity
     */
    public AFTime calculateDailyVelocity(int backlogId);
    
    /**
     * Calculate the expected date for completion, i.e. when the
     * effort left will be zero. The expected date is returned by
     * a day's accuracy.
     * @return null if velocity is negative or backlog is a product, expected date otherwise
     */
    public Date calculateExpectedDate(Backlog backlog, AFTime originalEstimate, AFTime velocity);
    
    /**
     * Calculate the schedule variance in days.
     * @param backlog
     * @param originalEstimate
     * @param velocity
     * @return
     */
    public Integer calculateScheduleVariance(Backlog backlog, AFTime originalEstimate, AFTime velocity);
    
    /**
     * Calculate the amount of scoping needed so that the iteration or project
     * is finished on time.
     * 
     * @return null if velocity is negative or backlog is a product, expected scoping needed otherwise
     */
    public AFTime calculateScopingNeeded(Backlog backlog, AFTime effortLeft, AFTime velocity);
}