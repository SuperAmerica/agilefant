package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;

public class BacklogItemComparator implements Comparator<BacklogItem> {

    public int compare(BacklogItem o1, BacklogItem o2) {
        if(o1 != null && o2 != null) {
            if(o1.getState() != State.DONE && o2.getState() != State.DONE) {
                return o2.getPriority().compareTo(o1.getPriority());
            } else if(o1.getState() == State.DONE) {
                return 1;
            } else if(o2.getState() == State.DONE) {
                return -1;
            } else {
                return o2.getPriority().compareTo(o1.getPriority());
            }
        }
        return 0;
    }
}
