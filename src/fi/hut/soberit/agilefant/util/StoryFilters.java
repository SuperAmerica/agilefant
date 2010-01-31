package fi.hut.soberit.agilefant.util;

import java.util.Set;

import fi.hut.soberit.agilefant.model.StoryState;

public class StoryFilters {

    public final Set<String> labels;
    public final Set<StoryState> states;
    public final String name;

    public StoryFilters(String name, Set<String> labels, Set<StoryState> states) {
        this.name = name;
        this.labels = labels;
        this.states = states;
    }

}
