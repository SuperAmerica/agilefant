package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.util.BacklogItemPriorityComparator;

/**
 * Abstract entity, a Hibernate entity bean, which represents a backlog.
 * <p>
 * All other entities providing backlog functionality inherit from this class.
 * Product, Deliverable and Iteration are all backlogs.
 * <p>
 * Conceptually, a backlog is a work log, which can contain some backlog items,
 * which in turn can contain some tasks. An example hierarchy would be
 * <p>
 * backlog: "iteration 3" <br>
 * backlog item : "saving implemented" <br>
 * task: "implement saving .foo files" <br>
 * <p>
 * Through Backlog, BacklogItems are appendable as a child for the implementing
 * object.
 * 
 * @see fi.hut.soberit.agilefant.model.Product
 * @see fi.hut.soberit.agilefant.model.Deliverable
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.Task
 */
@Entity
// inheritance implemented in db using a single table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// subclass types discriminated using string column
@DiscriminatorColumn(name = "backlogtype", discriminatorType = DiscriminatorType.STRING)
public abstract class Backlog implements Assignable {

    private int id;

    private String name;

    private String description;

    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();

    private Collection<EffortHistory> effortHistory = new HashSet<EffortHistory>();

    private User assignee;

    private AFTime totalEstimate;

    private AFTime bliEffortLeftSum;

    private AFTime bliOrigEstSum;

    @OneToMany(mappedBy = "backlog")
    /** A backlog can contain many backlog items. */
    public Collection<BacklogItem> getBacklogItems() {
        return backlogItems;
    }

    @Transient
    /**
     * Return a sorted list of backlog items. Items are sorted first by priority
     * and then by status.
     */
    public Collection<BacklogItem> getSortedBacklogItems() {
        /* Create two arraylists for temporarily storing the elements */
        ArrayList<BacklogItem> sortedList = new ArrayList<BacklogItem>();
        ArrayList<BacklogItem> doneItems = new ArrayList<BacklogItem>();

        /* Iterate through the list of backlog's backlogItems */
        Iterator<BacklogItem> iter = this.getBacklogItems().iterator();
        while (iter.hasNext()) {
            BacklogItem bli = iter.next();

            /*
             * If backlog item is marked as done, put it to doneItems-list,
             * otherwise add it to sorted list.
             */
            if (bli.getPlaceHolder().getStatus() == TaskStatus.DONE) {
                doneItems.add(bli);
            } else {
                sortedList.add(bli);
            }
        }

        /* Sort both lists by priority, highest priority first */
        BacklogItemPriorityComparator c = new BacklogItemPriorityComparator();
        Collections.sort(sortedList, c);
        Collections.sort(doneItems, c);

        /* Add all done items to the end of the list */
        sortedList.addAll(doneItems);

        return sortedList;
    }

    public void setBacklogItems(Collection<BacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }

    @Type(type = "escaped_text")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all Backlogs.
     */
    // tag this field as the id
    @Id
    // generate automatically
    @GeneratedValue(strategy = GenerationType.AUTO)
    // not nullable
    @Column(nullable = false)
    public int getId() {
        return id;
    }

    /**
     * Set the id of this object.
     * <p>
     * You shouldn't normally call this.
     */
    public void setId(int id) {
        this.id = id;
    }

    @Type(type = "escaped_truncated_varchar")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @ManyToOne
    public User getAssignee() {
        return assignee;
    }

    /** {@inheritDoc} */
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * Get the "total estimate" for this backlog.
     * <p>
     * The custom estimate for a backlog item, in the UI, equals
     * BacklogItem.effortEstimate, the summed estimate from contained tasks, if
     * it's not null and >0. Otherwise, it's BacklogItem.allocatedEffort. The
     * total estimate for a backlog is sum of these.
     */
    @Type(type = "af_time")
    @Formula(value = "( select SUM(IF((select SUM(t.effortEstimate) FROM Task t WHERE t.backlogItem_id = b.id), "
            + "(select SUM(t.effortEstimate) FROM Task t WHERE t.backlogItem_id = b.id), IFNULL(b.remainingEffortEstimate, 0))) "
            + "from BacklogItem b where b.backlog_id = id )")
    public AFTime getTotalEstimate() {
        return totalEstimate;
    }

    public void setTotalEstimate(AFTime totalEstimate) {
        this.totalEstimate = totalEstimate;
    }

    /**
     * Get all effortHistory objects related to this backlog
     * 
     * @return the effortHistory
     */
    @OneToMany(mappedBy = "backlog")
    @Cascade(CascadeType.DELETE_ORPHAN)
    public Collection<EffortHistory> getEffortHistory() {
        return effortHistory;
    }

    /**
     * Get the BLI effort left sum of this backlog
     * 
     * @return the bli effor left sum
     */
    @Transient
    public AFTime getBliEffortLeftSum() {
        long bliEffortLeftSum = 0;
        for (BacklogItem i : this.getBacklogItems()) {
            if (i.getBliEffEst() != null) {
                bliEffortLeftSum += i.getBliEffEst().getTime();
            }
            if (i.getTaskSumEffEst() != null) {
                bliEffortLeftSum += i.getTaskSumEffEst().getTime();
            }
        }
        return new AFTime(bliEffortLeftSum);
    }

    /**
     * @param effortHistory
     *                the effortHistory to set
     */
    public void setEffortHistory(Collection<EffortHistory> effortHistory) {
        this.effortHistory = effortHistory;
    }

    /**
     * Returns default performed effort for backlog. Override in child classes
     * to provide functionality.
     * 
     * @return the performed effort for a backlog
     */
    @Transient
    public AFTime getPerformedEffort() {
        return new AFTime(0);
    }

    /**
     * Calculates the BliOrigEstSum by summing the max of bliOrigEst and
     * taskSumOrigEst.
     * 
     * @return the bliOrigEstSum the original estimate sum of the bli
     */
    @Transient
    public AFTime getBliOrigEstSum() {
        long bliOrigEstSum = 0L;
        long taskSum = 0L;
        long bliSum = 0L;
        for (BacklogItem i : this.getBacklogItems()) {
            if (i.getBliOrigEst() != null) {
                bliSum = i.getBliOrigEst().getTime();
            } else {
                bliSum = 0L;
            }
            if (i.getTaskSumOrigEst() != null) {
                taskSum = i.getTaskSumOrigEst().getTime();
            } else {
                taskSum = 0L;
            }
            bliOrigEstSum += Math.max(bliSum, taskSum);
        }
        return new AFTime(bliOrigEstSum);
    }

    /**
     * Return default start date
     * 
     * @return default start date (epoc)
     */
    @Transient
    public Date getStartDate() {
        return new Date(0);
    }
}
