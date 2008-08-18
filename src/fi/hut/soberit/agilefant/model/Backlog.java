package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.util.BacklogItemPriorityComparator;
import fi.hut.soberit.agilefant.web.page.PageItem;
import flexjson.JSON;

/**
 * Abstract entity, a Hibernate entity bean, which represents a backlog.
 * <p>
 * All other entities providing backlog functionality inherit from this class.
 * Product, Project and Iteration are all backlogs.
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
 * @see fi.hut.soberit.agilefant.model.Project
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.Task
 */
@BatchSize(size=20)
@Entity
// inheritance implemented in db using a single table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// subclass types discriminated using string column
@DiscriminatorColumn(name = "backlogtype", discriminatorType = DiscriminatorType.STRING)
@Table(name = "backlog")
public abstract class Backlog implements Assignable, TimesheetLoggable, PageItem {

    private int id;

    private String name;

    private String description;

    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();

    private User assignee;

    private BacklogHistory backlogHistory = new BacklogHistory();

    private Collection<Assignment> assignments = new HashSet<Assignment>();

    @OneToMany(mappedBy = "backlog", fetch = FetchType.LAZY)
    /** A backlog can contain many backlog items. */
    @JSON(include = false)
    public Collection<BacklogItem> getBacklogItems() {
        return backlogItems;
    }

    @Transient
    /**
     * Return a sorted list of backlog items. Items are sorted first by priority
     * and then by state.
     */
    @JSON(include = false)
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
            if (bli.getState() == State.DONE) {
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
    @JSON
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
    @JSON
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
    @JSON
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @ManyToOne
    @JSON(include = false)
    public User getAssignee() {
        return assignee;
    }

    /** {@inheritDoc} */
    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    /**
     * Get the sum of backlog items' effort left contained in this backlog. DOES
     * NOT include efforts contained in sub-Backlogs. For example this sum does
     * not include effort contained in Project's iterations.
     * 
     * @return the BLI effort left sum
     * @deprecated Use the history directly rather than through a convenience
     *             method.
     */
    @Transient
    @Deprecated
    @JSON(include = false)
    public AFTime getBliEffortLeftSum() {
        return backlogHistory.getCurrentEffortLeft();
    }

    /**
     * Returns the original estimate sum of all this Backlog's BacklogItems.
     * Does Not include original estimates of sub-Backlogs. Does not calculate
     * the sum here, just returns the field's value.
     * 
     * @return the bliOrigEstSum the original estimate sum of the bli
     */
    @Transient
    @JSON(include = false)
    public AFTime getBliOriginalEstimateSum() {
        return backlogHistory.getCurrentOriginalEstimate();
    }

    /**
     * Returns the cumulative effort left contained in all BacklogItems under
     * this Backlog's sub-Backlogs.
     * 
     * @return The cumulative effort left contained in all backlogitems,
     *         including those in sub-backlogs.
     */
    @Transient
    @JSON(include = false)
    abstract public AFTime getSubBacklogEffortLeftSum();

    /**
     * Returns the cumulative original estimate contained in all BacklogItems
     * under this Backlog's sub-Backlogs.
     * 
     * @return The cumulative original estimate contained in all backlogitems,
     *         including those in sub-backlogs.
     */
    @Transient
    @JSON(include = false)
    abstract public AFTime getSubBacklogOriginalEstimateSum();

    /**
     * Returns the cumulative effort left sum and bli effort left sum, added
     * together.
     */
    @Transient
    @Deprecated
    @JSON(include = false)
    public AFTime getTotalEffortLeftSum() {
        AFTime result = new AFTime(0);
        result.add(this.getSubBacklogEffortLeftSum());
        result.add(this.getBliEffortLeftSum());
        return result;

    }

    /**
     * Returns the cumulative original estimate sum and bli original estimate
     * sum, added together.
     */
    @Transient
    @Deprecated
    @JSON(include = false)
    public AFTime getTotalOriginalEstimateSum() {
        AFTime result = new AFTime(0);
        result.add(this.getSubBacklogOriginalEstimateSum());
        result.add(this.getBliOriginalEstimateSum());
        return result;
    }

    /**
     * Return default start date
     * 
     * @return default start date (epoc)
     */
    @Transient
    @JSON
    public Date getStartDate() {
        return new Date(0);
    }
    
    /**
     * Return the end date.
     * @return
     */
    @Transient
    @JSON(include = false)
    public abstract Date getEndDate();
    
    @Transient
    public abstract void setEndDate(Date endDate);

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "history_fk")
    @Cascade(CascadeType.ALL)
    @JSON(include = false)
    public BacklogHistory getBacklogHistory() {
        return backlogHistory;
    }

    public void setBacklogHistory(BacklogHistory backlogHistory) {
        this.backlogHistory = backlogHistory;
    }

    @OneToMany(mappedBy = "backlog")
    @JSON(include = false)
    public Collection<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Collection<Assignment> assignments) {
        this.assignments = assignments;
    }
    /**
     * Convenience method for accessing users assigned for this project.
     * @return
     */
    @Transient
    @JSON(include = false)
    public List<User> getResponsibles() {
        List<User> users = new ArrayList<User>(assignments.size());
        for( Assignment assignment : assignments ) {
            users.add(assignment.getUser());
        }
        return users;
    }
    @Transient
    @JSON
    public abstract Integer getBacklogSize();
    
    @Transient
    public abstract Collection<BacklogThemeBinding> getBusinessThemeBindings();
}
