package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.db.WidgetCollectionDAO;
import fi.hut.soberit.agilefant.model.WidgetCollection;

@Service("widgetCollectionBusiness")
@Transactional(readOnly = true)
public class WidgetCollectionBusinessImpl extends
        GenericBusinessImpl<WidgetCollection> implements WidgetCollectionBusiness {

    private WidgetCollectionDAO widgetCollectionDAO;
    
    @Autowired
    public void setWidgetCollectionDAO(WidgetCollectionDAO widgetCollectionDAO) {
        this.genericDAO = widgetCollectionDAO;
        this.widgetCollectionDAO = widgetCollectionDAO;
    }


    public WidgetCollectionBusinessImpl() {
        super(WidgetCollection.class);
    }
    
}
