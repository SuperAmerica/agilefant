package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;

/**
 * Hibernate implementation of IterationGoalDAO interface using
 * GenericDAOHibernate.
 */
public class IterationGoalDAOHibernate extends
        GenericDAOHibernate<IterationGoal> implements IterationGoalDAO {

    public IterationGoalDAOHibernate() {
        super(IterationGoal.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public IterationGoal findFirstHigherRankedIterationGoal(
            IterationGoal iterGoal) {

        List<IterationGoal> results = super
                .getHibernateTemplate()
                .find(
                        "from IterationGoal ig where (ig.iteration = ?) and (ig.priority < ?) order by priority desc limit 1",
                        new Object[] { iterGoal.getIteration(),
                                iterGoal.getPriority() });

        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public IterationGoal findFirstLowerRankedIterationGoal(
            IterationGoal iterGoal) {

        List<IterationGoal> results = super
                .getHibernateTemplate()
                .find(
                        "from IterationGoal ig where (ig.iteration = ?) and (ig.priority > ?) order by priority asc limit 1",
                        new Object[] { iterGoal.getIteration(),
                                iterGoal.getPriority() });

        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public IterationGoal getLowestRankedIterationGoalInIteration(
            Iteration iteration) {

        List<IterationGoal> results = getHibernateTemplate().find(
                "from IterationGoal ig where (ig.iteration = ?) order by priority desc limit 1",
                new Object[] { iteration });

        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    /** {@inheritDoc} */
    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank,
            Iteration iteration) {
        // List projects = null;

        if (lowLimitRank == null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update IterationGoal ig set ig.priority = (ig.priority + 1) where (ig.priority < ?) and (ig.iteration=?)",
                            new Object[] { lowLimitRank, iteration });
        } else if (upperLimitRank == null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update IterationGoal ig set ig.priority = (ig.priority + 1) where (ig.priorityk >= ?) and (ig.iteration=?)",
                            new Object[] { lowLimitRank, iteration });
        } else if (lowLimitRank != null && upperLimitRank != null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update IterationGoal ig set ig.priority = (ig.priority + 1) where (ig.priority >= ?) and (ig.priority < ?) and (ig.iteration=?)",
                            new Object[] { lowLimitRank, upperLimitRank,
                                    iteration });
        } else
            throw new IllegalArgumentException("Both limits cannot be null.");
    }

}
