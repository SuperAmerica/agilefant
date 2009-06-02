package fi.hut.soberit.agilefant.model;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;

import flexjson.JSON;

/**
 * An entity bean representing a task.
 * 
 * @author rjokelai
 * 
 */

@Entity
@Table(name = "tasks")
public class Task {

    private int id;
    private String name;
    private String description;
    private Iteration iteration;
    private Story story;
    private List<Todo> todos;
    private ExactEstimate estimate;
    private ExactEstimate originalEstimate;
    private List<TaskHistoryEntry> historyEntries;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Type(type = "escaped_truncated_varchar")
    @JSON
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JSON(include = false)
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    @ManyToOne
    @JSON(include = false)
    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

    @OneToMany(mappedBy = "task")
    @IndexColumn(name = "rank")
    public List<Todo> getTodos() {
        return todos;
    }

    public void setEstimate(ExactEstimate estimate) {
        this.estimate = estimate;
    }

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "estimate")))
    public ExactEstimate getEstimate() {
        return estimate;
    }

    public void setHistoryEntries(List<TaskHistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }

    @OneToMany(mappedBy = "task")
    public List<TaskHistoryEntry> getHistoryEntries() {
        return historyEntries;
    }

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "originalestimate")))
    public ExactEstimate getOriginalEstimate() {
        return originalEstimate;
    }

    public void setOriginalEstimate(ExactEstimate originalEstimate) {
        this.originalEstimate = originalEstimate;
    }

}
