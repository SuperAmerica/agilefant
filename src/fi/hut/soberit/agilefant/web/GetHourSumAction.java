package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class GetHourSumAction extends ActionSupport {
    private static final long serialVersionUID = -8463616232401623518L;
    
    private String startDate;
    private String endDate;
    private HourEntryBusiness hourEntryBusiness;
    private AFTime hourSum;
    private boolean badArgumentFound;

    /**
     * Sums all the hours for the current user.
     */
    public String sumHours() {
        User user = SecurityUtil.getLoggedUser();
        
        try {
            hourSum = hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                    startDate, endDate);
            badArgumentFound = false;
        } catch (IllegalArgumentException iae) {
            badArgumentFound = true;
        }
        
        return Action.SUCCESS;
    }
    
    public AFTime getHourSum() {
        return hourSum;
    }
    public void setHourSum(AFTime hourSum) {
        this.hourSum = hourSum;
    }
    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
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
