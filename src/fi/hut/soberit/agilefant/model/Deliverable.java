package fi.hut.soberit.agilefant.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * A Hibernate entity bean which represents a deliverable.
 * <p>
 * Conceptually, a deliverable is a type of a backlog. A deliverable-backlog
 * represents work (iterations, backlog items, tasks) to be done towards some
 * project outcome (documents, code, plans, etc.).
 * <p>
 * A deliverable is further divided up to smaller containers for work, the
 * iterations. Deliverable also is a part of a bigger container, the product.
 * Since a deliverable is a backlog, it can contain backlog items, which, in
 * turn, are smaller containers for work.
 * <p>
 * Example deliverables would be "Acme KillerApp v1.3" or "User Documentation".
 * <p>
 * A deliverable is part of a product. It can contain iterations. It has an
 * optional starting and ending dates, as well as an owner. A deliverable is
 * also bound to some activity type. It also carries information on effort
 * estimations and amount of performed work. A deliverable has a rank number,
 * which corresponds to its priority. The rank number doesn't describe the
 * deliverable's absolute rank order; the number must be compared to all other
 * deliverable's ranks to find out rank order.
 * 
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.ActivityType
 * @see fi.hut.soberit.agilefant.model.Iteration
 */
@Entity
public class Deliverable extends Backlog implements PageItem, EffortContainer {

    private Product product;

    private ActivityType activityType;

    private Date endDate;

    private Date startDate;

    private List<Iteration> iterations = new ArrayList<Iteration>();

    private User owner;

    private AFTime effortEstimate;

    private AFTime performedEffort;

    private int rank = 0;

    /** The product, under which this deliverable belongs. */
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

    /** Iterations under this deliverable. */
    @OneToMany(mappedBy = "deliverable")
    @OrderBy(clause = "startDate asc, endDate asc")
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

    public void setEndDate(String endDate, String dateFormat)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setLenient(true);
        this.endDate = df.parse(endDate);
    }

    @ManyToOne
    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
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

    /** {@inheritDoc} */
    @Type(type = "af_time")
    @Formula(value = "(select SUM(t.effortEstimate)"
            + "from Task t, BacklogItem bi, Backlog b "
            + "where t.backlogItem_id = bi.id " + "and bi.backlog_id = b.id "
            + "and (" + "bi.backlog_id = id " + "or b.deliverable_id = id))")
    public AFTime getEffortEstimate() {
        return effortEstimate;
    }

    protected void setEffortEstimate(AFTime effortEstimate) {
        this.effortEstimate = effortEstimate;
    }

    /** {@inheritDoc} */
    @Type(type = "af_time")
    @Formula(value = "(select SUM(e.effort) "
            + "from TaskEvent e, Task t, BacklogItem bi, Backlog b "
            + "where e.eventType = 'PerformedWork' " + "and e.task_id = t.id "
            + "and t.backlogItem_id = bi.id " + "and bi.backlog_id = b.id "
            + "and (" + "bi.backlog_id = id " + "or b.deliverable_id = id))")
    public AFTime getPerformedEffort() {
        return performedEffort;
    }

    protected void setPerformedEffort(AFTime performedEffort) {
        this.performedEffort = performedEffort;
    }

    @Column(nullable = false, columnDefinition = "integer not null default 0")
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
