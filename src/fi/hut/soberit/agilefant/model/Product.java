package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OrderBy;

import fi.hut.soberit.agilefant.util.BacklogMetrics;
import fi.hut.soberit.agilefant.web.page.PageItem;
import flexjson.JSON;

/**
 * Hibernate entity bean representing a product.
 * <p>
 * Conceptually, a product is a type of a backlog. A project-backlog
 * represents work (projects, iterations, backlog items, tasks) done / to be
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
public class Product extends Backlog implements PageItem {

    private List<Project> projects = new ArrayList<Project>();
    
    private Set<BusinessTheme> businessThemes = new HashSet<BusinessTheme>();

    /** Get the collection of projects belonging to this product. */
    @OneToMany(mappedBy = "product")
    @OrderBy(clause = "startDate asc, endDate asc")
    @BatchSize(size=20)
    @JSON(include = false)
    public List<Project> getProjects() {
        return projects;
    }

    /** Set the collection of projects belonging to this product. */
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /** {@inheritDoc} */
    @Transient
    @JSON(include = false)
    public List<PageItem> getChildren() {
        List<PageItem> c = new ArrayList<PageItem>(this.projects.size());
        c.addAll(this.projects);
        return c;
    }

    /** {@inheritDoc} */
    @Transient
    @JSON(include = false)
    public PageItem getParent() {

        // We don't really want to show portfolio as root
        // return new PortfolioPageItem();
        return null;
    }
    
    @Override
    /**
     * Since Product has no end date, return null.
     * @return always null
     */
    @Transient
    @JSON
    public Date getEndDate() {
        return null;
    }
    
    @Override
    public void setEndDate(Date endDate) {        
    }

    /** {@inheritDoc} */
    @Transient
    @JSON(include = false)
    public boolean hasChildren() {
        return this.projects.size() > 0 ? true : false;
    }
    
    /**
     * Returns the sum of Product's sub-backlogs' items' effort left.
     */
    @Transient
    @Deprecated
    @SuppressWarnings("deprecation")
    @JSON(include = false)
    public AFTime getSubBacklogEffortLeftSum() {
        AFTime result = new AFTime(0);
        
        for (Project proj : projects) {
            result.add(proj.getSubBacklogEffortLeftSum());
            result.add(proj.getBliEffortLeftSum());
        }
        return result;
    }
    
    /**
     * Returns the sum of Product's sub-backlogs' items' original estimate.
     */
    @Transient
    @Deprecated
    @SuppressWarnings("deprecation")
    @JSON(include = false)
    public AFTime getSubBacklogOriginalEstimateSum() {
        AFTime result = new AFTime(0);
        Iterator<Project> it = projects.iterator();
        while(it.hasNext()) {
            Project proj = it.next();
            result.add(proj.getSubBacklogOriginalEstimateSum());
            result.add(proj.getBliOriginalEstimateSum());
        }
        return result;
    }

    @OneToMany(mappedBy = "product")
    @OrderBy(clause = "name asc")
    @BatchSize(size=20)
    @JSON(include = false)
    public Set<BusinessTheme> getBusinessThemes() {
        return businessThemes;
    }

    public void setBusinessThemes(Set<BusinessTheme> businessThemes) {
        this.businessThemes = businessThemes;
    }
    
    @Transient
    @JSON
    public Integer getBacklogSize() {
        return null;
    }
    
    @Transient
    public Collection<BacklogThemeBinding> getBusinessThemeBindings() {
        return null;
    }

    @Override
    @Transient
    public BacklogMetrics getMetrics() {
        return null;
    }
}
        
