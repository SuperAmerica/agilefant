package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;

public class StoryComparator implements Comparator<Story> {

    public int compare(Story story1, Story story2) {
        if (story1 == null) {
            return -1;
        }
        if (story2 == null) {
            return 1;
        }

        if (story1.getState() == StoryState.DONE
                && story2.getState() != StoryState.DONE) {
            return 1;
        } else if (story1.getState() != StoryState.DONE
                && story2.getState() == StoryState.DONE) {
            return -1;
        } else {
            return story1.getName().compareTo(story2.getName());
        }
    }

}
