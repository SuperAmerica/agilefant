package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.BatchSize;

/**
 * Hibernate entity bean which represents an hour entry owned by a backlog item.
 * 
 * Represents a job effort logged for a specific backlog item.
 * 
 * @see fi.hut.soberit.agilefant.model.HourEntry
 * @author User
 * 
 */
@Entity
@BatchSize(size = 20)
public class StoryHourEntry extends HourEntry {

    private Story story;

    @ManyToOne(optional = false)
    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

}
