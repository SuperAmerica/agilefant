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

import org.hibernate.annotations.Type;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="backlogtype",
    discriminatorType=DiscriminatorType.STRING
)
/**
 * Through Backlog, BacklogItems are appendable 
 * as a child for the implementing object.
 */
public abstract class Backlog implements Assignable {
    
    private int id;
    private String name;
    private String description;
    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();
    private User assignee;
    
    @OneToMany(mappedBy="backlog")
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
    
    @Id 
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(nullable = false)	    
    public int getId() {
        return id;
    }
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
    
    @ManyToOne
	public User getAssignee() {
		return assignee;
	}
    
	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}
}
