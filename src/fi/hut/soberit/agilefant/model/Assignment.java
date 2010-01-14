package fi.hut.soberit.agilefant.model;

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
import org.hibernate.envers.Audited;

import flexjson.JSON;

@BatchSize(size=20)
@Entity
@Table(name = "assignment")
@Audited
public class Assignment {
     
    private int id;
    private Backlog backlog;
    private User user;
    private SignedExactEstimate personalLoad;
    private int availability = 100;

    /**
     * Deviation from project's default overhead.
     */
    public void setPersonalLoad(SignedExactEstimate personalLoad) {
        this.personalLoad = personalLoad;
    }

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "delta_personal_load")))
    public SignedExactEstimate getPersonalLoad() {
        return personalLoad;
    }
    
    public Assignment() {
    }

    public Assignment(User user, Backlog backlog) {
        this.user = user;
        this.backlog = backlog;
    }

    @ManyToOne
    @JSON(include = false)
    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

}
