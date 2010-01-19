package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.LabelDAO;
import fi.hut.soberit.agilefant.model.Label;

@Repository("labelDAO")
public class LabelDAOHibernate extends GenericDAOHibernate<Label> implements
LabelDAO {

    public LabelDAOHibernate() {
        super(Label.class);
    }
}
    
