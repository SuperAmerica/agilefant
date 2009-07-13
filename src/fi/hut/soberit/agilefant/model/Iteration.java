package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

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
public class Iteration extends Backlog implements Schedulable {
 
    private Date startDate;

    private Date endDate;
    
    private Integer backlogSize;
    
    private Collection<Assignment> assignments = new ArrayList<Assignment>();
    
    private Collection<Task> tasks = new ArrayList<Task>();

    private Collection<IterationHistoryEntry> historyEntries = new ArrayList<IterationHistoryEntry>();
    
    @JSON
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @JSON
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @JSON
    public Integer getBacklogSize() {
        return backlogSize;
    }

    public void setBacklogSize(Integer backlogSize) {
        this.backlogSize = backlogSize;
    }

    public void setTasks(Collection<Task> tasks) {
        this.tasks = tasks;
    }
    
    @OneToMany(mappedBy = "iteration")
    @JSON(include = false)
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
    public Collection<IterationHistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    public void setHistoryEntries(Collection<IterationHistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }
    
}