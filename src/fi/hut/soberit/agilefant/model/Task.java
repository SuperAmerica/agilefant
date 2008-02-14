package fi.hut.soberit.agilefant.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * Hibernate entity bean representing a Task.
 * <p>
 * Conceptually task represents some work which is no further divided to smaller
 * pieces. It's work of a single person. A task is under a backlog item, which
 * is a bigger container of work.
 * <p>
 * Technically there's one-to-many relation between backlog item and a task. It
 * has a creator and an assignee.
 * <p>
 * Task is a unit which, within a Cycle of Control model, is in interest of
 * workers of a team, and sometimes their project manager also. Task is a
 * sub-part of a BacklogItem, and may be assigned to a named person.
 * <p>
 * Workers are interested in Tasks which have been assigned to them as things to
 * be done. To know better, which Task should be tackled next, there is a
 * priority attached to a Task. Task has a capability to log efforts done to it.
 * <p>
 * Project manager is generally more interested in BacklogItems than Tasks, but
 * in small projects, of for personal interests, may want to see the progress of
 * a single Task, too. Also, planning the future work to be assigned, it may be
 * useful for a Project manager to see the multitude of the tasks assigned to
 * each worker, to be able to balance the workload within her crew.
 */
@Entity
public class Task implements PageItem {

    private int id;

    private Priority priority;

    private State state = State.NOT_STARTED;

    private String name;

    private String description;

    private BacklogItem backlogItem;

    private User creator;
    
    private Integer rank;

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
     * The id is unique among all tasks.
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

    @ManyToOne
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    public BacklogItem getBacklogItem() {
        return backlogItem;
    }

    public void setBacklogItem(BacklogItem backlogItem) {
        this.backlogItem = backlogItem;
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
        return getBacklogItem();
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        // TODO Auto-generated method stub
        return false;
    }

    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
            @Parameter(name = "useOrdinal", value = "true"),
            @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.Priority") })
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
            @Parameter(name = "useOrdinal", value = "true"),
            @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.State") })
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    
    // @Column(nullable = false)
    @Transient
    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

}