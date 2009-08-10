package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.Interval;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.UnassignedLoadTO;

@Repository("taskDAO")
public class TaskDAOHibernate extends GenericDAOHibernate<Task> implements
        TaskDAO {

    public TaskDAOHibernate() {
        super(Task.class);
    }

    private void addIterationIntervalLimit(Criteria crit, Interval interval) {
        //search only from iterations
        crit.add(Restrictions.eq("class", "Iteration"));
        Date startDate = interval.getStart().toDate();
        Date endDate = interval.getEnd().toDate();
        //iteration may start during the interval
        Criterion startDateLimit = Restrictions.between("startDate", startDate,
                endDate);
        //iteration end during the interval
        Criterion endDateLimit = Restrictions.between("endDate", startDate,
                endDate);
        //interval may be within the iteration
        Criterion overlaps = Restrictions.or(startDateLimit, endDateLimit);
        Criterion withinIteration = Restrictions.and(Restrictions.le(
                "startDate", startDate), Restrictions.ge("endDate", endDate));
        crit.add(Restrictions.or(overlaps, withinIteration));
    }
    public List<Task> getIterationTasksWithEffortLeft(User user,
            Interval interval) {
        Criteria crit = getCurrentSession().createCriteria(Task.class);
        crit.createCriteria("responsibles")
                .add(Restrictions.idEq(user.getId()));
        Criteria iteration = crit.createCriteria("iteration");
        iteration.setFetchMode("parent", FetchMode.SELECT);
        this.addIterationIntervalLimit(iteration, interval);
        crit.add(Restrictions.isNull("story"));
        crit.add(Restrictions.gt("effortLeft", ExactEstimate.ZERO));
        crit.setFetchMode("creator", FetchMode.SELECT);
        return asList(crit);
    }

    public List<Task> getStoryTasksWithEffortLeft(User user, Interval interval) {
        Criteria crit = getCurrentSession().createCriteria(Task.class);
        crit.createCriteria("responsibles")
                .add(Restrictions.idEq(user.getId()));
       
        Criteria iteration = crit.createCriteria("story").createCriteria("backlog");
        iteration.setFetchMode("parent",FetchMode.SELECT);
        this.addIterationIntervalLimit(iteration, interval);
        crit.add(Restrictions.gt("effortLeft", ExactEstimate.ZERO));
        crit.setFetchMode("creator", FetchMode.SELECT);
        return asList(crit);
    }

    public Map<Integer, Integer> getNumOfResponsiblesByTask(Set<Integer> taskIds) {
        if(taskIds == null || taskIds.size() == 0) {
            return Collections.emptyMap();
        }
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

    public List<Task> getStoryAssignedTasksWithEffortLeft(User user,
            Interval interval) {
        Criteria crit = getCurrentSession().createCriteria(Task.class);
        crit.add(Restrictions.isEmpty("responsibles"));
        Criteria story = crit.createCriteria("story");
        story.createCriteria("responsibles").add(Restrictions.idEq(user.getId()));
        this.addIterationIntervalLimit(story.createCriteria("backlog"), interval);
        crit.setFetchMode("creator", FetchMode.SELECT);
        return asList(crit);
    }
    
    public List<UnassignedLoadTO> getUnassignedStoryTasksWithEffortLeft(User user,
            Interval interval) {
        
        Criteria iteration = getCurrentSession().createCriteria(Iteration.class,"iter");
        iteration.createCriteria("assignments","assigments").createCriteria("user").add(Restrictions.idEq(user.getId()));     
        
        Criteria stories = iteration.createCriteria("stories");
        stories.add(Restrictions.isEmpty("responsibles"));
                
        this.addIterationIntervalLimit(iteration, interval);
        
        Criteria tasks = stories.createCriteria("tasks","task");
        tasks.add(Restrictions.isEmpty("responsibles"));
        
        ProjectionList effortLeftSum = Projections.projectionList();
        effortLeftSum.add(Projections.sum("task.effortLeft"));
        effortLeftSum.add(Projections.groupProperty("iter.id"));
        effortLeftSum.add(Projections.property("assigments.availability"));
        
        iteration.setProjection(effortLeftSum);
        
        List<Object[]> data = asList(iteration);
        List<UnassignedLoadTO> result = new ArrayList<UnassignedLoadTO>();
        for(Object[] rowData : data) {
            UnassignedLoadTO row = new UnassignedLoadTO((ExactEstimate)rowData[0],(Integer)rowData[1], (Integer)rowData[2]);
            result.add(row);
        }
        
        return result;
    }

    public List<UnassignedLoadTO> getUnassignedIterationTasksWithEffortLeft(User user,
            Interval interval) {
        Criteria iteration = getCurrentSession().createCriteria(Iteration.class,"iter");
        iteration.createCriteria("assignments","assigments").createCriteria("user").add(Restrictions.idEq(user.getId()));     
                
        this.addIterationIntervalLimit(iteration, interval);
        
        Criteria tasks = iteration.createCriteria("tasks","task");
        tasks.add(Restrictions.isEmpty("responsibles"));
        tasks.add(Restrictions.isNull("story"));
        
        ProjectionList effortLeftSum = Projections.projectionList();
        effortLeftSum.add(Projections.sum("task.effortLeft"));
        effortLeftSum.add(Projections.groupProperty("iter.id"));
        effortLeftSum.add(Projections.property("assigments.availability"));
        
        iteration.setProjection(effortLeftSum);
        
        List<Object[]> data = asList(iteration);
        List<UnassignedLoadTO> result = new ArrayList<UnassignedLoadTO>();
        for(Object[] rowData : data) {
            UnassignedLoadTO row = new UnassignedLoadTO((ExactEstimate)rowData[0],(Integer)rowData[1], (Integer)rowData[2]);
            result.add(row);
        }
        
        return result;
    }
}
