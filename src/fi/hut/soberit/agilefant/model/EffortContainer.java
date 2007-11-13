package fi.hut.soberit.agilefant.model;

/**
 * Interface for things that, well, can have information on effort estimate and
 * already performed effort.
 */
public interface EffortContainer {

    /** Get effort estimate. */
    public AFTime getEffortEstimate();

    /** Get already performed effort. */
    public AFTime getPerformedEffort();
}
