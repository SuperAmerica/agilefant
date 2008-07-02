package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * Hibernate entity bean representing a back log. A Backlog is a work log that
 * can contain several tasks. A backlog item itself is in turn contained in a
 * backlog.
 * <p>
 * If the backlog, the backlog item belongs in, is an iteration, the backlog
 * item can be bound to an iteration goal.
 * <p>
 * Backlog item is a unit which, within a Cycle of Control model, is in interest
 * of project manager and the workers within her team.
 * <p>
 * Project manager is interested mainly in efforts that are still due to within
 * each Backlog item, to be able to check wether the workload of her team
 * (including each team member) seems to be too low, adequate or too high.
 * <p>
 * BacklogItem may be assigned to a named person, who then sees it and is later
 * interested in the progress of the BacklogItem.
 * <p>
 * Workers are mainly interested in BacklogItems as things to be done, usually
 * this means the sub-parts of the BacklogItems, namely Tasks, which have been
 * assigned to them. To know better, which BacklogItem should be tackled next,
 * there is a priority attached to a BacklogItem.
 * <p>
 * Backlog items may be tagged with themes.
 * 
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.Task
 * @see fi.hut.soberit.agilefant.model.BusinessTheme
 */
@Entity
@Table(name = "backlogitem")
public class BacklogItem implements PageItem, Assignable, EffortContainer, TimesheetLoggable {

    private int id;

    private Priority priority;

    private String name;

    private String description;

    private Backlog backlog;

    private Collection<Task> tasks = new HashSet<Task>();

    @Deprecated
    private User assignee;
    
    private Collection<User> responsibles = new ArrayList<User>();

    private State state = State.NOT_STARTED;
    
    private Collection<BusinessTheme> businessThemes = new HashSet<BusinessTheme>();

    private IterationGoal iterationGoal;

    private AFTime originalEstimate;

    private AFTime effortLeft;

    private AFTime effortSpent;
    
    /**
     * Returns effort left for this item.
     * 
     * @return effort left for this backlog item
     */

    @Type(type = "af_time")
    public AFTime getEffortLeft() {
        return effortLeft;
    }

    /**
     * Sets effort left for this item.
     * 
     * @param effort
     *                left for this backlog item
     */

