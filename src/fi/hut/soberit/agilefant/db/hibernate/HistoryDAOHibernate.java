package fi.hut.soberit.agilefant.db.hibernate;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import fi.hut.soberit.agilefant.db.HistoryDAO;
import fi.hut.soberit.agilefant.model.History;

public class HistoryDAOHibernate extends
        GenericDAOHibernate<History<?>> implements HistoryDAO {

    //private Log logger = LogFactory.getLog(getClass());

    public HistoryDAOHibernate() {
        super(History.class);
    }
}
