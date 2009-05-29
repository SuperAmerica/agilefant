package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;

/**
 * Hibernate entity bean representing a product.
 * <p>
 * Conceptually, a product is a type of a backlog. A project-backlog
 * represents work (projects, iterations, backlog items, todos) done / to be
 * done for the product.
 * <p>
 * A product contains projects, which are some partial outcomes of the
 * product. For example, different versions of the product or some
 * documentation.
 * <p>
 * Product is at the top level of the hiearchy and thus is the biggest container
 * of work. Since a project is a backlog, it can contain backlog items,
 * which, in turn, are smaller containers for work.
 * <p>
 * An example product would be "Acme WordProcessor" or "Agilefant 07".
 * 
 * @see fi.hut.soberit.agilefant.model.Project
 */
@Entity
@BatchSize(size=20)
public class Product extends Backlog {
       
    private List<Story> stories = new ArrayList<Story>();

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }
        
    @OneToMany(mappedBy = "product")
    public Collection<Story> getStories() {
        return stories;
    }
}
        
