package fi.hut.soberit.agilefant.model;

/**
 * A status enumeration, which represents the status of project.
 * 
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
