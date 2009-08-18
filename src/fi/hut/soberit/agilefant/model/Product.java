package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Filter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate entity bean representing a product.
 * <p>
 * Conceptually, a product is a type of a backlog. A project-backlog represents
 * work (projects, iterations, stories) done / to be done for the
 * product.
 * <p>
 * A product contains projects, which are some partial outcomes of the product.
 * For example, different versions of the product or some documentation.
 * <p>
 * Product is at the top level of the hierarchy and thus is the biggest container
 * of work. Since a project is a backlog, it can contain stories, which,
 * in turn, are smaller containers for work.
 * <p>
 * An example product would be "Acme WordProcessor" or "Agilefant 07".
 * 
 * @see fi.hut.soberit.agilefant.model.Project
 */
@Entity
@BatchSize(size = 20)
@Audited
public class Product extends Backlog {
    
    private Collection<Project> projects = new ArrayList<Project>();
    
    private Collection<Iteration> iterations = new ArrayList<Iteration>();
    
    @OneToMany(mappedBy="parent", targetEntity=Backlog.class)
    @Filter(name="project", condition="class=Project")
    @Transactional(readOnly=true)
    @NotAudited
    public Collection<Project> getProjects() {
        return this.projects;
    }
    
    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    @OneToMany(mappedBy="parent", targetEntity=Backlog.class)
    @Filter(name="iteration", condition="class=Iteration")
    @Transactional(readOnly=true)
    @NotAudited
    public Collection<Iteration> getIterations() {
        return iterations;
    }

    public void setIterations(Collection<Iteration> iterations) {
        this.iterations = iterations;
    }

}
