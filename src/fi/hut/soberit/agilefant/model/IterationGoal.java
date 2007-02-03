package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
public class IterationGoal {

	private int id;
	private Iteration iteration;
	private String name;
	private String description;
	private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();
	private Integer priority;
	
	// the default status is "looking good"
	private IterationGoalStatus status = IterationGoalStatus.LOOKING_GOOD;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable = false)	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}	
	
	@Type(type="truncated_string")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Type(type="text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn (nullable = false)
	public Iteration getIteration() {
		return iteration;
	}

	public void setIteration(Iteration iteration) {
		this.iteration = iteration;
	}

	@Type(type="fi.hut.soberit.agilefant.db.hibernate.EnumUserType",
			parameters = {
				@Parameter(name="useOrdinal", value="true"),
				@Parameter(name="enumClassName", value="fi.hut.soberit.agilefant.model.IterationGoalStatus")
			}
	)				
	public IterationGoalStatus getStatus() {
		return status;
	}

	public void setStatus(IterationGoalStatus status) {
		this.status = status;
	}
	
	@OneToMany(mappedBy="iterationGoal")
	public Collection<BacklogItem> getBacklogItems() {
		return backlogItems;
	}

	public void setBacklogItems(Collection<BacklogItem> backlogItems) {
		this.backlogItems = backlogItems;
	}

	@Column(nullable = true)
	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}	
}
