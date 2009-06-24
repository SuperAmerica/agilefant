package fi.hut.soberit.agilefant.db.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

@Repository("taskDAO")
public class TaskDAOHibernate extends GenericDAOHibernate<Task> implements
        TaskDAO {

    public TaskDAOHibernate() {
        super(Task.class);
    }

    public List<Task> getIterationTasksByUserAndTimeframe(User user,
            DateTime startDate, DateTime endDate) {
        Criteria crit = getCurrentSession().createCriteria(Task.class);
        crit.createCriteria("responsibles")
                .add(Restrictions.idEq(user.getId()));
        crit.createCriteria("iteration").add(
                Restrictions.and(Restrictions.le("startDate", startDate.toDate()),
                        Restrictions.ge("endDate", endDate.toDate())));
        crit.add(Restrictions.isNull("story"));
        crit.setFetchMode("creator", FetchMode.SELECT);
        return asList(crit);
    }

    public List<Task> getStoryTasksByUserAndTimeframe(User user, DateTime startDate, DateTime endDate) {
        Criteria crit = getCurrentSession().createCriteria(Task.class);
        crit.createCriteria("responsibles")
                .add(Restrictions.idEq(user.getId()));
        crit.createCriteria("story").createCriteria("backlog").add(
                Restrictions.and(Restrictions.le("startDate", startDate.toDate()),
                        Restrictions.ge("endDate", endDate.toDate())));
        crit.setFetchMode("creator", FetchMode.SELECT);
        return asList(crit);
    }

    public Map<Integer, Integer> getNumOfResponsiblesByTask(Set<Integer> taskIds) {
        Criteria crit = getCurrentSession().createCriteria(Task.class);
        crit.add(Restrictions.in("id", taskIds));
        crit.createAlias("responsibles", "responsible");
        ProjectionList sums = Projections.projectionList();
        sums.add(Projections.groupProperty("id"));
        sums.add(Projections.count("responsible.id"));
        
        crit.setProjection(sums);
        List<Object[]> rawData = asList(crit);
        
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for(Object[] row : rawData) {
            result.put((Integer)row[0], (Integer)row[1]);
        }
        return result;
    }

    public List<Task> getUnassignedTasksByStoryResponsibles(User user,
            DateTime startDate, DateTime endDate) {
        // TODO Auto-generated method stub
        return null;
    }
}
