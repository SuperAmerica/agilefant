package fi.hut.soberit.agilefant.model;

import java.sql.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

/**
 * Data model for backlog's time estimate history. Backlog items and tasks have
 * original estimates and effort left estimates for how much work is to be done.
 */
@Entity
public class EffortHistory {
    private int id;

    private Backlog backlog;

    private Date date;

    private AFTime effortLeft;

    private AFTime originalEstimate;

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all effortHistory items.
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

    /** The backlog which this effortHistory represents */
    @ManyToOne
    @JoinColumn(nullable = false)
    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    /**
     * Date reports when effortHistory was saved
     * 
     * @return date when effortHistory was saved
     */
    @Basic
    public Date getDate() {
        return this.date;
    }

    /**
     * Set the Date
     * 
     * @param date
     *                date to be set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * EffortLeft is the estimation of the work left and it's continually
     * re-evaluated
     * 
     * @return the effortLeft
     */
    @Type(type = "af_time")
    public AFTime getEffortLeft() {
        return effortLeft;
    }

    /**
     * @param effortLeft
     *                the effortLeft to set
     */
    public void setEffortLeft(AFTime effortLeft) {
        this.effortLeft = effortLeft;
    }

    /**
     * OriginalEstimate is the initial estimate of work left and this value
     * should not be updated
     * 
     * @return the originalEstimate
     */
    @Type(type = "af_time")
    public AFTime getOriginalEstimate() {
        return originalEstimate;
    }

    /**
     * @param originalEstimate
     *                the originalEstimate to set
     */
    public void setOriginalEstimate(AFTime originalEstimate) {
        this.originalEstimate = originalEstimate;
    }

}
