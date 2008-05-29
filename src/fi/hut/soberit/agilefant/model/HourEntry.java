package fi.hut.soberit.agilefant.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;


@BatchSize(size=20)
@Entity
@Table(name = "hourentry")
public class HourEntry {

    private int id;
    
    private Date date;
    
    private AFTime timeSpent;
    
    private User user;
     
    private String description;
    
    public Date getDate() {
        return this.date;
    }
    
    @Type(type = "escaped_text")
    public String getDescription() {
        return this.description;
    }
    
    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all tasks.
     */
    // tag this field as the id
    @Id
    // generate automatically
    @GeneratedValue(strategy = GenerationType.AUTO)
    // not nullable
    @Column(nullable = false)
    public int getId() {
        return this.id;
    }
    
    @Type(type = "af_time")
    public AFTime getTimeSpent() {
        return this.timeSpent;
    }
    

    @ManyToOne
    public User getUser() {
        return this.user;
    }
    
    public void setDate(Date date) { 
        this.date = date;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Set the id of this object.
     * <p>
     * You shouldn't normally call this.
     */
    public void setId(int id) {
        this.id = id;
    }
    
    public void setTimeSpent(AFTime timeSpent) {
        this.timeSpent = timeSpent;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
}
