package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.WidgetCollectionDAO;
import fi.hut.soberit.agilefant.model.WidgetCollection;

@Repository("widgetCollectionDAO")
public class WidgetCollectionDAOHibernate extends
        GenericDAOHibernate<WidgetCollection> implements WidgetCollectionDAO {
    
    public WidgetCollectionDAOHibernate() {
        super(WidgetCollection.class);
    }
    
}
