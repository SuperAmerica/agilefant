package fi.hut.soberit.agilefant.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum StoryState {

    NOT_STARTED, STARTED, PENDING, BLOCKED, IMPLEMENTED, DONE;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }

    public static final Set<StoryState> valueSet;

    static {
        Set<StoryState> allValues = new HashSet<StoryState>();
        allValues.add(NOT_STARTED);
        allValues.add(STARTED);
        allValues.add(PENDING);
        allValues.add(BLOCKED);
        allValues.add(IMPLEMENTED);
        allValues.add(DONE);
        valueSet = Collections.unmodifiableSet(allValues);
    }

}
