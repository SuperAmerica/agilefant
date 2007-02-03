package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
public class Practice {
	
	private int id;
	private String name;
	private String description;
	private PracticeTemplate template;

	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable = false)	
	public int getId() {
	    return id;
	}
	public void setId(int id) {
	    this.id = id;
	}

	@Type(type="text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Type(type="truncated_string")
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
