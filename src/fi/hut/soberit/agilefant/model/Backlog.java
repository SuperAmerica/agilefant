package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

/**
 * Abstract entity, a Hibernate entity bean, which represents a backlog.
 * <p>
 * All other entities providing backlog functionality inherit from this class.
 * Product, Deliverable and Iteration are all backlogs.  
 * <p>
 * Conceptually, a backlog is a work log, which can contain some backlog items, 
 * which in turn can contain some tasks. An example hierarchy would be
 * <p>
 * backlog: "iteration 3" <br>  
 * backlog item : "saving implemented" <br> 
 * task: "implement saving .foo files" <br>
 * <p>
 * Through Backlog, BacklogItems are appendable 
 * as a child for the implementing object.
 * 
 * @see fi.hut.soberit.agilefant.model.Product
 * @see fi.hut.soberit.agilefant.model.Deliverable
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.Task
 */
@Entity
// inheritance implemented in db using a single table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
// subclass types discriminated using string column 
@DiscriminatorColumn(
    name="backlogtype",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class Backlog implements Assignable {
    
    private int id;
    private String name;
    private String description;
    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();
    private User assignee;
    private AFTime totalEstimate; 
    
    @OneToMany(mappedBy="backlog")
    /** A backlog can contain many backlog items. */
    public Collection<BacklogItem> getBacklogItems() {
        return backlogItems;
    }
    public void setBacklogItems(Collection<BacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }
    
    @Type(type="escaped_text")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
	/** 
	 * Get the id of this object.
	 * <p>
	 * The id is unique among all Backlogs. 
	 */
	// tag this field as the id
	@Id
	// generate automatically
	@GeneratedValue(strategy=GenerationType.AUTO)
	// not nullable
	@Column(nullable = false)
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
    
    @Type(type="escaped_truncated_varchar")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    /** {@inheritDoc} */
    @ManyToOne
	public User getAssignee() {
		return assignee;
	}
    
    /** {@inheritDoc} */
	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}
		
	/**
	 * Get the "total estimate" for this backlog item. 
	 * <p>
	 * The custom estimate for a backlog item, in the UI, equals BacklogItem.effortEstimate, 
	 * the summed estimate from contained tasks, if it's not null and >0. 
	 * Otherwise, it's BacklogItem.allocatedEffort. The total estimate for a backlog is sum of 
	 * these.
	 */
	@Type(type="af_time")
	@Formula(value =
	"( select SUM(IF((select SUM(t.effortEstimate) FROM Task t WHERE t.backlogItem_id = b.id), " +
	"(select SUM(t.effortEstimate) FROM Task t WHERE t.backlogItem_id = b.id), IFNULL(b.remainingEffortEstimate, 0))) " +
	"from BacklogItem b where b.backlog_id = id )")	
	public AFTime getTotalEstimate() {
		return totalEstimate;
	}
	public void setTotalEstimate(AFTime totalEstimate) {
		this.totalEstimate = totalEstimate;
	}
}
