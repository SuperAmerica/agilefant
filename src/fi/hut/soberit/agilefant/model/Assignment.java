package fi.hut.soberit.agilefant.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;

import flexjson.JSON;

@BatchSize(size=20)
@Entity
@Table(name = "assignment")
public class Assignment implements Serializable {
    private static final long serialVersionUID = 5391104304173714927L;
     
    private int id;
    private Project project;
    private User user;
    private ExactEstimate personalLoad;

    /**
     * Deviation from project's default overhead.
     */
    public void setPersonalLoad(ExactEstimate personalLoad) {
        this.personalLoad = personalLoad;
    }

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "delta_personal_load")))
    public ExactEstimate getPersonalLoad() {
        return personalLoad;
    }
    
    public Assignment() {
    }

    public Assignment(User user, Project project) {
        this.user = user;
        this.project = project;
    }

    @ManyToOne
    @JSON(include = false)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @ManyToOne
    @JSON(include = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JSON(include = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
