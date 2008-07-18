package fi.hut.soberit.agilefant.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.util.ProjectMetrics;
import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * A Hibernate entity bean which represents a project.
 * <p>
 * Conceptually, a project is a type of a backlog. A project-backlog represents
 * work (iterations, backlog items, tasks) to be done towards some project
 * outcome (documents, code, plans, etc.).
 * <p>
 * A project is further divided up to smaller containers for work, the
 * iterations. Project also is a part of a bigger container, the product. Since
 * a project is a backlog, it can contain backlog items, which, in turn, are
 * smaller containers for work.
 * <p>
 * Example projects would be "Acme KillerApp v1.3" or "User Documentation".
 * <p>
 * A project is part of a product. It can contain iterations. It has an optional
 * starting and ending dates, as well as an owner. A project is also bound to
 * some activity type. It also carries information on effort estimations. A
 * project has a rank number, which corresponds to its priority. The rank number
 * doesn't describe the project's absolute rank order; the number must be
 * compared to all other project's ranks to find out rank order.
 * 
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.ProjectType
 * @see fi.hut.soberit.agilefant.model.Iteration
 */
@Entity
@BatchSize(size=20)
public class Project extends Backlog implements PageItem {

    private Product product;

    private ProjectType projectType;

    private Date endDate;

    private Date startDate;

    private List<Iteration> iterations = new ArrayList<Iteration>();

    private User owner;

    private int rank = 0;
    
    private AFTime defaultOverhead;
    
    private Status status = Status.OK;
    
    private ProjectMetrics metrics;

    /** The product, under which this project belongs. */
    @ManyToOne
    // @JoinColumn (nullable = true)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @ManyToOne
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    /** Iterations under this project. */
    @OneToMany(mappedBy = "project")
    @OrderBy(clause = "startDate asc, endDate asc")
    @BatchSize(size=20)
    public List<Iteration> getIterations() {
        return iterations;
    }

    public void setIterations(List<Iteration> iterations) {
        this.iterations = iterations;
    }

    // @Column(nullable = false)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    // TODO: Refactor to business layer and remove dateformat parameter    
    public void setStartDate(String startDate, String dateFormat)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setLenient(true);
        this.startDate = df.parse(startDate);
    }

    // @Column(nullable = false)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    // TODO: Refactor to business layer and remove dateformat parameter
    public void setEndDate(String endDate, String dateFormat)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setLenient(true);
        this.endDate = df.parse(endDate);
    }

    @ManyToOne
    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    /** {@inheritDoc} */
    @Transient
    public List<PageItem> getChildren() {
        List<PageItem> c = new ArrayList<PageItem>(this.iterations.size());
        c.addAll(this.iterations);
        return c;
    }

    /** {@inheritDoc} */
    @Transient
    public PageItem getParent() {
        return getProduct();
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        return this.iterations.size() > 0 ? true : false;
    }

    @Column(nullable = false, columnDefinition = "integer not null default 0")
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Returns the sum of Project's Iteration's items' effort left.
     */
    @Transient
    public AFTime getSubBacklogEffortLeftSum() {
        AFTime result = new AFTime(0);
        Iterator<Iteration> it = iterations.iterator();
        while (it.hasNext()) {
            Iteration iter = it.next();
            // this is actually redundant, but here for completeness.
            result.add(iter.getSubBacklogEffortLeftSum());
            result.add(iter.getBliEffortLeftSum());
        }
        return result;
    }

    /**
     * Returns the sum of Project's Iteration's items' original estimate.
     */
    @Transient
    public AFTime getSubBacklogOriginalEstimateSum() {
        AFTime result = new AFTime(0);
        Iterator<Iteration> it = iterations.iterator();
        while (it.hasNext()) {
            Iteration iter = it.next();
            // this is actually redundant, but here for completeness.
            result.add(iter.getSubBacklogOriginalEstimateSum());
            result.add(iter.getBliOriginalEstimateSum());
        }
        return result;
    }

    /**
     * Default overhead value for each person assigned to this project
     */
    @Type(type = "af_time")
    public AFTime getDefaultOverhead() {
        return defaultOverhead;
    }

    public void setDefaultOverhead(AFTime defaultOverhead) {
        this.defaultOverhead = defaultOverhead;
    }

    /**
     * Returns the status of the project.
     * 
     * @return the status of the project.
     */
    @Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
            @Parameter(name = "useOrdinal", value = "true"),
            @Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.Status") })
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Transient
    public ProjectMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(ProjectMetrics metrics) {
        this.metrics = metrics;
    }
}
