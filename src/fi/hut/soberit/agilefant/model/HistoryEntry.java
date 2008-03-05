package fi.hut.soberit.agilefant.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "historyentry")
public class HistoryEntry<T extends History<?>> {
    private int id;
   
    private T history;
    private Date date;
    private AFTime effortLeft;
    private AFTime originalEstimate;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    @ManyToOne(targetEntity=History.class)
    public T getHistory() {
        return history;
    }
    public void setHistory(T history) {
        this.history = history;
    }
    
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) { 
        this.date = date;
    }
    
    @Type(type = "af_time")
    public AFTime getEffortLeft() {
        return effortLeft;
    }
    public void setEffortLeft(AFTime effortLeft) {
        this.effortLeft = effortLeft;
    }
    
    @Type(type = "af_time")
    public AFTime getOriginalEstimate() {
        return originalEstimate;
    }
    public void setOriginalEstimate(AFTime originalEstimate) {
        this.originalEstimate = originalEstimate;
    }
}
