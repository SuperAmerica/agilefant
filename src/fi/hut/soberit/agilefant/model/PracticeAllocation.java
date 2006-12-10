package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

/**
 * PracticeAllocation is an "allocation" of a practice into a task.
 * <p>
 * A Task can't directly reference to a practice, since the state
 * of a practice must be contained somewhere. Thus there's an extra 
 * step of practice allocations.
 */
@Entity
public class PracticeAllocation {

	private int id;	
	private Task task;
	private Practice practice;
	private PracticeStatus status = PracticeStatus.NOT_STARTED;

	PracticeAllocation() {}
	
	PracticeAllocation(Practice practice, Task task) {
		this.practice = practice;
		this.task = task;
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
	
	@Type(type="fi.hut.soberit.agilefant.db.hibernate.EnumUserType",
			parameters = {
				@Parameter(name="useOrdinal", value="true"),
				@Parameter(name="enumClassName", value="fi.hut.soberit.agilefant.model.PracticeStatus")
			}
	)			
	public PracticeStatus getStatus() {
		return status;
	}

	public void setStatus(PracticeStatus status) {
		this.status = status;
	}

	@OneToOne
	public Practice getPractice() {
		return practice;
	}

	public void setPractice(Practice practice) {
		this.practice = practice;
	}

	@ManyToOne
	@JoinColumn (nullable = true)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	
	
}
