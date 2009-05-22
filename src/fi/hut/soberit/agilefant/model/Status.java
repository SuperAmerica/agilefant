package fi.hut.soberit.agilefant.model;

/**
 * A status enumeration, which represents the status of project.
 * <p>
 * Possible values are "ok", "challenged" and "critical".
 * 
 * @see fi.hut.soberit.agilefant.model.Task
 */
public enum Status {
    GREEN, YELLOW, RED, GREY, BLACK;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }

}
