package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class PracticeTemplate {
	
	private int id;	
	private Collection<Practice> practices = new HashSet<Practice>();
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable = false)	
	public int getId() {
	    return id;
	}

	public void setId(int id) {
	    this.id = id;
	}

	@OneToMany(mappedBy="template")
	public Collection<Practice> getPractices() {
		return practices;
	}

	public void setPractices(Collection<Practice> practices) {
		this.practices = practices;
	}
	
	
}
