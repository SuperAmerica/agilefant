package fi.hut.soberit.agilefant.model;

import java.util.Date;

import javax.persistence.Entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing an event where time estimate for a task is
 * changed.
 * <p>
 * Contains the new estimate.
 * <p>
 * Since the class inherits from TaskComment, some comment text may accompany
 * the new time estimate.
 * 
 * @see fi.hut.soberit.agilefant.model.TaskEvent
 * @see fi.hut.soberit.agilefant.model.TaskComment
 */
@Entity
public class EstimateHistoryEvent extends TaskComment {

    private AFTime newEstimate;

    private Log logger = LogFactory.getLog(getClass());

    public EstimateHistoryEvent() {
    }

    public EstimateHistoryEvent(User actor, Task task, Date created,
            AFTime newEstimate) {
        super(actor, task, created);
        this.newEstimate = newEstimate;
    }

    @Type(type = "af_time")
    public AFTime getNewEstimate() {
        return newEstimate;
    }

    public void setNewEstimate(AFTime newEstimate) {
        this.newEstimate = newEstimate;
    }
}
