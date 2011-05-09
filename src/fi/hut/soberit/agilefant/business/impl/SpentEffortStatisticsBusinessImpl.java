package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.SpentEffortStatisticsBusiness;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailyUserSpentEffortTO;

@Service("spentEffortStatisticsBusiness")
public class SpentEffortStatisticsBusinessImpl implements
        SpentEffortStatisticsBusiness {
    
    @Autowired
    private HourEntryDAO hourEntryDAO;

    public List<DailyUserSpentEffortTO> retrieveByUser(User user,
            DateTime start, int daysForward) {
        List<DailyUserSpentEffortTO> ret = new ArrayList<DailyUserSpentEffortTO>();

        Interval interval = new Interval(start, start.plusDays(30));
        List<HourEntry> rawEntries = hourEntryDAO.retrieveByUserAndInterval(user, interval);
        
        Map<DateMidnight, Long> cumulative = new HashMap<DateMidnight, Long>();
        
        for(HourEntry he : rawEntries) {
            DateMidnight dm = he.getDate().toDateMidnight();
            if(!cumulative.containsKey(dm)) {
                cumulative.put(dm, 0L);
            }
            cumulative.put(dm, cumulative.get(dm) + he.getMinutesSpent());
        }
        
        //mock data!
        for(int i = 0; i < daysForward; i++) {
            DailyUserSpentEffortTO tmp  = new DailyUserSpentEffortTO();
            DateTime cdate = start.plusDays(i);
            tmp.setDate(cdate);
         
            Long sum = cumulative.get(cdate.toDateMidnight());
            if(sum != null) {
                tmp.setTotalEffort(sum);
            }
            //tmp.setAssignedEffort(Math.round(10*Math.random())%5 + 4);
            //tmp.setUnassignedEffort(Math.round(10*Math.random())%3 + 1);
            ret.add(tmp);
        }
        return ret;
    }

}
