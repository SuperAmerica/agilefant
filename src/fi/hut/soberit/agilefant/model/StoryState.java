package fi.hut.soberit.agilefant.model;

public enum StoryState {

    NOT_STARTED, STARTED, PENDING, BLOCKED, IMPLEMENTED, DONE;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }

}
