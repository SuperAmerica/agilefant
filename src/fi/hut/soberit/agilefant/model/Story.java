package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import flexjson.JSON;

@Entity
@Table(name = "stories")
@Audited
public class Story implements TimesheetLoggable, LabelContainer, NamedObject, TaskContainer {
    private int id;
    private String name;
    private String description;
    private Backlog backlog;
    private StoryState state = StoryState.NOT_STARTED;
//    private int rank = 0;
    private int treeRank = 0;
    private Story parent;
    private List<Story> children = new ArrayList<Story>();
    private Set<Label> labels = new HashSet<Label>();
    
    private Set<User> responsibles = new HashSet<User>();
    private Set<Task> tasks = new HashSet<Task>();
    private Set<StoryHourEntry> hourEntries = new HashSet<StoryHourEntry>();
    private Set<StoryRank> storyRanks = new HashSet<StoryRank>();

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
     * @return Set of the responsible users
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
    @NotAudited
    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
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
    public Set<StoryHourEntry> getHourEntries() {
        return hourEntries;
    }
    
    public void setHourEntries(Set<StoryHourEntry> hourEntries) {
        this.hourEntries = hourEntries;
    }

    @JSON
    @ManyToOne
    @Fetch(FetchMode.JOIN)
    public Story getParent() {
        return parent;
    }

    public void setParent(Story parent) {
        this.parent = parent;
    }

    @JSON(include=false)
    @OneToMany(mappedBy="parent", targetEntity=fi.hut.soberit.agilefant.model.Story.class)
    @Fetch(FetchMode.SELECT)
    @IndexColumn(name="treeRank")
    @NotAudited
    public List<Story> getChildren() {
        return children;
    }

    public void setChildren(List<Story> children) {
        this.children = children;
    }
    
    @JSON(include=false)
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, targetEntity=StoryRank.class, mappedBy="story")
    public Set<StoryRank> getStoryRanks() {
        return storyRanks;
    }

    public void setStoryRanks(Set<StoryRank> storyRanks) {
        this.storyRanks = storyRanks;
    }

    @Column(nullable = false, columnDefinition = "int default 0")
    @JSON
    public int getTreeRank() {
        return treeRank;
    }

    public void setTreeRank(int treeRank) {
        this.treeRank = treeRank;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    @OneToMany(mappedBy = "story")
    @NotAudited
    @JSON
    public Set<Label> getLabels() {
        return labels;
    }

}
