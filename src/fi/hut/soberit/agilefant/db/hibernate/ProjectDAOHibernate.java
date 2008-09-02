package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.util.ProjectMetrics;

/**
 * Hibernate implementation of ProjectDAO interface using
 * GenericDAOHibernate.
 */
public class ProjectDAOHibernate extends GenericDAOHibernate<Project>
        implements ProjectDAO {

    public ProjectDAOHibernate() {
        super(Project.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Project> getOngoingProjects() {
        Date current = Calendar.getInstance().getTime();
        return super
                .getHibernateTemplate()
                .find(
                        "from Project d where d.startDate <= ? and d.endDate >= ? order by d.product.name ASC, d.endDate",
                        new Object[] { current, current });
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Project> getAllRankedProjects() {
        return super.getHibernateTemplate().find(
                "from Project d where d.rank != 0 order by d.rank ASC");
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Project> getOngoingRankedProjects() {
        Date current = Calendar.getInstance().getTime();
        return super
                .getHibernateTemplate()
                .find(
                        "from Project d where d.startDate <= ? and d.endDate >= ? and d.rank != 0 order by d.rank ASC",
                        new Object[] { current, current });
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Project> getOngoingUnrankedProjects() {
        Date current = Calendar.getInstance().getTime();
        return super
                .getHibernateTemplate()
                .find(
                        "from Project d where d.startDate <= ? and d.endDate >= ? and d.rank = 0 order by d.product.name ASC, d.endDate",
                        new Object[] { current, current });
    }

    @SuppressWarnings("unchecked")
    public Project findFirstLowerRankedOngoingProject(
            Project project) {
        Date current = Calendar.getInstance().getTime();
        List<Project> projects = getHibernateTemplate()
                .find(
                        "from Project d where (d.rank < ?) and (d.rank != 0) and (d.startDate <= ? and d.endDate >= ?) order by d.rank desc limit 1",
                        new Object[] { project.getRank(), current, current });
        if (projects.size() == 0) {
            return null;
        } else {
            return (Project) projects.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public Project findFirstUpperRankedOngoingProject(
            Project project) {
        Date current = Calendar.getInstance().getTime();
        List<Project> projects = getHibernateTemplate()
                .find(
                        "from Project d where (d.rank > ?) and (d.rank != 0) and (d.startDate <= ? and d.endDate >= ?) order by d.rank asc limit 1",
                        new Object[] { project.getRank(), current, current });
        if (projects.size() == 0) {
            return null;
        } else {
            return (Project) projects.get(0);
        }
    }

    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank) {
        /*
         * if (lowLimitRank == null) projects =
         * super.getHibernateTemplate().find( "from Project d where d.rank <
         * ?", upperLimitRank); else if (upperLimitRank == null) projects =
         * super.getHibernateTemplate().find( "from Project d where d.rank >=
         * ?", lowLimitRank); else if (lowLimitRank != null && upperLimitRank !=
         * null) projects = super.getHibernateTemplate().find( "from
         * Project d where d.rank >= ? and d.rank < ?", new Object[] {
         * lowLimitRank, upperLimitRank }); else throw new
         * IllegalArgumentException("Both limits canot be null.");
         * 
         * Iterator it = projects.iterator(); while (it.hasNext()) {
         * Project d = (Project) it.next(); d.setRank(d.getRank() + 1);
         * store(d); }
         */

        if (lowLimitRank == null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update Project d set d.rank = (d.rank + 1) where d.rank < ?",
                            upperLimitRank);
        } else if (upperLimitRank == null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update Project d set d.rank = (d.rank + 1) where d.rank >= ?",
                            lowLimitRank);
        } else if (lowLimitRank != null && upperLimitRank != null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update Project d set d.rank = (d.rank + 1) where d.rank >= ? and d.rank < ?",
                            new Object[] { lowLimitRank, upperLimitRank });
        } else
            throw new IllegalArgumentException("Both limits cannot be null.");
    }
    
    @SuppressWarnings("unchecked")
    public List<Integer> findBiggestRank() {
        return super.getHibernateTemplate().find(
                "select max(d.rank) from Project d");
    }

    @SuppressWarnings("unchecked")
    public Integer getDoneBLIs(Project proj) {
        DetachedCriteria crit = DetachedCriteria.forClass(Iteration.class);
        ProjectionList sums = Projections.projectionList();
        sums.add(Projections.groupProperty("project"));
        sums.add(Projections.count("backlogItems"));
        @SuppressWarnings("unused")
        DetachedCriteria bli = crit.createAlias("backlogItems", "bli");
        crit.add(Restrictions.eq("project", proj));
        crit.add(Restrictions.eq("bli.state", State.DONE));
        crit.setProjection(sums);
        List res = super.getHibernateTemplate().findByCriteria(crit);
        try {
            Object[] data = (Object[]) res.get(0);
            return (Integer) data[1];
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public ProjectMetrics getProjectBLIMetrics(Project proj) {
        DetachedCriteria crit = DetachedCriteria.forClass(Iteration.class);
        ProjectionList sums = Projections.projectionList();
        sums.add(Projections.groupProperty("project"));
        sums.add(Projections.sum("bli.effortLeft"));
        sums.add(Projections.sum("bli.originalEstimate"));
        sums.add(Projections.count("bli.id"));
        crit.createAlias("backlogItems", "bli");
        crit.add(Restrictions.eq("project", proj));
        crit.setProjection(sums);
        List res = super.getHibernateTemplate().findByCriteria(crit);
        try {
            Object[] data = (Object[]) res.get(0);
            ProjectMetrics metr = new ProjectMetrics();
            metr.setOriginalEstimate((AFTime) data[2]);
            metr.setEffortLeft((AFTime) data[1]);
            //metr.setNumberOfAllIterations((Integer) data[4]);
            metr.setTotalItems((Integer) data[3]);
            return metr;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<BacklogThemeBinding> getProjectThemeData(Project proj) {
        DetachedCriteria crit = DetachedCriteria
                .forClass(BacklogThemeBinding.class);
        if (proj.getIterations().size() == 0) {
            return new ArrayList<BacklogThemeBinding>();
        }
        crit.add(Restrictions.in("backlog", proj.getIterations()));
        return super.getHibernateTemplate().findByCriteria(crit);
    }

}
