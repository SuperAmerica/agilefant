package fi.hut.soberit.agilefant.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing a Todo.
 * <p>
 * Conceptually todo represents some work which is no further divided to smaller
 * pieces. It's work of a single person. A todo is under a backlog item, which
 * is a bigger container of work.
 * <p>
 * Technically there's one-to-many relation between backlog item and a todo. It
 * has a creator and an assignee.
 * <p>
 * Todo is a unit which, within a Cycle of Control model, is in interest of
 * workers of a team, and sometimes their project manager also. Todo is a
 * sub-part of a BacklogItem, and may be assigned to a named person.
 * <p>
 * Workers are interested in todos which have been assigned to them as things to
 * be done. To know better, which todo should be tackled next, there is a
 * priority attached to a todo. Todo has a capability to log efforts done to it.
 * <p>
 * Project manager is generally more interested in BacklogItems than todos, but
 * in small projects, of for personal interests, may want to see the progress of
 * a single todo, too. Also, planning the future work to be assigned, it may be
 * useful for a Project manager to see the multitude of the todos assigned to
 * each worker, to be able to balance the workload within her crew.
 */
@BatchSize(size=20)
@Entity
@Table(name = "todos")
public class Todo {

    private int id;

    //private Priority priority;

    private State state = State.NOT_STARTED;

    private String name;

    private String description;

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
     * The id is unique among all todos.
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

    /** {@inheritDoc} */
    @Transient
    public Collection<Backlog> getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Transient
    public Backlog getParent() {
        return null;
        //return getBacklogItem();
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        // TODO Auto-generated method stub
        return false;
    }
/*
    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
            @Parameter(name = "useOrdinal", value = "true"),
            @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.Priority") })
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
*/
    @Enumerated(EnumType.ORDINAL)
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    
    @Column 
    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

}