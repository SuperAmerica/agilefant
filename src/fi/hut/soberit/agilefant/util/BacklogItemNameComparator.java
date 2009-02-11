package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.BacklogItem;

/**
 * Compares backlogItem names alphabetically.
 * @author kranki
 *
 */
public class BacklogItemNameComparator implements Comparator<BacklogItem> {

    public int compare(BacklogItem arg0, BacklogItem arg1) {
        return arg0.getName().compareTo(arg1.getName());
    }
    
}
