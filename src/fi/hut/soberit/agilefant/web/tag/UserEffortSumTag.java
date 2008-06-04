package fi.hut.soberit.agilefant.web.tag;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.User;

/**
 * Calculate the user's hour entry sums for Today/Yesterday/This week/This month.
 * 
 * @author Roni Tammisalo
 */
public class UserEffortSumTag extends SpringTagSupport {
    
    private static final long serialVersionUID = -4749794515584345165L;

    private HourEntryBusiness hourEntryBusiness;
    
    private User user;

    private String timeInterval;
    
    @Override
    public int doStartTag() throws JspException {
        AFTime sum = null;
        
        hourEntryBusiness = (HourEntryBusiness) super.getApplicationContext().getBean(
                "hourEntryBusiness");
        
        if (timeInterval.equals("Today")) {
            sum = this.getSpentEffortForToday();
            
        } else if (timeInterval.equals("Yesterday")) {
            sum = this.getSpentEffortForYesterday();
            
        } else if (timeInterval.equals("This week")) {
            sum = this.getSpentEffortForThisWeek();
            
        } else if (timeInterval.equals("This month")) {
            sum = this.getSpentEffortForThisMonth();
        }
        
        super.getPageContext().setAttribute(super.getId(), sum);
        
        return Tag.EVAL_BODY_INCLUDE;
    }
    
    /**
     * Returns total spent effort between startDate and endDate.
     */
    private AFTime getSpentEffort(Date startDate, Date endDate) {
        return hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, startDate, endDate);
    }
    
    /**
     * Returns total spent effort for today.
     */
    private AFTime getSpentEffortForToday() {
        Date startDate;
        Date endDate;
        Calendar calendar = this.getCorrectedCalendar();
        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        startDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        endDate = calendar.getTime();
        
        return this.getSpentEffort(startDate, endDate);
    }
    
    /**
     * Returns total spent effort for yesterday.
     */
    private AFTime getSpentEffortForYesterday() {
        Date startDate;
        Date endDate;
        Calendar calendar = this.getCorrectedCalendar();
        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        startDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        endDate = calendar.getTime();
        
        return this.getSpentEffort(startDate, endDate);
    }
    
    /**
     * Returns total spent effort for this week.
     */
    private AFTime getSpentEffortForThisWeek() {
        Date startDate;
        Date endDate;
        Calendar calendar = this.getCorrectedCalendar();
        
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        endDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        startDate = calendar.getTime();
        
        return this.getSpentEffort(startDate, endDate);
    }
    
    /**
     * Returns total spent effort for this month.
     */
    private AFTime getSpentEffortForThisMonth() {
        Date startDate;
        Date endDate;
        Calendar calendar = this.getCorrectedCalendar();
        
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        endDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        startDate = calendar.getTime();
        
        return this.getSpentEffort(startDate, endDate);
    }
    
    /**
     * Returns a calendar instance with minutes and seconds set to zero.
     */
    private Calendar getCorrectedCalendar() {
        Calendar calendar = Calendar.getInstance();
        
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        
        return calendar;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }
}
