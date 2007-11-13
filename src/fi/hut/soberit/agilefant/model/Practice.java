package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing a Practice. A task contains one or more
 * practices, linked trough PracticeAllocation. A practice in a task also has a
 * status.
 * <p>
 * Currently practices aren't implemented in the UI.
 * 
 * @see fi.hut.soberit.agilefant.model.PracticeAllocation
 * @see fi.hut.soberit.agilefant.model.PracticeTemplate
 * @see fi.hut.soberit.agilefant.model.PracticeStatus
 */
@Entity
public class Practice {

	private int id;

	private String name;

	private String description;

	private PracticeTemplate template;

	/**
	 * Get the id of this object.
	 * <p>
	 * The id is unique among all practices.
	 */
	// tag this field as the id
	@Id
	// generate automatically
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	@Type(type = "escaped_text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Type(type = "escaped_truncated_varchar")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ManyToOne
	public PracticeTemplate getTemplate() {
		return template;
	}

	public void setTemplate(PracticeTemplate template) {
		this.template = template;
	}

}
