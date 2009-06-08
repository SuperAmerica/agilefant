package fi.hut.soberit.agilefant.model;

public enum TaskState {

    NOT_STARTED, STARTED, PENDING, BLOCKED, IMPLEMENTED, DONE;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }

}
