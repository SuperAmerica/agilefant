package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.WidgetCollection;

public interface AgilefantWidgetBusiness extends GenericBusiness<AgilefantWidget> {

    /*
     * CRUD
     */
    /**
     * Creates and returns a new AgilefantWidget. 
     */
    public AgilefantWidget create(String type, Integer objectId, Integer collectionId);
    
    
    /**
     * Move the widget to a specified position in its collection.
     */
    public void move(AgilefantWidget widget, int position, int listNumber);
    
    /**
     * Arranges the given widgets to lists according to their listNumber property.
     */
    public List<List<AgilefantWidget>> generateWidgetGrid(WidgetCollection collection);
    
}
