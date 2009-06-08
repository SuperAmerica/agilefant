package fi.hut.soberit.agilefant.web;

import java.text.ParseException;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.CalendarUtils;

/**
 * Action for calculating logged spent effort for the current user between two
 * dates.
 * 
 * @author rtammisa
 */
@Component("getHourSumAction")
@Scope("prototype")
public class GetHourSumAction extends ActionSupport {
    private static final long serialVersionUID = -8463616232401623518L;

    private String startDate;
    private String endDate;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    private long hourSum;
    private boolean badArgumentFound;

    private DateTime parseDateTime(String string) throws ParseException {
        return new DateTime(CalendarUtils.parseDateFromString(string));
    }

    /**
     * Sums all the hours for the current user between startDate and endDate.
     */
    public String sumHours() {
        User user = SecurityUtil.getLoggedUser();

        try {
            hourSum = hourEntryBusiness.calculateSumByUserAndTimeInterval(user,
                    parseDateTime(startDate), parseDateTime(endDate));
            badArgumentFound = false;
        } catch (ParseException e) {
            badArgumentFound = true;
        }

        return Action.SUCCESS;
    }

    public long getHourSum() {
        return hourSum;
    }

    public void setHourSum(long hourSum) {
        this.hourSum = hourSum;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isBadArgumentFound() {
        return badArgumentFound;
    }

    public void setBadArgumentFound(boolean badArgumentFound) {
        this.badArgumentFound = badArgumentFound;
    }

}
