package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;

/**
 * Hibernate implementation of BacklogItemDAO interface using GenericDAOHibernate.
 */
public class BacklogItemDAOHibernate extends GenericDAOHibernate<BacklogItem> implements BacklogItemDAO {

	public BacklogItemDAOHibernate(){
		super(BacklogItem.class);
	}
	
	@SuppressWarnings(value = "unchecked")
	public List<Task> getRealTasks(BacklogItem backlogItem) {
		HibernateTemplate ht = super.getHibernateTemplate();
		String[] queryParams = {"backlogItem"};
		Object[] queryValues = {backlogItem};
		if(backlogItem.getPlaceHolder() != null) {
			String query = 
				"from Task t " +
				"where t.backlogItem = :backlogItem and " +
				"t != t.backlogItem.placeHolder";
			return (List<Task>) ht.findByNamedParam(
					query, queryParams, queryValues);
		} else {
			return new ArrayList<Task>(backlogItem.getTasks());
		}
	}
	
	@SuppressWarnings(value = "unchecked")
	public AFTime getBLIEffortLeft(BacklogItem backlogItem) {
		List<Long> results;
		HibernateTemplate ht = super.getHibernateTemplate();
		String[] queryParams = {"backlogItem"};
		Object[] queryValues = {backlogItem};
		String query =
			"select sum(t.effortEstimate) " +
			"from Task t " +
			"where t.backlogItem = :backlogItem";
		results = (List<Long>) 
				ht.findByNamedParam(query, queryParams, queryValues);
		if(results.size() > 0 && results.get(0) != null) {
			return new AFTime((Long) results.get(0));
		} else {
			return null;
		}
	}
	
	@SuppressWarnings(value = "unchecked")
	public AFTime getTaskSumEffortLeft(BacklogItem backlogItem) {
		List<Long> results;
		HibernateTemplate ht = super.getHibernateTemplate();
		String[] queryParams = {"backlogItem"};
		Object[] queryValues = {backlogItem};
		String query =
			"select sum(t.effortEstimate) " +
			"from Task t " +
			"where t.backlogItem = :backlogItem and " +
			"t != t.backlogItem.placeHolder";
		results = (List<Long>) 
				ht.findByNamedParam(query, queryParams, queryValues);
		if(results.size() > 0 && results.get(0) != null) {
			return new AFTime((Long) results.get(0));
		} else {
			return null;
		}
	}
}
