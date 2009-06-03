package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

/**
 * Hibernate entity bean which represents an hour entry.
 * 
 * Conceptually, hour entry represents logged effort for a given user in given
 * date with given description. One entry can be associated with only one user.
 * Hour entry is the base information container for the timesheet functionality.
 * 
 * This is a base class for all logged efforts and thus has no information
 * whatsoever of the parent object. Child classes for HourEntry add ownership
 * information. Class is not defined abstract as it us used as common logged
 * effort information container.
 * 
 * @see fi.hut.soberit.agilefant.model.StoryHourEntry
 * @see fi.hut.soberit.agilefant.model.BacklogHourEntry
 * @author Pasi Pekkanen, Roni Tammisalo
 * 
 */
@BatchSize(size = 20)
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "hourentries")
public class HourEntry {

    private int id;

    private DateTime date;

    private long minutesSpent;

    private User user;

    private String description;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime getDate() {
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
    public int getId() {
        return this.id;
    }

    @ManyToOne(optional = false)
    public User getUser() {
        return this.user;
    }

    public void setDate(DateTime date) {
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

    public void setUser(User user) {
        this.user = user;
    }

    @Transient
    public boolean isBacklogEffortEntry() {
        return (this instanceof BacklogHourEntry);
    }

    @Transient
    public boolean isStoryEffortEntry() {
        return (this instanceof StoryHourEntry);
    }

    public long getMinutesSpent() {
        return minutesSpent;
    }

    public void setMinutesSpent(long minutesSpent) {
        this.minutesSpent = minutesSpent;
    }

}
