package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.util.IterationGoalMetrics;
import fi.hut.soberit.agilefant.web.page.PageItem;
import flexjson.JSON;

/**
 * A Hibernate entity bean which represents an iteration goal.
 * <p>
 * Conceptually, iteration goals divide an iteration to higher level goals than
 * backlog items. Several backlog items belonging to an iteration might work
 * tovards the same goal.
 * <p>
 * Example iteration goal would be "documentation finished", bound to "finish
 * javadocs" and "finish user documentation" - backlog items.
 * <p>
 * The IterationGoal is contained under an iteration. Its can be linked to
 * several backlog items. It also has a priority number among its peers under
 * the same iteration, smaller number meaning more important goal.
 * 
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 */
@Entity
@Table(name = "iterationgoal")
public class IterationGoal implements PageItem {

    private int id;
    
    private Iteration iteration;

    private String name;

    private String description;

    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();

    private Integer priority = new Integer(0);
    
    private IterationGoalMetrics metrics = new IterationGoalMetrics();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    @JSON
    public int getId() {
        return id;
    }

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

    @Type(type = "escaped_text")
    @JSON
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        return false;
    }

    /** {@inheritDoc} */
    @Transient
    @JSON(include = false)
    public Collection<PageItem> getChildren() {
        return null;
    }

    /** {@inheritDoc} */
    @Transient
    @JSON(include = false)
    public PageItem getParent() {
        return getIteration();
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    @JSON(include = false)
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    @OneToMany(mappedBy = "iterationGoal")
    @JSON(include = false)
    public Collection<BacklogItem> getBacklogItems() {
        return backlogItems;
    }

    public void setBacklogItems(Collection<BacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }

    @Column(nullable = false)
    @JSON(include = true)
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Transient
    @JSON(include=true)
    public IterationGoalMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(IterationGoalMetrics metrics) {
        this.metrics = metrics;
    }
}
