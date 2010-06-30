package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.AgilefantWidgetBusiness;
import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.db.AgilefantWidgetDAO;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.WidgetCollection;

@Service("agilefantWidgetBusiness")
@Transactional
public class AgilefantWidgetBusinessImpl extends
        GenericBusinessImpl<AgilefantWidget> implements AgilefantWidgetBusiness {

    @Autowired
    private WidgetCollectionBusiness widgetCollectionBusiness;
    
    private AgilefantWidgetDAO agilefantWidgetDAO;
    
    @Autowired
    public void setAgilefantWidgetDAO(AgilefantWidgetDAO agilefantWidgetDAO) {
        this.genericDAO = agilefantWidgetDAO;
        this.agilefantWidgetDAO = agilefantWidgetDAO;
    }

    public AgilefantWidgetBusinessImpl() {
        super(AgilefantWidget.class);
    }
    
    /** {@inheritDoc} */
    @Transactional
    public AgilefantWidget create(String type, Integer objectId, Integer collectionId) {
        AgilefantWidget storable = new AgilefantWidget();
        
        if (type == null || objectId == null || collectionId == null) {
            throw new IllegalArgumentException("Arguments must be supplied");
        }
        
        WidgetCollection collection = widgetCollectionBusiness.retrieve(collectionId);
        
        storable.setType(type);
        storable.setObjectId(objectId);
        storable.setWidgetCollection(collection);
        
        widgetCollectionBusiness.insertWidgetToHead(collection, storable);
        
        Integer newId = (Integer)agilefantWidgetDAO.create(storable);
        
        return  agilefantWidgetDAO.get(newId);
    }
    
    
    /** {@inheritDoc} */
    @Transactional
    public void move(AgilefantWidget widget, int position, int listNumber) {
        widgetCollectionBusiness.insertWidgetToPosition(widget
                .getWidgetCollection(), widget, position, listNumber);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<List<AgilefantWidget>> generateWidgetGrid(
            WidgetCollection collection, int minNumberOfLists) {
        Collection<AgilefantWidget> widgets = collection.getWidgets();
        List<List<AgilefantWidget>> columns = new ArrayList<List<AgilefantWidget>>();
        int numberOfLists = minNumberOfLists - 1;

        for (AgilefantWidget widget : widgets) {
            if (numberOfLists < widget.getListNumber()) {
                numberOfLists = widget.getListNumber();
            }
        }

        for (int i = 0; i <= numberOfLists; i++) {
            columns.add(new ArrayList<AgilefantWidget>());
        }

        for (AgilefantWidget widget : widgets) {
            columns.get(widget.getListNumber()).add(widget);
        }
        for (List<AgilefantWidget> column : columns) {
            Collections.sort(column, new PropertyComparator("position", true,
                    true));
        }
        return columns;
    }

}