    public void setEffortLeft(AFTime effortLeft) {
        this.effortLeft = effortLeft;
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
     * The id is unique among all backlog items.
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

    // @Column(nullable = false)
    @Type(type = "escaped_truncated_varchar")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the tasks belonging in this backlog item.
     * 
     * @return Collection of tasks belonging to this backlog item
     */
    @OneToMany(mappedBy = "backlogItem", fetch = FetchType.LAZY)
    @OrderBy(value="rank ASC")
    @Cascade(CascadeType.DELETE_ORPHAN)
    @BatchSize(size=20)
    public Collection<Task> getTasks() {
        return tasks;
    }

    /**
     * Sets the tasks belonging in this backlog item.
     * 
     * @param tasks
     *                Collection of tasks belonging to this backlog item
     */

    public void setTasks(Collection<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Returns the backlog, in which this backlog item belongs.
     * 
     * @return the backlog, in which this backlog item belongs
     */
    @ManyToOne
    @Cascade(CascadeType.REFRESH)
    @JoinColumn(nullable = false)
    public Backlog getBacklog() {
        return backlog;
    }

    /**
     * Sets the backlog, in which this backlog item belongs.
     * 
     * @param backlog
     *                the backlog, in which this backlog item belongs
     */

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    /** {@inheritDoc} */
    @Transient
    public Collection<PageItem> getChildren() {
        Collection<PageItem> c = new HashSet<PageItem>(this.tasks.size());
        c.addAll(this.tasks);
        return c;
    }

    /** {@inheritDoc} */
    @Transient
    public PageItem getParent() {
        // TODO: do some checks
        return (PageItem) getBacklog();
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        return this.tasks.size() > 0 ? true : false;
    }

    /** Backlog item priority. */
    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
            @Parameter(name = "useOrdinal", value = "true"),
            @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.Priority") })
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
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
     * Returns the state of the backlog item.
     * 
     * @return the state of the backlog item.
     */
    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
            @Parameter(name = "useOrdinal", value = "true"),
            @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.State") })
    public State getState() {
        return state;
    }

    /**
     * Sets the state of the backlog item.
     * 
     * @param state
     *                the state to set for the backlog item
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Gets the iteration goal of the backlog item.
     * 
     * @return iterationGoal the iteration goal to of the backlog item
     */
    @ManyToOne
    @JoinColumn(nullable = true)
    public IterationGoal getIterationGoal() {
        return iterationGoal;
    }

    /**
     * Sets the iteration goal of the backlog item.
     * 
     * @param iterationGoal
     *                the iteration goal to set for the backlog item
     */
    public void setIterationGoal(IterationGoal iterationGoal) {
        this.iterationGoal = iterationGoal;
    }

    /**
     * Returns the placeholder task of this backlog item. DO NOT USE THIS. IT
     * WILL BE REMOVED IN FOLLOWING VERSIONS.
     * 
     * @deprecated
     * @param placeHolder
     *                the placeHolder
     * @return the placeHolder
     */
    /*
     * @OneToOne @Cascade(CascadeType.DELETE_ORPHAN) public Task
     * getPlaceHolder() { return placeHolder; }
     */

    /**
     * Sets the placeholder task of this backlog items. DO NOT USE THIS. IT WILL
     * BE REMOVED IN FOLLOWING VERSIONS.
     * 
     * @deprecated
     * @param placeHolder
     *                the placeHolder to set
     */
    /*
     * public void setPlaceHolder(Task placeHolder) { this.placeHolder =
     * placeHolder; }
     */

    /**
     * Get the backlog items parent backlogs.
     * 
     * @param item
     *                backlog item whose parents are to be get.
     * @return list of parent backlogs.
     */
    @Transient
    public List<Backlog> getParentBacklogs() {
        List<Backlog> retlist = new ArrayList<Backlog>();
        Backlog firstParent = getBacklog();

        if (firstParent instanceof Iteration) {
            Project deli = ((Iteration) firstParent).getProject();
            Product prod = deli.getProduct();
            retlist.add(prod);
            retlist.add(deli);
            retlist.add(firstParent);
        } else if (firstParent instanceof Project) {
            Product prod = ((Project) firstParent).getProduct();
            retlist.add(prod);
            retlist.add(firstParent);
        } else if (firstParent instanceof Product) {
            retlist.add(firstParent);
        }
        return retlist;
    }

    /**
     * Returns the project related to this <code>BacklogItem</code> if there is one.
     * Otherwise returns <code>null</code>. This method is used to retrieve the project
     * which contains the users who can be responsible for this BacklogItem without violating the
     * "assign to project before assigning to BLI" -ideology.
     * @return
     */
    @Transient
    public Project getProject() {
        Backlog parent = getBacklog();
        if( parent instanceof Iteration ) {
            return ((Iteration)parent).getProject();
        } else if( parent instanceof Project ) {
            return (Project) parent;
        } else {
            return null;
        }
    }
    
    /**
     * Returns the original effort estimate for this backlog item.
     * 
     * @return the original effort estimate for this backlog item
     */
    @Type(type = "af_time")
    public AFTime getOriginalEstimate() {
        return originalEstimate;
    }

    /**
     * Sets the original effort estimate for this backlog item.
     * 
     * @param originalEstimate
     *                the original effort estimate for this backlog item
     */
    public void setOriginalEstimate(AFTime originalEstimate) {
        this.originalEstimate = originalEstimate;
    }
    
    /**
     * Get the users responsible for this backlog item.
     * @return collection of the responsible users
     */
    @ManyToMany(
            targetEntity = fi.hut.soberit.agilefant.model.User.class,
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "backlogitem_user",
            joinColumns={@JoinColumn(name = "BacklogItem_id")},
            inverseJoinColumns={@JoinColumn(name = "User_id")}
    )
    @OrderBy("initials")
    @BatchSize(size=20)
    public Collection<User> getResponsibles() {
        return responsibles;
    }

    /**
     * Set the users responsible for this backlog item.
     * @param responsibles list of users
     */
    public void setResponsibles(Collection<User> responsibles) {
        this.responsibles = responsibles;
    }

    /**
     * Get the total effort spent on this BacklogItem.
     */
    @Transient
    public AFTime getEffortSpent() {
        return effortSpent;
    }

    public void setEffortSpent(AFTime effortSpent) {
        this.effortSpent = effortSpent;
    }

    /**
     * Get the business themes tagged to this backlog item.
     * @return collection of the tagged business themes
     */
    @ManyToMany(
            targetEntity = fi.hut.soberit.agilefant.model.BusinessTheme.class,
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "backlogitem_businesstheme",
            joinColumns={@JoinColumn(name = "backlogitem_id")},
            inverseJoinColumns={@JoinColumn(name = "businesstheme_id")}
    )
    @BatchSize(size=20)
    public Collection<BusinessTheme> getBusinessThemes() {
        return businessThemes;
    }

    public void setBusinessThemes(Collection<BusinessTheme> businessThemes) {
        this.businessThemes = businessThemes;
    }
}
