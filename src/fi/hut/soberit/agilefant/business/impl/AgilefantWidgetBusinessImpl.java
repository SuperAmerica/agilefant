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
@Transactional(readOnly = true)
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
    public AgilefantWidget create(String type, Integer objectId,
            Integer collectionId, Integer position, Integer listNumber) {
        AgilefantWidget storable = new AgilefantWidget();
        
        if (type == null || objectId == null || collectionId == null || position == null || listNumber == null) {
            throw new IllegalArgumentException("Arguments must be supplied");
        }
        
        storable.setType(type);
        storable.setObjectId(objectId);
        storable.setListNumber(listNumber);
        storable.setPosition(position);
        storable.setWidgetCollection(widgetCollectionBusiness.retrieve(collectionId));
        
        // TODO: order the list
        
        
        Integer newId = (Integer)agilefantWidgetDAO.create(storable);
        
        return agilefantWidgetDAO.get(newId);
    }
    

    @SuppressWarnings("unchecked")
    public List<List<AgilefantWidget>> generateWidgetGrid(
            WidgetCollection collection) {
        Collection<AgilefantWidget> widgets = collection.getWidgets();
        List<List<AgilefantWidget>> columns = new ArrayList<List<AgilefantWidget>>();
        int numberOfLists = 0;
        
        if(widgets.size() == 0) {
            return columns;
        }

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
