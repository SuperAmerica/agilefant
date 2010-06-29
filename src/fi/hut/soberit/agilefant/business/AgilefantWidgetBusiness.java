package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.WidgetCollection;

public interface AgilefantWidgetBusiness extends GenericBusiness<AgilefantWidget> {
    public List<List<AgilefantWidget>> generateWidgetGrid(WidgetCollection collection);
}
