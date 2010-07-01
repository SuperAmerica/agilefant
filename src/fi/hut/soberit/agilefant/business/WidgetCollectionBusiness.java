package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.WidgetCollection;

public interface WidgetCollectionBusiness extends
        GenericBusiness<WidgetCollection> {

    /**
     * Create a new portfolio.
     */
    public WidgetCollection createPortfolio();
    
    /**
     * Retrieve all <code>WidgetCollections</code> as alphabetical list.
     * @return
     */
    public List<WidgetCollection> getAllCollections();
    
    /**
     * Insert the widget to a given position and shift the others.
     */
    public void insertWidgetToHead(WidgetCollection collection, AgilefantWidget widget);

    /**
     * Inserts the widget to a given position
     */
    public void insertWidgetToPosition(WidgetCollection collection, AgilefantWidget widget, int position, int listNumber);
}
