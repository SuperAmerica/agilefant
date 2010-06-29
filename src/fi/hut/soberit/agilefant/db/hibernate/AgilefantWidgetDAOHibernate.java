package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.AgilefantWidgetDAO;
import fi.hut.soberit.agilefant.model.AgilefantWidget;

@Repository("agilefantWidgetDAO")
public class AgilefantWidgetDAOHibernate extends
        GenericDAOHibernate<AgilefantWidget> implements AgilefantWidgetDAO {
    
    public AgilefantWidgetDAOHibernate() {
        super(AgilefantWidget.class);
    }
    
}
