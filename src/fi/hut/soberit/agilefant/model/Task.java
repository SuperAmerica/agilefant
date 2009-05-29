package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import flexjson.JSON;

/**
 * An entity bean representing a task.
 * @author rjokelai
 *
 */

@Entity
@Table(name = "tasks")
public class Task {

    private int id;
    private String name;
    private String description;
    private Iteration iteration;
    private Story story;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    @Type(type = "escaped_truncated_varchar")
    @JSON
    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name;
    }

    @Type(type = "escaped_truncated_varchar")
    @JSON
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    @JSON(include = false)
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    @JSON(include = false)
    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }
}
