package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import org.hibernate.annotations.Cascade;
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
public class Task implements TimesheetLoggable, NamedObject, Rankable {

    private int id;
    private String name;
    private String description;
    private Iteration iteration;
    private Story story;
    
    private TaskState state;
    private int rank = 0;
//    private Priority priority;
    
    private ExactEstimate effortLeft;
    private ExactEstimate originalEstimate;
    private List<User> responsibles = new ArrayList<User>();
    private Set<TaskHourEntry> hourEntries = new HashSet<TaskHourEntry>();
    private Set<WhatsNextEntry> whatsNextEntries;

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
    public List<User> getResponsibles() {
        return responsibles;
    }
    
    public void setResponsibles(List<User> responsibles) {
        this.responsibles = responsibles;
    }

    @NotAudited
    @OneToMany(
            targetEntity = fi.hut.soberit.agilefant.model.WhatsNextEntry.class,
            fetch = FetchType.LAZY,
            mappedBy = "task"
    )
    @Cascade(value = { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JSON(include = false)
    public Set<WhatsNextEntry> getWhatsNextEntries() {
        return whatsNextEntries;
    }
    
    public void setWhatsNextEntries(Set<WhatsNextEntry> entries) {
        this.whatsNextEntries = entries;
    }

    @OneToMany(mappedBy="task")
    @OrderBy("date desc")
    @NotAudited
    public Set<TaskHourEntry> getHourEntries() {
        return hourEntries;
    }
    
    public void setHourEntries(Set<TaskHourEntry> hourEntries) {
        this.hourEntries = hourEntries;
    }

    @Column(nullable = false, columnDefinition = "int default 0")
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }

}
