package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.joda.time.DateTime;

import flexjson.JSON;

/**
 * A Hibernate entity bean which represents an iteration.
 * <p>
 * Conceptually, an iteration is a type of a backlog. A iteration-backlog
 * represents work (stories, tasks) to be done during an iteration.
 * Iteration is a time period, a conceptual tool, used to divide and manage
 * work. It's usually a few weeks in length.
 * <p>
 * Since a project is a backlog, it can contain stories, which, in turn,
 * are smaller containers for work. An iteration is a part of a bigger work
 * container, the project.
 * <p>
 * An iteration is part of a project. Start- and ending dates can be defined, as
 * well as effort estimate.
 * 
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.Story
 * @see fi.hut.soberit.agilefant.model.Iteration
 */
@Entity
@BatchSize(size = 20)
@Audited
public class Iteration extends Backlog implements Schedulable {
 
    private DateTime startDate;

    private DateTime endDate;
    
    private ExactEstimate backlogSize = new ExactEstimate(0);
    
    private Collection<Assignment> assignments = new ArrayList<Assignment>();
    
    private Collection<Task> tasks = new ArrayList<Task>();

    private Collection<IterationHistoryEntry> historyEntries = new ArrayList<IterationHistoryEntry>();
    
    private ExactEstimate baselineLoad = new ExactEstimate(0);
    
    
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    @JSON
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime getEndDate() {
        return endDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    @JSON
    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime getStartDate() {
        return startDate;
    }


    @JSON
    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "backlogSize")))
    public ExactEstimate getBacklogSize() {
        return backlogSize;
    }

    public void setBacklogSize(ExactEstimate backlogSize) {
        this.backlogSize = backlogSize;
    }

    public void setTasks(Collection<Task> tasks) {
        this.tasks = tasks;
    }
    
    @OneToMany(mappedBy = "iteration")
    @JSON(include = false)
    @NotAudited
    public Collection<Task> getTasks() {
        return tasks;
    }
    
    @OneToMany(targetEntity = fi.hut.soberit.agilefant.model.Assignment.class,
            mappedBy = "backlog")
    @JSON(include = false)
    public Collection<Assignment> getAssignments() {
        return assignments;
    }
    
    public void setAssignments(Collection<Assignment> assignments) {
        this.assignments = assignments;
    }
 
    @OneToMany(mappedBy = "iteration")
    @JSON(include = false)
    @Cascade(CascadeType.DELETE)
    @NotAudited
    public Collection<IterationHistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(Collection<IterationHistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }
    
    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "baselineLoad")))
    public ExactEstimate getBaselineLoad() {
        return baselineLoad;
    }

    public void setBaselineLoad(ExactEstimate baselineLoad) {
        this.baselineLoad = baselineLoad;
    }
    
}