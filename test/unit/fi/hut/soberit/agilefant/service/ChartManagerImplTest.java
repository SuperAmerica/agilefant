package fi.hut.soberit.agilefant.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import junit.framework.TestCase;

public class ChartManagerImplTest extends TestCase {
	private ChartManagerImpl cm = new ChartManagerImpl();
	
	public void testFilterEstimates() {
		final User user = new User();
		final Task task = new Task();
		
		final Date now = new Date();
		final Date hourAgo = new Date(now.getTime() - AFTime.HOUR_IN_MILLIS);
		final Date twoHoursAgo = new Date(now.getTime() - AFTime.HOUR_IN_MILLIS);
		final Date yesterday = new Date(now.getTime() - AFTime.DAY_IN_MILLIS);
		
		final AFTime hour = new AFTime("1h");
		
		Collection<EstimateHistoryEvent> estimates = new ArrayList<EstimateHistoryEvent>() {{
			new EstimateHistoryEvent(user, task, now, new AFTime("1h"));
			new EstimateHistoryEvent(user, task, hourAgo, new AFTime("3h"));
			new EstimateHistoryEvent(user, task, twoHoursAgo, new AFTime("2h"));
		}};
		
		Collection<EstimateHistoryEvent> filteredEstimates = cm.filterEstimates(estimates);
		
		assertEquals(1, filteredEstimates.size());
		assertEquals(new AFTime("1h"), filteredEstimates.iterator().next().getNewEstimate());
	}

	public void testGetAverageDailyProgress() {
		fail("Not yet implemented");
	}

	public void testGetDataset() {
		fail("Not yet implemented");
	}

}
