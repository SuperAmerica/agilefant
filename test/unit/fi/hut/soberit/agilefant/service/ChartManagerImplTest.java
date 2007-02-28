package fi.hut.soberit.agilefant.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import junit.framework.TestCase;

public class ChartManagerImplTest extends TestCase {
	private final Date now = new Date();
	private final Date hourAgo = new Date(now.getTime() - AFTime.HOUR_IN_MILLIS);
	private final Date twoHoursAgo = new Date(now.getTime() - 2*AFTime.HOUR_IN_MILLIS);
	private final Date yesterday = new Date(now.getTime() - AFTime.DAY_IN_MILLIS);
	
	private ChartManagerImpl cm = new ChartManagerImpl();
	
	public void testFilterEstimates_withEmptyArray() {
		assertEquals(0, cm.filterEstimates(new ArrayList<EstimateHistoryEvent>()).size());
	}
	
	public void testFilterEstimates_withSingleDay() {
		final User user = new User();
		final Task task = new Task();
		
		Collection<EstimateHistoryEvent> estimates = new ArrayList<EstimateHistoryEvent>() {{
			add(new EstimateHistoryEvent(user, task, now, new AFTime("1h")));
			add(new EstimateHistoryEvent(user, task, hourAgo, new AFTime("3h")));
			add(new EstimateHistoryEvent(user, task, twoHoursAgo, new AFTime("2h")));
		}};
		
		Collection<EstimateHistoryEvent> filteredEstimates = cm.filterEstimates(estimates);
		
		assertEquals(1, filteredEstimates.size());
		assertEquals(new AFTime("1h"), filteredEstimates.iterator().next().getNewEstimate());
	}

	public void testGetAverageDailyProgress() {
		Iteration iter = new Iteration();
	}

	public void testGetDataset() {
		fail("Not yet implemented");
	}
}
