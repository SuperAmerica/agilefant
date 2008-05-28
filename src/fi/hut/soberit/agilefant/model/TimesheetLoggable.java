package fi.hut.soberit.agilefant.model;

/**
 * Interface for things that are timesheet loggable.
 * 
 * @author Roni Tammisalo, Ville Rantamaula
 */
public interface TimesheetLoggable {
    
    /**
     * Get entity type.
     */
    public String timesheetType();
    /**
     * Get identifier
     */
    public int getId();
}
