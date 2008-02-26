package fi.hut.soberit.agilefant.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

@Entity
public class Assignment implements Serializable {
    private static final long serialVersionUID = 5391104304173714927L;
    
    private int id;
    private Backlog backlog;
    private User user;
    private AFTime deltaOverhead;

    /**
     * Deviation from project's default overhead.
     */
    @Type(type = "af_time")
    public AFTime getDeltaOverhead() {
        return deltaOverhead;
    }

    public void setDeltaOverhead(AFTime deltaOverhead) {
        this.deltaOverhead = deltaOverhead;
    }

    public Assignment() {
    }

    public Assignment(User user, Backlog backlog) {
        this.user = user;
        this.backlog = backlog;
    }

    @ManyToOne
    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    @ManyToOne
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

}
