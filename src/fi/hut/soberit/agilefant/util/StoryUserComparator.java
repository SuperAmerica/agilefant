package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

public class StoryUserComparator implements Comparator<ResponsibleContainer> {

    public int compare(ResponsibleContainer u1, ResponsibleContainer u2) {
        if (u1 != null && u2 != null) {
            if(u1.getUser() != null && u2.getUser() != null) {
                return u1.getUser().getFullName().compareTo(u2.getUser().getFullName());
            }
        }
        return 0;
    }
}
