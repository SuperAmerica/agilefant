package fi.hut.soberit.agilefant.transfer;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class HourEntryTO extends HourEntry {
    // we need a field so flexjson works
    private Long dateMilliSeconds = null;
    
    public HourEntryTO(TaskHourEntry t) {
        BeanCopier.copy(t, this);
        DateTime d = getDate();
        if(d != null)
            dateMilliSeconds = d.getMillis();
    }
    
    public Long getDateMilliSeconds() {
        return dateMilliSeconds;
    }
}
