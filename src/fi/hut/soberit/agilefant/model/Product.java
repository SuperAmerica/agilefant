package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Product extends Backlog {
	
    private Collection<Deliverable> deliverables = new HashSet<Deliverable>();

    @OneToMany(mappedBy="product")
    public Collection<Deliverable> getDeliverables() {
        return deliverables;
    }
    public void setDeliverables(Collection<Deliverable> deliverables) {
        this.deliverables = deliverables;
    }
}
