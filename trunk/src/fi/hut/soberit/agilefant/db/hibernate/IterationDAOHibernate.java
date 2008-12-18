package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Iteration;

/**
 * Hibernate implementation of IterationDAO interface using GenericDAOHibernate.
 */
public class IterationDAOHibernate extends GenericDAOHibernate<Iteration>
        implements IterationDAO {

    public IterationDAOHibernate() {
        super(Iteration.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Iteration> getOngoingIterations() {
        Date current = Calendar.getInstance().getTime();
        return super
                .getHibernateTemplate()
                .find(
                        "from Iteration i where i.startDate <= ? and i.endDate >= ? order by i.project.name ASC, i.endDate",
                        new Object[] { current, current });
    }
}
