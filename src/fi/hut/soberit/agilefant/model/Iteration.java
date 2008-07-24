package fi.hut.soberit.agilefant.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.BatchSize;

import fi.hut.soberit.agilefant.util.BacklogMetrics;
import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * A Hibernate entity bean which represents an iteration.
 * <p>
 * Conceptually, an iteration is a type of a backlog. A iteration-backlog
 * represents work (backlog items, tasks) to be done during an iteration.
 * Iteration is a time period, a conceptual tool, used to divide and manage
 * work. It's usually a few weeks in length.
 * <p>
 * Since a project is a backlog, it can contain backlog items, which, in turn,
 * are smaller containers for work. An iteration is a part of a bigger work
 * container, the project.
 * <p>
 * An iteration is part of a project. Start- and ending dates can be defined, as
 * well as effort estimate.
 * <p>
 * An iteration can contain some iteration goals to which underlying backlog
 * items can be bound to. Iteration goals are higher level concepts. Multiple
 * backlog items can work towards a single iteration goal.
 * 
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.IterationGoal
 */
@Entity
@BatchSize(size=20)
public class Iteration extends Backlog implements PageItem {

    private Date startDate;

    private Date endDate;

    private Project project;

    private Collection<IterationGoal> iterationGoals = new HashSet<IterationGoal>();
    
    private BacklogMetrics metrics;

    // private User owner;

    //private Log logger = LogFactory.getLog(getClass());

    /** The project, under which this iteration is. */
    @ManyToOne
    // @JoinColumn (nullable = false)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // @Column(nullable = false)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(String endDate, String dateFormat)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setLenient(true);
        this.endDate = df.parse(endDate);
    }

    // @Column(nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(String startDate, String dateFormat)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setLenient(true);
        this.startDate = df.parse(startDate);
    }
    
    /** {@inheritDoc} */
    @Transient
    public Collection<PageItem> getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Transient
    public PageItem getParent() {
        // TODO Auto-generated method stub
        return getProject();
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        // TODO Auto-generated method stub
        return false;
    }

    @OneToMany(mappedBy = "iteration")
    @OrderBy("priority asc")
    public Collection<IterationGoal> getIterationGoals() {
        return iterationGoals;
    }

    public void setIterationGoals(Collection<IterationGoal> iterationGoals) {
        this.iterationGoals = iterationGoals;
    }

    /**
     * Returns the sum of Iteration's sub-backlogs' items' effort left. Returns
     * 0, since Iteration has no sub-backlogs.
     */
    @Transient
    public AFTime getSubBacklogEffortLeftSum() {
        return new AFTime(0);
    }

    /**
     * Returns the sum of Iteration's sub-backlogs' items' original estimate.
     * Returns 0, since Iteration has no subBacklogs.
     */
    @Transient
    public AFTime getSubBacklogOriginalEstimateSum() {
        return new AFTime(0);
    }

    @Transient
    public BacklogMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(BacklogMetrics metrics) {
        this.metrics = metrics;
    }

}