package fi.hut.soberit.agilefant.model;

/**
 * A state enumeration, which represents the state of a Todo and Story.
 * <p>
 * Possible values are "not started", "started", "blocked", "implemented" and
 * "done".
 * 
 * @see fi.hut.soberit.agilefant.model.Todo
 */
public enum State {
    NOT_STARTED, STARTED, PENDING, BLOCKED, IMPLEMENTED, DONE;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }

}
