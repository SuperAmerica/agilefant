package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import fi.hut.soberit.agilefant.web.PageItem;

@Entity
public class Product extends Backlog implements PageItem {
	
    private Collection<Deliverable> deliverables = new HashSet<Deliverable>();

    @OneToMany(mappedBy="product")
    public Collection<Deliverable> getDeliverables() {
        return deliverables;
    }
    public void setDeliverables(Collection<Deliverable> deliverables) {
        this.deliverables = deliverables;
    }
    @Transient
	public Collection<PageItem> getChildren() {
		Collection<PageItem> c = new HashSet<PageItem>(this.deliverables.size());
		c.addAll(this.deliverables);
		return c;
	}
    @Transient
	public PageItem getParent() {
		// TODO Auto-generated method stub
		return null;
	}
    @Transient
	public boolean hasChildren() {
		return this.deliverables.size() > 0 ? true : false;
	}
}
