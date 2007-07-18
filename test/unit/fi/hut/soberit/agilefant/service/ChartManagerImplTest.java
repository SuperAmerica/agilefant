package fi.hut.soberit.agilefant.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.Iteration;
import junit.framework.TestCase;

public class ChartManagerImplTest extends TestCase {
	private final Date now = new Date();
	private final Date hourAgo = new Date(now.getTime() - AFTime.HOUR_IN_MILLIS);
	private final Date twoHoursAgo = new Date(hourAgo.getTime() - AFTime.HOUR_IN_MILLIS);
	private final Date yesterday = new Date(now.getTime() - AFTime.DAY_IN_MILLIS);
	private final Date dayBeforeYesterday = new Date(yesterday.getTime() - AFTime.DAY_IN_MILLIS);
	
	final User user = new User();
	final Task task = new Task();
	Task task1 = Task(1);
	Task task2 = Task(2);
	Task task3 = Task(3);
	
	private Iteration i;
	
	private ChartManagerImpl cm = new ChartManagerImpl();
	
	public void setUp() {
		this.i = new Iteration();
	}
	
	public void testFilterEstimates_withEmptyArray() {
		assertEquals(0, cm.filterEstimates(new ArrayList<EstimateHistoryEvent>()).size());
	}
	
	public void testFilterEstimates_withSingleDay() {
		Collection<EstimateHistoryEvent> estimates = new ArrayList<EstimateHistoryEvent>() {{
			add(new EstimateHistoryEvent(user, task1, twoHoursAgo, new AFTime("2h")));
			add(new EstimateHistoryEvent(user, task1, hourAgo, new AFTime("1h")));
			add(new EstimateHistoryEvent(user, task1, now, new AFTime("3h")));
		}};
		
		TreeMap<Date, Integer> filteredEstimates = cm.filterEstimates(estimates);
		
		assertEquals(1, filteredEstimates.size());
		assertEquals(3, (int)filteredEstimates.get(now));
	}
	
	
	public void testFilterEstimates_withMultipleDays() {
		Collection<EstimateHistoryEvent> estimates = new ArrayList<EstimateHistoryEvent>() {{
			add(new EstimateHistoryEvent(user, task1, yesterday, new AFTime("2h")));
			add(new EstimateHistoryEvent(user, task1, hourAgo, new AFTime("1h")));
			add(new EstimateHistoryEvent(user, task1, now, new AFTime("3h")));
			
		}};
		
		TreeMap<Date, Integer> filteredEstimates = cm.filterEstimates(estimates);
		
		assertEquals(2, filteredEstimates.size());
		assertEquals(3, (int)filteredEstimates.get(now)); // Today, the latest count
		assertEquals(2, (int)filteredEstimates.get(yesterday)); // Yesterday we only had one entry
	}
	
	private int sumMap(Map<Date, Integer> map) {
		int result = 0;
		for(Date d: map.keySet()) {
			result += map.get(d);
		}
		return result;
	}

	
	public void testBacklogItemHadNoTasksOnGivenDate() {
		BacklogItem bi = new BacklogItem();
		Task t = new Task();
		t.setCreated(yesterday);
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(t);
		bi.setTasks(tasks);
	
		assertTrue(cm.backlogItemHadNoTasksOnGivenDate(bi, dayBeforeYesterday));
		assertFalse(cm.backlogItemHadNoTasksOnGivenDate(bi, yesterday));		
	
	}
	public void testAddEstimatesOfEmptyBacklogItems() {
		TreeMap<Date, Integer> map = new TreeMap<Date, Integer>();
		map.put(yesterday, new Integer(2));
		map.put(dayBeforeYesterday, new Integer(3));

		Iteration iter = new Iteration();
		
		int temp = sumMap(map);
		cm.addEstimatesOfEmptyBacklogItems(map, iter);
		assertEquals(temp, sumMap(map));
		
		// assertEquals()

		BacklogItem bi = new BacklogItem();
		Task t = new Task();
		t.setCreated(yesterday);
		
		ArrayList<Task> tasks = new ArrayList<Task>();
		tasks.add(t);
		bi.setTasks(tasks);
		bi.setAllocatedEffort(new AFTime("1h"));
		ArrayList<BacklogItem> backlogItems = new ArrayList<BacklogItem>();
		backlogItems.add(bi);
		iter.setBacklogItems(backlogItems);
		
		temp = sumMap(map);
		cm.addEstimatesOfEmptyBacklogItems(map, iter);
		assertNotSame(temp, sumMap(map));
	}
	


	public void testGetAverageDailyProgress_withEmptyCollcetion() {
		// TODO Should this return Double.NaN instead of 0.0? Would that break things?
		assertEquals(0.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>()));
	}
	
	public void testGetAverageDailyProgress_withSinglePerformedWork() {
		assertEquals(2.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, yesterday, null, new AFTime("2h"), now));
		}}));
	}

	
	public Task Task(int Id){
		Task task = new Task();
		task.setId(Id);
		return task;
	}

	
	public void testGetAverageDailyProgress_withNullEffort() {
		assertEquals(0.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, yesterday, null, null, now));
		}}));
	}
	
	public void testGetAverageDailyProgress_withSingleDay() {
		assertEquals(7.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, hourAgo, null, new AFTime("6h"), now));
			add(new PerformedWork(user, task, twoHoursAgo, null, new AFTime("1h"), now));
		}}));
	}
	
	public void testGetAverageDailyProgress_withMultipleDays() {
//		 sorting is done primary based on workingDate and if that is null then based on creationDate
		assertEquals(6.0, cm.getAverageDailyProgress(new ArrayList<PerformedWork>() {{
			add(new PerformedWork(user, task, now, null, new AFTime("5h"), hourAgo));
			add(new PerformedWork(user, task, now, null, new AFTime("2h"), yesterday));
			add(new PerformedWork(user, task, now, null, new AFTime("8h"), yesterday));
			add(new PerformedWork(user, task, now, null, new AFTime("3h"), dayBeforeYesterday)); 
		}}));
	}

}
