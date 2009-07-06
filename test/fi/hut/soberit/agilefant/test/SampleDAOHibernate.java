package fi.hut.soberit.agilefant.test;

import fi.hut.soberit.agilefant.db.hibernate.GenericDAOHibernate;

public class SampleDAOHibernate extends GenericDAOHibernate<SampleEntity>
        implements SampleDAO {

    public SampleDAOHibernate() {
        super(SampleEntity.class);
    }

}
