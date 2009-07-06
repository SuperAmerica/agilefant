package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.User;

/**
 * Calculates the user's hour entry sums for Today/Yesterday/This week/This
 * month. Days start at 00:00 and end at 23:59 for hour entries.
 * 
 * @author Roni Tammisalo
 */
public class UserEffortSumTag extends SpringTagSupport {

    private static final long serialVersionUID = -4749794515584345165L;

    private HourEntryBusiness hourEntryBusiness;

    private User user;

    private String timeInterval;

    @Override
    protected void retrieveSingletons() {
        hourEntryBusiness = requireBean("hourEntryBusiness");
    }

    @Override
    public int doStartTag() throws JspException {
        long sum = 0;

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
    private long getSpentEffort(DateTime startDate, DateTime endDate) {
        DateTime correctedEndDate = endDate.withTime(23, 59, 0, 0);
        return hourEntryBusiness.calculateSumByUserAndTimeInterval(user,
                startDate, correctedEndDate);
    }

    /**
     * Returns total spent effort for today.
     */
    private long getSpentEffortForToday() {
        DateTime today = getToday();
        return this.getSpentEffort(today, today);
    }

    /**
     * Returns total spent effort for yesterday.
     */
    private long getSpentEffortForYesterday() {
        DateTime yesterday = getToday().minusDays(1);
        return this.getSpentEffort(yesterday, yesterday);
    }

    /**
     * Returns total spent effort for this week.
     */
    private long getSpentEffortForThisWeek() {
        DateTime today = getToday();
        DateTime firstDayOfWeek = today.withDayOfWeek(DateTimeConstants.MONDAY);
        return this.getSpentEffort(firstDayOfWeek, today);
    }

    /**
     * Returns total spent effort for this month.
     */
    private long getSpentEffortForThisMonth() {
        DateTime today = getToday();
        DateTime firstDayOfMonth = today.withDayOfMonth(1);
        return this.getSpentEffort(firstDayOfMonth, today);
    }

    private DateTime getToday() {
        return new LocalDate().toDateTimeAtStartOfDay();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }
}
