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
     * @param objectId TODO
     */
    public AgilefantWidget create(String type, Integer objectId, Integer collectionId, Integer position, Integer listNumber);
    
    
    /**
     * Arranges the given widgets to lists according to their listNumber property.
     */
    public List<List<AgilefantWidget>> generateWidgetGrid(WidgetCollection collection);
    
}
