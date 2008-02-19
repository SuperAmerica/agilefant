package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.hibernate.validator.Range;

/**
 * Hibernate entity bean representing an activity type.
 * <p>
 * Conceptually, ProjectType represents a work entitity by defining some
 * WorkTypes which are applicable to this kind of an activity. Activity type of
 * "customer software project" might contain work types "planning", "coding" and
 * "customer support".
 * <p>
 * ProjectType has a target percentage, which is defined as the percentage of
 * all the work that should be spent to work which are under this particular
 * ProjectType, given by company leaders or such.
 * 
 * @see fi.hut.soberit.agilefant.model.WorkType
 */
@Entity
public class ProjectType implements Comparable<ProjectType> {

    private int id;

    private String name;

    private String description;

    private Collection<WorkType> workTypes = new HashSet<WorkType>();

    private int targetSpendingPercentage = 0;

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all activity types.
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

    @Type(type = "escaped_text")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(nullable = false, unique = true)
    @Type(type = "escaped_truncated_varchar")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Single activity type can contain many work types, single worktype is part
     * of a single activity type.
     */
    @OneToMany(mappedBy = "projectType")
    public Collection<WorkType> getWorkTypes() {
        return workTypes;
    }

    public void setWorkTypes(Collection<WorkType> workTypes) {
        this.workTypes = workTypes;
    }

    @Range(min = 0, max = 100)
    public int getTargetSpendingPercentage() {
        return targetSpendingPercentage;
    }

    public void setTargetSpendingPercentage(int targetSpendingPercentage) {
        this.targetSpendingPercentage = targetSpendingPercentage;
    }
    
    public int compareTo(ProjectType o) {
        if (o == null) {
            return -1;
        }
        return getName().compareTo(o.getName());
    }
}