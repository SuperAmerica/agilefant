package fi.hut.soberit.agilefant.util;

import java.util.Calendar;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EffortHistory;

/**
 * This class is utility class for updating effort history.
 *
 */
public class EffortHistoryUpdater {
	
	/**
	 * Update the effort history of given backlog.
	 * 
	 * @param effortHistoryDAO the EffortHistoryDAO to use
	 * @param taskEventDAO the TaskEventDAO to use
	 * @param backlogItemDAO the BacklogItemDAO to use
	 * @param backlog the Backlog to update effort history for
	 */
	public static void updateEffortHistory(EffortHistoryDAO effortHistoryDAO,
			TaskEventDAO taskEventDAO, BacklogItemDAO backlogItemDAO, 
			Backlog backlog) {
		
		java.sql.Date today = 
			new java.sql.Date(Calendar.getInstance().getTimeInMillis());
		EffortHistory effortHistory = 
			effortHistoryDAO.getByDateAndBacklog(today, backlog);
		
		if (effortHistory == null) {
			effortHistory = new EffortHistory();
			effortHistory.setDate(today);
			effortHistory.setBacklog(backlog);
		}
		
		/* Set estimate values to backlog items */
		for(BacklogItem i: backlog.getBacklogItems()) {
			i.setBliOrigEst(taskEventDAO.getBLIOriginalEstimate(i, 
					backlog.getStartDate()));
			i.setTaskSumOrigEst(taskEventDAO.getTaskSumOrigEst(i, 
					backlog.getStartDate()));
			i.setBliEffEst(backlogItemDAO.getBLIEffortLeft(i));
			i.setTaskSumEffEst(backlogItemDAO.getTaskSumEffortLeft(i));
		}
		
		effortHistory.setOriginalEstimate(backlog.getBliOrigEstSum());
		effortHistory.setEffortLeft(backlog.getBliEffortLeftSum());
		
		effortHistoryDAO.store(effortHistory);
	}
}
