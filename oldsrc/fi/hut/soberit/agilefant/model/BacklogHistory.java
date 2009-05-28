package fi.hut.soberit.agilefant.model;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@DiscriminatorValue("1")
public class BacklogHistory extends History<Backlog> {
    private List<HistoryEntry<BacklogHistory>> effortHistoryEntries = new LinkedList<HistoryEntry<BacklogHistory>>();

    @OneToMany(mappedBy="history")
    @OrderBy(value="date desc")
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    public List<HistoryEntry<BacklogHistory>> getEffortHistoryEntries() {
        return effortHistoryEntries;
    }

    public void setEffortHistoryEntries(List<HistoryEntry<BacklogHistory>> effortHistoryEntries) {
        this.effortHistoryEntries = effortHistoryEntries;
    };
    
    /**
     * Returns the <code>HistoryEntry</code> for today. <code>null</code> if one does not exist.
     * @return
     */
    @Transient
    public HistoryEntry<BacklogHistory> getCurrentEntry() {
        if( effortHistoryEntries.size() > 0 ) {
            HistoryEntry<BacklogHistory> entry = effortHistoryEntries.get(0);
            GregorianCalendar today = new GregorianCalendar();
            today.setTime( new Date() );
            GregorianCalendar entryDate = new GregorianCalendar();
            entryDate.setTime( entry.getDate() );
            
            if( today.get(GregorianCalendar.YEAR) == entryDate.get(GregorianCalendar.YEAR)
                    && today.get(GregorianCalendar.MONTH) == entryDate.get(GregorianCalendar.MONTH)
                    && today.get(GregorianCalendar.DAY_OF_MONTH) == entryDate.get(GregorianCalendar.DAY_OF_MONTH)) {
                return entry;
            }
        }
        return null;
    }
    
    /**
     * Returns the latest <code>HistoryEntry</code> for this history.
     */
    @Transient
    public HistoryEntry<BacklogHistory> getLatestEntry() {        
        if( effortHistoryEntries.size() > 0)
            return effortHistoryEntries.get(0);
        else
            return createTemporaryEntry(new Date());
    }
    
    /**
     * Returns the latest <code>HistoryEntry</code> for this history, excluding current.
     * returns null if none exists.
     */
    @Transient
    public HistoryEntry<BacklogHistory> getLastToCurrentEntry() {
        if ( effortHistoryEntries.size() > 0 ) {
            HistoryEntry<BacklogHistory> lastEntry = effortHistoryEntries.get(0);
            GregorianCalendar today = new GregorianCalendar();
            today.setTime( new Date() );
            GregorianCalendar entryDate = new GregorianCalendar();
            entryDate.setTime( lastEntry.getDate() );
            
            if( !(today.get(GregorianCalendar.YEAR) == entryDate.get(GregorianCalendar.YEAR)
                    && today.get(GregorianCalendar.MONTH) == entryDate.get(GregorianCalendar.MONTH)
                    && today.get(GregorianCalendar.DAY_OF_MONTH) == entryDate.get(GregorianCalendar.DAY_OF_MONTH)) ) {
                return lastEntry;
            } else if ( effortHistoryEntries.size() > 1 ) {
               return effortHistoryEntries.get(1); 
            }        
        }
        return null;
    }
    
    /**
     * Returns the latest <code>HistoryEntry</code> that is <em>not after</code> the reference Date.
     * @param date Reference <code>Date</code>
     * @return
     */
    @Transient
    public HistoryEntry<BacklogHistory> getDateEntry(Date date) {      
        for( HistoryEntry<BacklogHistory> entry : effortHistoryEntries ) {
            if( !entry.getDate().after(date) ) {
                return entry;
            }
        }
        /* Following code is for backlogs where the starting time has been moved backwards beyond when the backlog was created. */
        return createTemporaryEntry(date);
    }
    
    /**
     * Gotta figure a smarter way to do this.
     * @param date
     * @return
     */
    @Deprecated
    private HistoryEntry<BacklogHistory> createTemporaryEntry(Date date) {
        HistoryEntry<BacklogHistory> faux = new HistoryEntry<BacklogHistory>();
        faux.setDate( new java.sql.Date(date.getTime()) );
        faux.setEffortLeft(new AFTime(0) );
        faux.setOriginalEstimate(new AFTime(0));
        faux.setDeltaEffortLeft(new AFTime(0));
        return faux;
    }
    
    /**
     * Returns the current effort left for this <code>BacklogHistory</code>
     * @return
     */
    @Transient
    public AFTime getCurrentEffortLeft() {
        HistoryEntry<BacklogHistory> entry;
        entry = getLatestEntry();
        if(entry != null)
            return entry.getEffortLeft();
        return new AFTime(0);
    }
    /**
     * Returns the current original estimate for this <code>BacklogHistory</code>
     * @return
     */
    @Transient
    public AFTime getCurrentOriginalEstimate() {
        HistoryEntry<BacklogHistory> entry;
        entry = getLatestEntry();
        if(entry != null)
            return entry.getOriginalEstimate();
        return new AFTime(0);
    }    
}
