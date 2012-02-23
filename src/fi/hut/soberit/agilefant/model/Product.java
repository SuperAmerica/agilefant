package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.transaction.annotation.Transactional;

import flexjson.JSON;

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
@XmlRootElement
@XmlAccessorType( XmlAccessType.NONE )
public class Product extends Backlog {
    
    private Collection<Team> teams = new HashSet<Team>();
    
    /**
     * Get the product's teams
     * 
     * return the teams
     */
    @ManyToMany(targetEntity = Team.class)
    @JoinTable(name = "team_product", joinColumns = { @JoinColumn(name = "Product_id") }, inverseJoinColumns = { @JoinColumn(name = "Team_id") })
    @BatchSize(size = 5)
    @JSON(include = false)
    @NotAudited
    public Collection<Team> getTeams() {
        return teams;
    }
    
    /**
     * Set the team's products.
     * 
     * @param products the products to be set
     */
    public void setTeamss(Collection<Team> teams) {
        this.teams = teams;
    }
    
    @Transactional(readOnly=true)
    @Transient
    @XmlElement(name = "projects")
    @XmlElementWrapper
    public Collection<Project> getProjects() {
        List<Project> projects = new ArrayList<Project>();
        for(Backlog bl : this.getChildren()) {
            if(bl instanceof Project) {
                projects.add((Project)bl);
            }
        }
        return projects;
    }
    
    @Transactional(readOnly=true)
    @Transient
    public Collection<Iteration> getIterations() {
        List<Iteration> iterations = new ArrayList<Iteration>();
        for(Backlog bl : this.getChildren()) {
            if(bl instanceof Iteration) {
                iterations.add((Iteration)bl);
            }
        }
        return iterations;    
    }
}
