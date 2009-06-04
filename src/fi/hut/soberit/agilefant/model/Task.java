package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
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
public class Task implements TimesheetLoggable {

    private int id;
    private String name;
    private String description;
    private Iteration iteration;
    private Story story;
    
    private State state;
    private Priority priority;
    
    private List<Todo> todos = new ArrayList<Todo>();
    private ExactEstimate effortLeft;
    private ExactEstimate originalEstimate;
    private List<TaskHistoryEntry> historyEntries = new ArrayList<TaskHistoryEntry>();
    private Collection<User> responsibles = new ArrayList<User>();

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

    public void setEffortLeft(ExactEstimate estimate) {
        this.effortLeft = estimate;
    }

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "effortleft")))
    public ExactEstimate getEffortLeft() {
        return effortLeft;
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

    @JSON
    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
                @Parameter(name = "useOrdinal", value = "true"),
                @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.State")
    })
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @ManyToMany(
            targetEntity = fi.hut.soberit.agilefant.model.User.class,
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "task_user"
    )
    @OrderBy("initials asc")
    @JSON(include = false)
    public Collection<User> getResponsibles() {
        return responsibles;
    }
    
    public void setResponsibles(Collection<User> responsibles) {
        this.responsibles = responsibles;
    }

    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
            @Parameter(name = "useOrdinal", value = "true"),
            @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.Priority")
    })
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
