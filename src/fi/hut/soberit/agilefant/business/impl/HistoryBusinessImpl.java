package fi.hut.soberit.agilefant.business.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HistoryEntry;

public class HistoryBusinessImpl implements HistoryBusiness {
    private BacklogDAO backlogDAO;
    
    public void updateBacklogHistory(int backlogId) {
        Backlog backlog = backlogDAO.get(backlogId);
        BacklogHistory history = backlog.getBacklogHistory();
        
        if( history == null ) {
            history = newBacklogHistory(backlog);
        }
        
        AFTime originalEstimate = new AFTime(0);
        AFTime effortLeft = new AFTime(0);
        
        // Calculate newest EL and OE.
        for(BacklogItem item : backlog.getBacklogItems()) {
            if( item.getOriginalEstimate() != null ) {
                originalEstimate.add( item.getOriginalEstimate() );
            }
            if( item.getEffortLeft() != null ) {
                effortLeft.add( item.getEffortLeft() );
            }            
        }
        
        HistoryEntry<BacklogHistory> entry = history.getCurrentEntry();
        HistoryEntry<BacklogHistory> latestEntry = history.getLatestEntry();
        
        // Calculate delta.
        long delta = effortLeft.getTime() - latestEntry.getEffortLeft().getTime();
        // Add delta only if orig. est. changed.
        boolean originalEstimateChanged = originalEstimate.getTime() !=  latestEntry.getOriginalEstimate().getTime();
                      
        if( entry == null ) { 
            entry = new HistoryEntry<BacklogHistory>();
            entry.setDate(new java.sql.Date( new Date().getTime()));
            entry.setHistory(history);
            entry.setEffortLeft(new AFTime(0));
            entry.setOriginalEstimate(new AFTime(0));
            history.getEffortHistoryEntries().add(entry);            
        }
        
        // Calculate deltaEffortLeft only if orig. est. changed.
        if ( originalEstimateChanged ) {                       
            
            // If EL changed differently from OE, subtract the difference from delta
            // OE2 - OE1 - (EL2 - EL1)
            AFTime newestChange = new AFTime(originalEstimate.getTime() - entry.getOriginalEstimate().getTime()
                        - effortLeft.getTime() + entry.getEffortLeft().getTime() );
            // Delta EL set to yesterday's entry, to make drawing the chart easier.
            updateYesterdayEntry(history, new AFTime(delta), newestChange);
        }     
        
        entry.setEffortLeft( effortLeft );
        entry.setOriginalEstimate( originalEstimate );
        // Today's entry always gets delta = 0.
        entry.setDeltaEffortLeft( new AFTime(0) );       
    }

    /**
     * Updates yesterday's entry with delta or creates a new one and copies old effort left and
     * original estimate to it. NewestChange cancel out changes made to effort lefts to items
     * that are then removed/reset/moved.
     */
    private void updateYesterdayEntry(BacklogHistory history, AFTime deltaEffortLeft, AFTime newestChange) {
        HistoryEntry<BacklogHistory> lastEntry = history.getLastToCurrentEntry();
        // Yesterday.
        Calendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DATE, -1);
        
        // No entries, create a new "yesterday" entry with EL and OE = 0 and given delta.
        if (lastEntry == null) {
            HistoryEntry<BacklogHistory> yesterdayEntry = new HistoryEntry<BacklogHistory>();
            yesterdayEntry.setDate(new java.sql.Date(yesterday.getTime().getTime()));
            yesterdayEntry.setHistory(history);
            yesterdayEntry.setEffortLeft(new AFTime(0));
            yesterdayEntry.setOriginalEstimate(new AFTime(0));
            yesterdayEntry.setDeltaEffortLeft(deltaEffortLeft);
            history.getEffortHistoryEntries().add(yesterdayEntry);
            return;
        }
              
        Calendar last = new GregorianCalendar();
        last.setTime(lastEntry.getDate());
        
        // Last entry is yesterday, update its delta.
        if ( yesterday.get(GregorianCalendar.YEAR) == last.get(GregorianCalendar.YEAR)
                && yesterday.get(GregorianCalendar.MONTH) == last.get(GregorianCalendar.MONTH)
                && yesterday.get(GregorianCalendar.DAY_OF_MONTH) == last.get(GregorianCalendar.DAY_OF_MONTH) ) {
            /* To cancel out changes made during the day to effort lefts, decrease from delta orig.est. - eff.left,
             * if old delta is > 0.
            */
            if (lastEntry.getDeltaEffortLeft().getTime() > 0) {
                lastEntry.getDeltaEffortLeft().add(newestChange);
            }
            lastEntry.getDeltaEffortLeft().add(deltaEffortLeft);       
        }
        // Last entry is older, create a new "yesterday" entry, and copy the older one's EL and OE to it.
        else {
            HistoryEntry<BacklogHistory> yesterdayEntry = new HistoryEntry<BacklogHistory>();
            HistoryEntry<BacklogHistory> lastBeforeYesterday = history.getDateEntry(last.getTime());
            yesterdayEntry.setDate(new java.sql.Date(yesterday.getTime().getTime()));
            yesterdayEntry.setHistory(history);
            yesterdayEntry.setEffortLeft(lastBeforeYesterday.getEffortLeft());
            yesterdayEntry.setOriginalEstimate(lastBeforeYesterday.getOriginalEstimate());
            yesterdayEntry.setDeltaEffortLeft(deltaEffortLeft);
            history.getEffortHistoryEntries().add(yesterdayEntry);
        }
    }
   
    
    /**
     * Creates a <code>BacklogHistory</code> for a Backlog which is missing it.
     * @param backlog
     */
    private BacklogHistory newBacklogHistory(Backlog backlog) {
        BacklogHistory history = new BacklogHistory();
        history.setEffortHistoryEntries( new LinkedList<HistoryEntry<BacklogHistory>>());
        backlog.setBacklogHistory(history);
        return history;
    }

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }
}
