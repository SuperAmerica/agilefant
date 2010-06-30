package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.db.WidgetCollectionDAO;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
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
