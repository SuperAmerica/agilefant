package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;

/**
 * Hibernate implementation of BacklogItemDAO interface using
 * GenericDAOHibernate.
 */
public class BacklogItemDAOHibernate extends GenericDAOHibernate<BacklogItem>
        implements BacklogItemDAO {

    public BacklogItemDAOHibernate() {
        super(BacklogItem.class);
    }
    
    public List<BacklogItem> productNonDoneTopLevelBacklogItems(int productId) {
        // TODO Auto-generated method stub
        return new ArrayList<BacklogItem>();
    }

    public List<BacklogItem> backlogItemChildren(int fatherId) {
        // TODO Auto-generated method stub
        return new ArrayList<BacklogItem>();
    }
}
