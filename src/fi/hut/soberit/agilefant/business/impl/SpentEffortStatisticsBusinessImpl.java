package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.SpentEffortStatisticsBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailyUserSpentEffortTO;

@Service("spentEffortStatisticsBusiness")
public class SpentEffortStatisticsBusinessImpl implements
        SpentEffortStatisticsBusiness {

    public List<DailyUserSpentEffortTO> retrieveByUser(User user,
            DateTime start, int daysForward) {
        List<DailyUserSpentEffortTO> ret = new ArrayList<DailyUserSpentEffortTO>();

        //mock data!
        for(int i = 0; i < daysForward; i++) {
            DailyUserSpentEffortTO tmp  = new DailyUserSpentEffortTO();
            tmp.setDate(start.plusDays(i));
            tmp.setAssignedEffort(Math.round(10*Math.random())%5 + 4);
            tmp.setUnassignedEffort(Math.round(10*Math.random())%3 + 1);
            ret.add(tmp);
        }
        return ret;
    }

}
