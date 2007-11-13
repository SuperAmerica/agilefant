package fi.hut.soberit.agilefant.model;

/**
 * A priority enumeration, which represents the priority of a task and a backlog
 * item.
 * 
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.Task
 */
public enum Priority {
    TRIVIAL, MINOR, MAJOR, CRITICAL, BLOCKER, UNDEFINED;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }
}
