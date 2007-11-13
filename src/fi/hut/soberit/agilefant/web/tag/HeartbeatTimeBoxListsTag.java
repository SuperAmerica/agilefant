package fi.hut.soberit.agilefant.web.tag;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.UserDAO;

/**
 * A tag for requesting task and backlog item lists for current user, inside
 * given timeframe. Lists all tasks and backlog items, belonging to a
 * deliverable or iteration, duration of which intersects with given timeframe.
 * Also tasks belonging to a product or a deliverable/iteration with no duration
 * specified are listed.
 * 
 * <p>
 * If the timeframe is not specified
 * 
 * @author Turkka Äijälä
 */
public class HeartbeatTimeBoxListsTag extends SpringTagSupport {
    private static final long serialVersionUID = 5445839193289241981L;

    public static final String UNFINISHED_TASK_LIST_KEY = "unfinishedTaskList";

    public static final String BACKLOGITEMS_LIST_KEY = "backlogItemsList";

    private UserDAO userDAO;

    private int id;

    private Date startTime = null;

    private Date endTime = null;

    /**
     * Java Date represents time in millisecond accuracy, even though you would
     * expect a day accuracy. This is used to set the time within the day.
     * 
     * @param date
     *                date to modify
     * @param hour
     *                hour within the day to set
     * @param minute
     *                minute within the hour to set
     * @param second
     *                second within the minute to set
     * @param msec
     *                millisecond within the second to set
     * @return update date
     */
    private Date setClock(Date date, int hour, int minute, int second, int msec) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, hour);

        return c.getTime();
    }

    @Override
    public int doStartTag() throws JspException {

        Date startTime = null;
        Date endTime = null;

        if (this.startTime != null && this.endTime != null) {
            startTime = this.startTime;
            endTime = this.endTime;
        } else {
            startTime = setClock(new Date(), 0, 0, 0, 0);
            endTime = setClock(new Date(), 23, 59, 59, 999);
        }

        userDAO = (UserDAO) super.getApplicationContext().getBean("userDAO");

        super.getPageContext().setAttribute(
                HeartbeatTimeBoxListsTag.UNFINISHED_TASK_LIST_KEY,
                userDAO.getUnfinishedTasksByTime(userDAO.get(id), startTime,
                        endTime));

        super.getPageContext().setAttribute(
                HeartbeatTimeBoxListsTag.BACKLOGITEMS_LIST_KEY,
                userDAO.getBacklogItemsByTime(userDAO.get(id), startTime,
                        endTime));

        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setUserId(int id) {
        this.id = id;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

}
