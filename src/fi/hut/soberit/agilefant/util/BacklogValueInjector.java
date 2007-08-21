package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import java.sql.Date;

/**
 * Utility class for injecting backlog item metrics into each backlog item.
 */
public class BacklogValueInjector {
	/**
	 * Iject metrics into the backlog items of the given backlog
	 * @param backlog The backlog to iject the values to
	 * @param startDate The start date to calulate the metrics with
	 * @param taskEventDAO The taskEventDAO to use
	 * @param backlogItemDAO The backlogItemDAO to use
	 */
	public static void injectMetrics(Backlog backlog, Date startDate,
			TaskEventDAO taskEventDAO, BacklogItemDAO backlogItemDAO) {
		for(BacklogItem i: backlog.getBacklogItems()) {
			i.setBliOrigEst(taskEventDAO.getBLIOriginalEstimate(i, startDate));
			i.setTaskSumOrigEst(taskEventDAO.getTaskSumOrigEst(i, startDate));
			i.setTaskSumEffEst(backlogItemDAO.getTaskSumEffortLeft(i));
			if (i.getTaskSumEffEst() != null) {
				/* Use the effort left on placeholder if available */
				i.setBliEffEst(
						new AFTime(backlogItemDAO.getBLIEffortLeft(i).getTime() -
						i.getTaskSumEffEst().getTime()));
			} else {
				i.setBliEffEst(backlogItemDAO.getBLIEffortLeft(i));
			}
		}
	}
}
