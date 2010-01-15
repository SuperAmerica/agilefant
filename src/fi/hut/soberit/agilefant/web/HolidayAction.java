package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.HolidayBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;

@Component("holidayAction")
@Scope("prototype")
public class HolidayAction extends ActionSupport implements Prefetching {

    private static final long serialVersionUID = -4798675617849645091L;
    private List<Holiday> userHolidays = new ArrayList<Holiday>();
    private Holiday holiday = new Holiday();
    private int holidayId = 0;
    private int userId = 0;

    @Autowired
    private HolidayBusiness holidayBusiness;
    @Autowired
    private UserBusiness userBusiness;

    public void initializePrefetchedData(int objectId) {
        this.holiday = this.holidayBusiness.retrieveDetached(objectId);
    }

    public String store() {
        this.holidayBusiness.store(this.holiday);
        return Action.SUCCESS;
    }

    public String retrieve() {
        this.holiday = this.holidayBusiness.retrieve(this.holidayId);
        return Action.SUCCESS;
    }

    public String futureUserHolidays() {
        User user = this.userBusiness.retrieve(this.userId);
        this.userHolidays = this.holidayBusiness
                .retrieveFutureHolidaysByUser(user);
        return Action.SUCCESS;
    }

    public List<Holiday> getUserHolidays() {
        return userHolidays;
    }

    public void setUserHolidays(List<Holiday> userHolidays) {
        this.userHolidays = userHolidays;
    }

    public Holiday getHoliday() {
        return holiday;
    }

    public void setHoliday(Holiday holiday) {
        this.holiday = holiday;
    }

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
