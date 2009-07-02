package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.joda.time.Interval;

@Entity
public class Holiday {
    private int id;
    private User user;
    private Interval interval;

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    @ManyToOne
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    @Type(type = "org.joda.time.contrib.hibernate.PersistentInterval")
    public Interval getInterval() {
        return interval;
    }
    public void setInterval(Interval interval) {
        this.interval = interval;
    }
    
}
