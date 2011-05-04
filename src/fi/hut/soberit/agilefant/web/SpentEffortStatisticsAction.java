package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.SpentEffortStatisticsBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailyUserSpentEffortTO;

@Component("spentEffortStatisticsAction")
@Scope("prototype")
public class SpentEffortStatisticsAction extends ActionSupport {
    
    private static final long serialVersionUID = -2641372816050021727L;

    private int userId;
    private List<DailyUserSpentEffortTO> entries;
    
    @Autowired
    private SpentEffortStatisticsBusiness spentEffortStatisticsBusiness;
    
    @Autowired
    private UserBusiness userBusiness;

    public String retrieveMonthlyStatisticsByUser() {
        DateTime start = new DateTime().minusMonths(1).toDateMidnight().toDateTime();
        User user = this.userBusiness.retrieve(userId);
        this.entries = this.spentEffortStatisticsBusiness.retrieveByUser(user, start, 30);
        return Action.SUCCESS;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<DailyUserSpentEffortTO> getEntries() {
        return entries;
    }

}
