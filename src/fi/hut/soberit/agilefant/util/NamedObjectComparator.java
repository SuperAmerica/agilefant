package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

import fi.hut.soberit.agilefant.model.NamedObject;

public class NamedObjectComparator implements Comparator<NamedObject> {
    
    public int compare(NamedObject arg0, NamedObject arg1) {
        if(arg0 == null && arg1 == null) {
            return 0;
        }
        if(arg0 == null) {
            return -1;
        }
        if(arg1 == null) {
            return 1;
        }
        return arg0.getName().compareTo(arg1.getName());
    }

}
