package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import flexjson.JSON;

/**
 * An entity bean representing a task.
 * 
 * @author rjokelai
 * 
 */

@Entity
@Table(name = "tasks")
@Audited
public class Task implements TimesheetLoggable, NamedObject {

    private int id;
    private String name;
    private String description;
    private Iteration iteration;
    private Story story;
    
    private TaskState state;
    private Priority priority;
    
    private ExactEstimate effortLeft = new ExactEstimate();
    private ExactEstimate originalEstimate = new ExactEstimate();
    private Collection<User> responsibles = new ArrayList<User>();
    private Collection<TaskHourEntry> hourEntries = new ArrayList<TaskHourEntry>();
    
    private Date createdDate;
    private User creator;

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

    @JSON
    @Type(type = "escaped_text")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    @JSON(include = false)
    @NotAudited
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    @ManyToOne
    @JSON(include = false)
    @NotAudited
    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public void setEffortLeft(ExactEstimate estimate) {
        this.effortLeft = estimate;
    }

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "effortleft")))
    public ExactEstimate getEffortLeft() {
        return effortLeft;
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
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
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

    @NotAudited
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
    @JSON(include = true)
    @NotAudited
    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @OneToMany(mappedBy="task")
    @OrderBy("date desc")
    @NotAudited
    public Collection<TaskHourEntry> getHourEntries() {
        return hourEntries;
    }
    
    public void setHourEntries(Collection<TaskHourEntry> hourEntries) {
        this.hourEntries = hourEntries;
    }
}
