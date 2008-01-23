package fi.hut.soberit.agilefant.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Assignment implements Serializable {
    private static final long serialVersionUID = 5391104304173714927L;
    
    private int id;
    private Backlog backlog;
    private User user;

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
