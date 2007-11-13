package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.OrderBy;

import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * Hibernate entity bean representing a product.
 * <p>
 * Conceptually, a product is a type of a backlog. A deliverable-backlog
 * represents work (deliverables, iterations, backlog items, tasks) done / to be
 * done for the product.
 * <p>
 * A product contains deliverables, which are some partial outcomes of the
 * product. For example, different versions of the product or some
 * documentation.
 * <p>
 * Product is at the top level of the hiearchy and thus is the biggest container
 * of work. Since a deliverable is a backlog, it can contain backlog items,
 * which, in turn, are smaller containers for work.
 * <p>
 * An example product would be "Acme WordProcessor" or "Agilefant 07".
 * 
 * @see fi.hut.soberit.agilefant.model.Deliverable
 */
@Entity
public class Product extends Backlog implements PageItem {

    private List<Deliverable> deliverables = new ArrayList<Deliverable>();

    /** Get the collection of deliverables belonging to this product. */
    @OneToMany(mappedBy = "product")
    @OrderBy(clause = "startDate asc, endDate asc")
    public List<Deliverable> getDeliverables() {
        return deliverables;
    }

    /** Set the collection of deliverables belonging to this product. */
    public void setDeliverables(List<Deliverable> deliverables) {
        this.deliverables = deliverables;
    }

    /** {@inheritDoc} */
    @Transient
    public List<PageItem> getChildren() {
        List<PageItem> c = new ArrayList<PageItem>(this.deliverables.size());
        c.addAll(this.deliverables);
        return c;
    }

    /** {@inheritDoc} */
    @Transient
    public PageItem getParent() {

        // We don't really want to show portfolio as root
        // return new PortfolioPageItem();
        return null;
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        return this.deliverables.size() > 0 ? true : false;
    }
}
