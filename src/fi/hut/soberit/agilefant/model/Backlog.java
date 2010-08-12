package fi.hut.soberit.agilefant.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import flexjson.JSON;

/**
 * Abstract entity, a Hibernate entity bean, which represents a backlog.
 * <p>
 * All other entities providing backlog functionality inherit from this class.
 * Product, Project and Iteration are all backlogs.
 * <p>
 * Conceptually, a backlog is a work log, which can contain some stories,
 * which in turn can contain some tasks. An example hierarchy would be
 * <p>
 * backlog: "iteration 3" <br>
 * story : "saving implemented" <br>
 * task: "implement saving .foo files" <br>
 * <p>
 * Through Backlog, Stories are appendable as a child for the implementing
 * object.
 * 
 * @see fi.hut.soberit.agilefant.model.Product
 * @see fi.hut.soberit.agilefant.model.Project
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.Story
 * @see fi.hut.soberit.agilefant.model.Todo
 */
@BatchSize(size=20)
@Entity
// inheritance implemented in db using a single table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// subclass types discriminated using string column
@DiscriminatorColumn(name = "backlogtype", discriminatorType = DiscriminatorType.STRING)
@Table(name = "backlogs")
@Audited
@XmlAccessorType( XmlAccessType.NONE )
public abstract class Backlog implements TimesheetLoggable, NamedObject {

    private int id;

    private String name;

    private String description;

    private Backlog parent;
    
    private Set<Backlog> children = new HashSet<Backlog>();
    
    private Set<Story> stories = new HashSet<Story>();
    
    private Set<BacklogHourEntry> hourEntries = new HashSet<BacklogHourEntry>();
    
    private Set<StoryRank> storyRanks = new HashSet<StoryRank>();
    
    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all Backlogs.
     */
    // tag this field as the id
    @Id
    // generate automatically
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JSON
    public int getId() {
        return id;
    }

    /**
     * Set the id of this object.
     * <p>
     * You shouldn't normally call this.
     */
    public void setId(int id) {
        this.id = id;
    }

    @Type(type = "escaped_truncated_varchar")
    @JSON
    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Type(type = "escaped_text")
    @JSON
    @XmlElement
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Get the backlog's parent backlog.
     * @return the parent backlog
     */
    @JSON(include = false)
    @ManyToOne
    public Backlog getParent() {
        return parent;
    }

    /**
     * Set the backlog's parent backlog.
     * @param parent the parent backlog
     */
    public void setParent(Backlog parent) {
        this.parent = parent;
    }

    /**
     * Set the backlog's child backlogs.
     * @param children
     */
    public void setChildren(Set<Backlog> children) {
        this.children = children;
    }

    /**
     * Get the backlog's child backlogs.
     * @return
     */
    @OneToMany(mappedBy = "parent")
    @NotAudited
    @XmlElementWrapper
    @XmlElement(name = "backlog")
    public Set<Backlog> getChildren() {
        return children;
    }
   
    @OneToMany(mappedBy = "backlog")
    @NotAudited
    @XmlElementWrapper
    @XmlElement(name = "story")
    public Set<Story> getStories() {
        return stories;
    }
    
    public void setStories(Set<Story> stories) {
        this.stories = stories;
    }

    @OneToMany(mappedBy="backlog")
    @NotAudited
    public Set<BacklogHourEntry> getHourEntries() {
        return hourEntries;
    }

    public void setHourEntries(Set<BacklogHourEntry> hourEntries) {
        this.hourEntries = hourEntries;
    }

    @JSON(include=false)
    @NotAudited
    @OneToMany(fetch = FetchType.LAZY, mappedBy="backlog", targetEntity=StoryRank.class, cascade=CascadeType.REMOVE)
    public Set<StoryRank> getStoryRanks() {
        return storyRanks;
    }

    public void setStoryRanks(Set<StoryRank> storyRanks) {
        this.storyRanks = storyRanks;
    }
}
