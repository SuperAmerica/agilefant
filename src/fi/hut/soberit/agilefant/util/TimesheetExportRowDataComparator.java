package fi.hut.soberit.agilefant.util;

import java.util.Comparator;

public class TimesheetExportRowDataComparator implements Comparator<TimesheetExportRowData> {

    private NamedObjectComparator innerComparator;
    
    public TimesheetExportRowDataComparator() {
        innerComparator = new NamedObjectComparator();
    }
    public int compare(TimesheetExportRowData arg0, TimesheetExportRowData arg1) {
        if(arg0 == null && arg1 == null) {
            return 0;
        }
        if(arg0 == null) {
            return -1;
        }
        if(arg1 == null) {
            return 1;
        }
        int subCompare = 0;
        subCompare = innerComparator.compare(arg0.getProduct(), arg1.getProduct());
        if(subCompare != 0) {
            return subCompare;
        }
        subCompare = innerComparator.compare(arg0.getProject(), arg1.getProject());
        if(subCompare != 0) {
            return subCompare;
        }
        subCompare = innerComparator.compare(arg0.getIteration(), arg1.getIteration());
        if(subCompare != 0) {
            return subCompare;
        }
        subCompare = innerComparator.compare(arg0.getStory(), arg1.getStory());
        if(subCompare != 0) {
            return subCompare;
        }
        return innerComparator.compare(arg0.getTask(), arg1.getTask());
    }

}
