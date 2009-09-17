package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import flexjson.JSON;

@Entity
@Table(name = "stories")
@Audited
public class Story implements TimesheetLoggable, NamedObject, Rankable, TaskContainer {
    private int id;
    private String name;
    private String description;
    private Backlog backlog;
    private StoryState state = StoryState.NOT_STARTED;
    private int rank = 0;
    private Story parent;
    private Collection<Story> children;
    
    private Set<User> responsibles = new HashSet<User>();
    private Collection<Task> tasks = new ArrayList<Task>();
    private Collection<StoryHourEntry> hourEntries = new ArrayList<StoryHourEntry>();

    private Integer storyPoints;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Type(type = "escaped_text")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne(optional = false)
    @NotAudited
    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    @Column(nullable = false)
    public StoryState getState() {
        return state;
    }

    public void setState(StoryState state) {
        this.state = state;
    }

    public void setResponsibles(Set<User> responsibles) {
        this.responsibles = responsibles;
    }

    /**
     * Get the users responsible for this story item.
     * @return collection of the responsible users
     */
    @ManyToMany(
            targetEntity = fi.hut.soberit.agilefant.model.User.class
    )
    @JoinTable(
            name = "story_user",
            joinColumns={@JoinColumn(name = "Story_id")},
            inverseJoinColumns={@JoinColumn(name = "User_id")}
    )
    @BatchSize(size=20)
    @Fetch(FetchMode.SUBSELECT)
    public Set<User> getResponsibles() {
        return responsibles;
    }
    
    @OneToMany(targetEntity = fi.hut.soberit.agilefant.model.Task.class,
            mappedBy = "story"
    )
    @Cascade(CascadeType.DELETE_ORPHAN)
    @NotAudited
    public Collection<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Collection<Task> tasks) {
        this.tasks = tasks;
    }

    @JSON
    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    @OneToMany(mappedBy = "story",
            targetEntity = fi.hut.soberit.agilefant.model.StoryHourEntry.class )
    @NotAudited
    @OrderBy("date desc")
    public Collection<StoryHourEntry> getHourEntries() {
        return hourEntries;
    }
    
    public void setHourEntries(Collection<StoryHourEntry> hourEntries) {
        this.hourEntries = hourEntries;
    }

    @JSON
    @Column(nullable = false, columnDefinition = "int default 0")
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    @JSON(include=false)
    @ManyToOne
    public Story getParent() {
        return parent;
    }

    public void setParent(Story parent) {
        this.parent = parent;
    }

    @JSON(include=false)
    @OneToMany(mappedBy="parent", targetEntity=fi.hut.soberit.agilefant.model.Story.class)
    @Fetch(FetchMode.SELECT)
    @NotAudited
    public Collection<Story> getChildren() {
        return children;
    }

    public void setChildren(Collection<Story> children) {
        this.children = children;
    }
}
