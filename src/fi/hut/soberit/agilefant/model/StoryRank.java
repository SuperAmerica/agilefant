package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "storyrank", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "backlog_id", "story_id" }) })
public class StoryRank {

    private int id;
    private Story story;
    private Backlog backlog;
    private StoryRank next;
    private StoryRank previous;

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(optional = false)
    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    @ManyToOne(optional = false)
    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    @ManyToOne(optional = true)
    public StoryRank getNext() {
        return next;
    }

    public void setNext(StoryRank next) {
        this.next = next;
    }

    @ManyToOne(optional = true)
    public StoryRank getPrevious() {
        return previous;
    }

    public void setPrevious(StoryRank previous) {
        this.previous = previous;
    }
}
