package fi.hut.soberit.agilefant.util;

import java.util.HashSet;
import java.util.Set;

import fi.hut.soberit.agilefant.model.StoryState;

public class StoryFilters {

    public final Set<StoryState> states;
    public final String name;

    public StoryFilters() {
        this.name = null;
        this.states = new HashSet<StoryState>();
    }
    
    public StoryFilters(String name, Set<StoryState> states) {
        this.name = name;
        this.states = states;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((states == null) ? 0 : states.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StoryFilters other = (StoryFilters) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (states == null) {
            if (other.states != null)
                return false;
        } else if (!states.equals(other.states))
            return false;
        return true;
    }

}
