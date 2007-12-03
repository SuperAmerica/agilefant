package fi.hut.soberit.agilefant.model;

/**
 * Interface for things that, well, can have information on effort estimate.
 */
public interface EffortContainer {

    /** Get effort estimate. */
    public AFTime getEffortLeft();
    
}
