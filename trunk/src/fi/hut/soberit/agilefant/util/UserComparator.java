package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.User;

/**
 * A comparator class for sorting Users
 * 
 * @author rstrom
 */
public class UserComparator implements Comparator<User> {

    public int compare(User o1, User o2) {
        if (o1 != null && o2 != null && o1.getFullName() != null
                && o2.getFullName() != null)
            return o1.getFullName().compareTo(o2.getFullName());
        else
            return 0;

    }
}
