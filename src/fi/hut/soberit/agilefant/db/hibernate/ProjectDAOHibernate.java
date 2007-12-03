package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Project;

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
    public Collection<Project> getOngoingProjects() {
        Date current = Calendar.getInstance().getTime();
        return super
                .getHibernateTemplate()
                .find(
                        "from Project d where d.startDate <= ? and d.endDate >= ? order by d.product.name ASC, d.endDate",
                        new Object[] { current, current });
    }

    /** {@inheritDoc} */
    public Collection<Project> getAllRankedProjects() {
        return super.getHibernateTemplate().find(
                "from Project d where d.rank != 0 order by d.rank ASC");
    }

    /** {@inheritDoc} */
    public Collection<Project> getOngoingRankedProjects() {
        Date current = Calendar.getInstance().getTime();
        return super
                .getHibernateTemplate()
                .find(
                        "from Project d where d.startDate <= ? and d.endDate >= ? and d.rank != 0 order by d.rank ASC",
                        new Object[] { current, current });
    }

    /** {@inheritDoc} */
    public Collection<Project> getOngoingUnrankedProjects() {
        Date current = Calendar.getInstance().getTime();
        return super
                .getHibernateTemplate()
                .find(
                        "from Project d where d.startDate <= ? and d.endDate >= ? and d.rank = 0 order by d.product.name ASC, d.endDate",
                        new Object[] { current, current });
    }

    public Project findFirstLowerRankedOngoingProject(
            Project project) {
        Date current = Calendar.getInstance().getTime();
        List projects = getHibernateTemplate()
                .find(
                        "from Project d where (d.rank < ?) and (d.rank != 0) and (d.startDate <= ? and d.endDate >= ?) order by d.rank desc limit 1",
                        new Object[] { project.getRank(), current, current });
        if (projects.size() == 0) {
            return null;
        } else {
            return (Project) projects.get(0);
        }
    }

    public Project findFirstUpperRankedOngoingProject(
            Project project) {
        Date current = Calendar.getInstance().getTime();
        List projects = getHibernateTemplate()
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
        List projects = null;
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

    public List<Integer> findBiggestRank() {
        List result = null;
        return result = super.getHibernateTemplate().find(
                "select max(d.rank) from Project d");
    }

}
