package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Hibernate entity bean representing a practice template.
 * <p>
 * PracticeTemplate is a collection of practices. PracticeTemplate can be
 * applied to a task (Task.useTemplate), where all practices contained in the
 * template are added in the task.
 * <p>
 * Currently practices aren't implemented in the UI.
 * 
 * @see fi.hut.soberit.agilefant.model.Practice
 * @see fi.hut.soberit.agilefant.model.PracticeAllocation
 * @see fi.hut.soberit.agilefant.model.PracticeStatus
 */
@Entity
public class PracticeTemplate {

    private int id;

    private Collection<Practice> practices = new HashSet<Practice>();

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all practice templates.
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

    @OneToMany(mappedBy = "template")
    public Collection<Practice> getPractices() {
        return practices;
    }

    public void setPractices(Collection<Practice> practices) {
        this.practices = practices;
    }

}
