package fi.hut.soberit.agilefant.business.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EffortHistory;
import fi.hut.soberit.agilefant.util.BacklogValueInjector;
import fi.hut.soberit.agilefant.util.EffortHistoryUpdater;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogBusinessImpl implements BacklogBusiness {
	private BacklogItemDAO backlogItemDAO;

	private EffortHistoryDAO effortHistoryDAO;

	private TaskEventDAO taskEventDAO;

	private BacklogDAO backlogDAO;

	// @Override
	public void deleteMultipleItems(int backlogId, int[] backlogItemIds) {
		Backlog backlog = backlogDAO.get(backlogId);
		
		for (int id : backlogItemIds) {		
			Collection<BacklogItem> items = backlog.getBacklogItems();
			Iterator<BacklogItem> iterator = items.iterator();
			while( iterator.hasNext() ) {
				BacklogItem item = iterator.next();
				if( item.getId() == id ) {
					iterator.remove();
					backlogItemDAO.remove(id); 	//This isn't needed once Cascades are correct for Backlog -> BacklogItem.
												//Once that's done the test should also work proper.
				}
			}
		}
		
		updateEffortHistory(backlogId);
	}

	/**
	 * TODO: this implementation is not how a Business object ideally implements
	 * this sort of functionality. There should be no need for static methods,
	 * but rather the logic should simply be contained in non-static methods of
	 * <code>Business</code> objects. At this time the old implementation is
	 * not touched but this method is added here while EffortHistoryUpdater is
	 * deprecated.
	 */
	// @Override
	public void updateEffortHistory(int backlogId) {
		Backlog backlog = backlogDAO.get(backlogId);

		EffortHistoryUpdater.updateEffortHistory(effortHistoryDAO,
				taskEventDAO, backlogItemDAO, backlog);
	}

	public BacklogItemDAO getBacklogItemDAO() {
		return backlogItemDAO;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	public EffortHistoryDAO getEffortHistoryDAO() {
		return effortHistoryDAO;
	}

	public void setEffortHistoryDAO(EffortHistoryDAO effortHistoryDAO) {
		this.effortHistoryDAO = effortHistoryDAO;
	}

	public TaskEventDAO getTaskEventDAO() {
		return taskEventDAO;
	}

	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}

	public BacklogDAO getBacklogDAO() {
		return backlogDAO;
	}

	public void setBacklogDAO(BacklogDAO backlogDAO) {
		this.backlogDAO = backlogDAO;
	}
}
