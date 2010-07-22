package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.db.WidgetCollectionDAO;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WidgetCollection;

@Service("widgetCollectionBusiness")
@Transactional
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
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public List<WidgetCollection> getAllPublicCollections() {
        List<WidgetCollection> allCollections = new ArrayList<WidgetCollection>();
        allCollections.addAll(widgetCollectionDAO.getCollectionsForUser(null));
        return allCollections;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public List<WidgetCollection> getCollectionsForUser(User user) {
        List<WidgetCollection> allCollections = new ArrayList<WidgetCollection>();
        allCollections.addAll(widgetCollectionDAO.getCollectionsForUser(user));
        return allCollections;
    }
    
    /** {@inheritDoc} */
    @Transactional
    public WidgetCollection createPortfolio() {
        WidgetCollection collection = new WidgetCollection();
        collection.setName("New portfolio");
        
        Integer newId = (Integer)widgetCollectionDAO.create(collection);
        collection = widgetCollectionDAO.get(newId);
        
        collection.setName("New portfolio");
        
        return collection;
    }
    
    
    /** {@inheritDoc} */
    @Transactional
    public void insertWidgetToHead(WidgetCollection collection,
            AgilefantWidget widget) {
        this.insertWidgetToPosition(collection, widget, 0, 0);
    }

    /** {@inheritDoc} */
    @Transactional
    public void insertWidgetToPosition(WidgetCollection collection,
            AgilefantWidget widget, int position, int listNumber) {
        for (AgilefantWidget w : collection.getWidgets()) {
            if (w.getListNumber() == listNumber && w.getPosition() >= position) {
                w.setPosition(w.getPosition() + 1);
            }
        }
        widget.setPosition(position);
        widget.setListNumber(listNumber);
    }
}
