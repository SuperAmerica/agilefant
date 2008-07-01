package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;


/**
 * 
 * Hibernate entity bean representing a theme. 
 * <p>
 * Backlog items can be tagged with a theme to group them according to a
 * common theme.
 * <p>
 * A theme's name is appended to the items' name.
 * 
 * Note: the word "Theme" is a reserved word, do not ever rename this to Theme!
 * 
 * @see fi.hut.soberit.agilefant.model.BusinessTheme
 *
 */
@Entity
@BatchSize(size=20)
@Table(name = "businesstheme")
public class BusinessTheme implements Comparable<BusinessTheme> {

    private int id;

    private String name;

    private String description;
        
    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all themes.
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

    public void setId(int id) {
        this.id = id;
    }

    @Column(nullable = false, unique = true)
    @Type(type = "escaped_truncated_varchar")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Type(type = "escaped_text")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Themes are compared by their names.
     */
    public int compareTo(BusinessTheme o) {
        if (o == null) {
            return -1;
        }
        return getName().compareTo(o.getName());
    }
}
