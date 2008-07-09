package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing a work type.
 * <p>
 * WorkType is a category for a work to be done, reported in Task level.
 * Percentages of WorkTypes done may be telling whether the company is doing
 * what is thought it should be doing.
 * <p>
 * WorkType belongs in an activity type, and there're several work types for a
 * single activity type. Activity type of "customer software project" might
 * contain work types "planning", "coding" and "customer support".
 * 
 * @see fi.hut.soberit.agilefant.model.ProjectType
 */
@Entity
@BatchSize(size=20)
@Table(name = "worktype")
public class WorkType {

    private int id;

    private String name;

    private String description;

    private ProjectType projectType;

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all work types.
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

    @ManyToOne
    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    @Type(type = "escaped_text")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false)
    @Type(type = "escaped_truncated_varchar")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
