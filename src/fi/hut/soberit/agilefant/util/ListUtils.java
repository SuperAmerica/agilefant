package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ListUtils<T> {
    
    /**
     * Removes duplicates from a collection and returns a new collection with
     * the duplicates removed. 
     * @param <T>
     * @param collection
     * @return
     */
    public static<T> Collection<T> removeDuplicates(Collection<T> collection) {
        Set<T> set = new HashSet<T>(collection);
        Collection<T> returnedList = new ArrayList<T>(set);
        return returnedList;
    }
}
