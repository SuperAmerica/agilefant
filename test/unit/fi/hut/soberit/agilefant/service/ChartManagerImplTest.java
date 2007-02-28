package fi.hut.soberit.agilefant.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import junit.framework.TestCase;

public class ChartManagerImplTest extends TestCase {
	private final Date now = new Date();
	private final Date hourAgo = new Date(now.getTime() - AFTime.HOUR_IN_MILLIS);
	private final Date twoHoursAgo = new Date(hourAgo.getTime() - AFTime.HOUR_IN_MILLIS);
	private final Date yesterday = new Date(now.getTime() - AFTime.DAY_IN_MILLIS);
	private final Date dayBeforeYesterday = new Date(yesterday.getTime() - AFTime.DAY_IN_MILLIS);
	
	final User user = new User();
	final Task task = new Task();
	
	private ChartManagerImpl cm = new ChartManagerImpl();
	
	public void testFilterEstimates_withEmptyArray() {
		assertEquals(0, cm.filterEstimates(new ArrayList<EstimateHistoryEvent>()).size());
	}
	
	public void testFilterEstimates_withSingleDay() {
		Collection<EstimateHistoryEvent> estimates = new ArrayList<EstimateHistoryEvent>() {{
			add(new EstimateHistoryEvent(user, task, now, new AFTime("1h")));
			add(new EstimateHistoryEvent(user, task, hourAgo, new AFTime("3h")));
			add(new EstimateHistoryEvent(user, task, twoHoursAgo, new AFTime("2h")));
		}};
		
		Collection<EstimateHistoryEvent> filteredEstimates = cm.filterEstimates(estimates);
		
		assertEquals(1, filteredEstimates.size());
		assertEquals(new AFTime("1h"), filteredEstimates.iterator().next().getNewEstimate());
	}

	public void testGetAverageDailyProgress_withEmptyCollcetion() {
		// TODO Should this return Double.NaN instead of 0.0? Would that break things?
		assertEquals(0.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>()));
	}
	
	public void testGetAverageDailyProgress_withSinglePerformedWork() {
		assertEquals(2.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, yesterday, new AFTime("2h"), null, now));
		}}));
	}
	
	public void testGetAverageDailyProgress_withNullEffort() {
		assertEquals(0.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, yesterday, null, null, now));
		}}));
	}
	
	public void testGetAverageDailyProgress_withSingleDay() {
		assertEquals(3.5, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, hourAgo, new AFTime("6h"), null, now));
			add(new PerformedWork(user, task, twoHoursAgo, new AFTime("1h"), null, now));
		}}));
	}
	
	public void testGetAverageDailyProgress_withMultipleDays() {
		assertEquals(6.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, hourAgo, new AFTime("5h"), null, now));
			add(new PerformedWork(user, task, yesterday, new AFTime("2h"), null, now));
			add(new PerformedWork(user, task, yesterday, new AFTime("8h"), null, now));
			add(new PerformedWork(user, task, dayBeforeYesterday, new AFTime("3h"), null, now));
		}}));
	}
}
