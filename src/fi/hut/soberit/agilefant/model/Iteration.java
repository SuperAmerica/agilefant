package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;

import flexjson.JSON;

/**
 * A Hibernate entity bean which represents an iteration.
 * <p>
 * Conceptually, an iteration is a type of a backlog. A iteration-backlog
 * represents work (backlog items, todos) to be done during an iteration.
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
@BatchSize(size = 20)
public class Iteration extends Backlog {

    private Date startDate;

    private Date endDate;

    private Project project;

    private List<Story> stories = new ArrayList<Story>();

//    /** The project, under which this iteration is. */
//    @ManyToOne(optional = false)
//    @JSON(include = false)
//    public Project getProject() {
//        return project;
//    }
//
//    public void setProject(Project project) {
//        this.project = project;
//    }

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

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    @OneToMany(mappedBy = "iteration")
    public List<Story> getStories() {
        return stories;
    }

}