/**
 * 
 */
package fi.hut.soberit.agilefant.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import fi.hut.soberit.agilefant.db.EstimateHistoryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.PerformedWorkDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.TaskEvent;
import fi.hut.soberit.agilefant.model.Task;

/**
 * @author mpmerila
 */
public class ChartManagerImpl implements ChartManager {
	private static final Log log = LogFactory.getLog(ChartManagerImpl.class);

	private IterationDAO iterationDAO;
	private PerformedWorkDAO performedWorkDAO;
	private EstimateHistoryDAO estimateHistoryDAO;


	protected byte[] getChartImageByteArray(JFreeChart chart) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(out, chart, 780, 600);
			return out.toByteArray();
		} catch (IOException e) {
			log.warn("Problem occurred creating chart", e);
			return null;
		}
	}

	protected boolean onSameDay(Date d1, Date d2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d1);
		int day1 = calendar.get(Calendar.DAY_OF_MONTH);
		int month1 = calendar.get(Calendar.MONTH);
		int year1 = calendar.get(Calendar.YEAR);
		calendar.setTime(d2);
		int day2 = calendar.get(Calendar.DAY_OF_MONTH);
		int month2 = calendar.get(Calendar.MONTH);
		int year2 = calendar.get(Calendar.YEAR);

		return day1 == day2 && month1 == month2 && year1 == year2; 
	}

	protected boolean backlogItemHadNoTasksOnGivenDate(BacklogItem b, Date d) {
		for(Task t: b.getTasks()) {       // If there is a task
			if(t.getCreated().before(d) ||        // that was created before the given time d or
					onSameDay(t.getCreated(), d))     //   on the same day
				return false;             // the backlog item had tasks on given date 
		}
		return true;                      // otherwise it didnt have.
	}

	protected void addEstimatesOfEmptyBacklogItems(Map<Date, Integer> map, Iteration iteration) {
		Collection<BacklogItem> backlogItems = iteration.getBacklogItems();
//		Map<Date, Integer> result = 
		for(Date date : map.keySet()) {
			for(BacklogItem b : backlogItems) {
				if(backlogItemHadNoTasksOnGivenDate(b, date)) {
					Integer i = map.get(date);
					if(b.getAllocatedEffort() != null) {
						i = i + transformAFTimeToInt(b.getAllocatedEffort());
						map.put(date, i);
					}
				}
			}
		}
	}

	protected TreeMap<Date, Integer> filterEstimates(Collection<EstimateHistoryEvent> estimates) {
		/*-------------------------------------------------------------*/
		// The code for dataset: effort estimates
		// we only want to keep the last estimate for the day

		Date previousTime = null;

		HashMap<Integer, EstimateHistoryEvent> map = new HashMap<Integer, EstimateHistoryEvent>();
		TreeMap<Date, Integer> map2 = new TreeMap<Date, Integer>();
		Calendar calendar2 = Calendar.getInstance();
//		Collection<BacklogItem> backlogItems = iteration.getBacklogItems();

		for(EstimateHistoryEvent estimateEvent : estimates) {

			AFTime estimate = estimateEvent.getNewEstimate();
			Date creationTime = estimateEvent.getCreated();

			if(estimate!=null){
				if (previousTime!=null && creationTime!=null) {
					calendar2.setTime(previousTime);
					int date1 = calendar2.get(Calendar.DAY_OF_MONTH);
					int month1 = calendar2.get(Calendar.MONTH);
					int year1 = calendar2.get(Calendar.YEAR);
					calendar2.setTime(creationTime);
					int date2 = calendar2.get(Calendar.DAY_OF_MONTH);
					int month2 = calendar2.get(Calendar.MONTH);
					int year2 = calendar2.get(Calendar.YEAR);

					if (date1 != date2 || month1 != month2 || year1 != year2) { // Day changed, time to pop the estimates
						int sum = getSumOfEstimates(map.values());
						/*							for(BacklogItem b : backlogItems) {
								if(backlogItemHadNoTasksOnGivenDate(b, previousTime))
									sum = sum + transformAFTimeToInt(b.getAllocatedEffort());
							}*/
						map2.put(previousTime, sum);
					}
				}
				previousTime = creationTime;
				int id1 = estimateEvent.getTask().getId();
				map.put(id1, estimateEvent);

			}
		}

		/* for the last day*/
		int sum = getSumOfEstimates(map.values());
		if(previousTime!=null){
			map2.put(previousTime, sum);
		}		

		return map2;

	}

	/**
	 * @param workCollection Collection of PerformedWork
	 * @return Average daily work done, in hours
	 */
	protected double getAverageDailyProgress(Collection<PerformedWork> workCollection) {
		/*-------------------------------------------------------------*/
		// The code for dataset: actual workhours

		//TimeSeries workSeries = new TimeSeries("Workhours", Day.class);

		int lastDay=0;
		int lastMonth=0;
		int lastYear=0;
		int workSum=0;
		int count=0;
		int worktime=0;

		Date firstDate = null;
		Date lastDate = null;
		Calendar calendar = Calendar.getInstance();

		for(PerformedWork performedWork : workCollection){

			AFTime effort = performedWork.getEffort();
			if(effort!=null){
				long time = effort.getTime();

				long hours = time / AFTime.HOUR_IN_MILLIS;
				time %= AFTime.HOUR_IN_MILLIS; // we remove the full hours from the sum

				long minutes = time / AFTime.MINUTE_IN_MILLIS;
				time %= AFTime.MINUTE_IN_MILLIS; // we remove the full minutes from the sum 

				worktime = Math.round(hours + (minutes/60)); // we account the total sum in hours.
				Date date = performedWork.getWorkDate();
				if(date == null){
					date = performedWork.getCreated();
				}

				// Check if this date is so far the first date
				if(firstDate == null){
					firstDate = date;
				}else if(date.before(firstDate)){
					firstDate = date;
				}

				// Check if this is so far the last date
				if(lastDate == null){
					lastDate = date;
				}else if(date.after(lastDate)){
					lastDate = date;
				}


				calendar.setTime(date);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH) + 1; // January == 0
				int year = calendar.get(Calendar.YEAR);
				count++;

				// day changed, time to pop the previous days hours
				if ((day!=lastDay || month != lastMonth || year!=lastYear) && (count > 1)){
					//workSeries.add(new Day(day_last, month_last, year_last), worksum);
					//worksum=0; Can be used if the effort is wanted per day
				}

				workSum=workSum+worktime;
				lastDay=day;
				lastMonth=month;
				lastYear=year;
			}		

		}

		double diff = 0;
		double totalWorkDone = 0;

		if((workSum > 0) && (lastDate!=null)){ // pop the last days hours
			//workSeries.add(new Day(day_last, month_last, year_last), worksum);
			totalWorkDone=workSum;
			diff = lastDate.getTime() - firstDate.getTime();
		}

		double diffInDays = diff / AFTime.DAY_IN_MILLIS;
		double usedCalendarDays = Math.ceil(diffInDays) + 1; // If all the work is done in one day the value is 1
		double averageProgress = totalWorkDone / usedCalendarDays;

		double numbers = averageProgress;

		return numbers;
	}


	protected TimeSeriesCollection getDataset(double averageDailyProgress, TreeMap<Date, Integer> values2, Date startDate, Date endDate) {

		TimeSeries estimateSeries = new TimeSeries("Actual velocity", Day.class);
		TimeSeries referenceSeries = new TimeSeries("Estimated velocity", Day.class);

		Collection<Date> dates = values2.keySet();

		for(Date date : dates){
			double value = values2.get(date);
			estimateSeries.add(new Day(date), value);
		}

		// Set start date for referernce series
		Date firstEntry = values2.firstKey();
		if(startDate.before(firstEntry)){
			referenceSeries.add(new Day(startDate), values2.get(firstEntry)); // Sets the first date and value
		}else{
			referenceSeries.add(new Day(values2.firstKey()), values2.get(firstEntry)); // Sets the first date and value
		}

		// Set end date for reference series

		referenceSeries.add(new Day(endDate), 0); // Sets the end date and value

		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(estimateSeries);
		dataset.addSeries(referenceSeries);

		Date lastEntry = values2.lastKey();
		double lastValue = values2.get(lastEntry);

		TimeSeries trendSeries = createTrendSeries(averageDailyProgress, lastEntry, lastValue);
		if (trendSeries != null) {
			dataset.addSeries(trendSeries);
		}

		return dataset;
	} 


	private TimeSeries createTrendSeries(double averageDailyProgress, Date date, double lastEstimate) {
		// Variables that are used to account date setting to the trend line
		Calendar calendar = Calendar.getInstance();

		TimeSeries trendSeries = new TimeSeries("Reference velocity", Day.class);
		calendar.setTime(date);
		if (lastEstimate > 0) { // To avoid a gap in the graph
			trendSeries.add(new Day(date), lastEstimate);
		}
		if (averageDailyProgress > 0) {
			while (lastEstimate>0) {
				lastEstimate = lastEstimate - averageDailyProgress;
				if (lastEstimate<0) {
					lastEstimate=0;
				}	
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				trendSeries.add(new Day(calendar.getTime()), lastEstimate);
			}
			return trendSeries;
		} else {
			return null;
		}
	}	


	protected JFreeChart getChart(TimeSeriesCollection dataset, Date startDate, Date endDate) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Project burndown",
				"Date",
				"Estimated effort",
				dataset,
				true,
				true,
				false);
		XYPlot plot = chart.getXYPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();

		axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy")); // Here we set how the time axis should look like

		/* we want to set the start date to be official start day*/
		Date iterStartDate = startDate;
		if (iterStartDate != null) {
			Date min = axis.getMinimumDate();
			if (min.after(iterStartDate)) {
				axis.setMinimumDate(iterStartDate); // If there is no work done before the start of the iteration
			}
		}

		/* We want to set the end date to be official end day */
		Date iterEndDate = endDate;
		if (iterEndDate != null) {
			Date max = axis.getMaximumDate();
			if (max.before(iterEndDate)) {
				axis.setMaximumDate(iterEndDate); // If there is no work done after the end of the iteration
			}
		}

		//axis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 7)); // A way to set how often dates are showing in the time axis

		XYItemRenderer rend = plot.getRenderer();
		XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer) rend;
		rr.setShapesVisible(true);
		return chart;
	}

	public byte[] getIterationBurndown(int iterationId) {
		Iteration iteration = iterationDAO.get(iterationId);
		Date startDate = iteration.getStartDate(); // We set the start date for burndown graph
		Date endDate = iteration.getEndDate();// We set the end date for burndown graph

		/*-- Remove the comments of the line below if you want to see the actual workhours in the system --*/
		//dataset.addSeries(workSeries); 

		Collection<EstimateHistoryEvent> estimates = estimateHistoryDAO.getEstimateHistory(iterationDAO.get(iterationId));
		TreeMap<Date, Integer> values2 = filterEstimates(estimates);
		addEstimatesOfEmptyBacklogItems(values2, iteration);
		double averageDailyProgress = getAverageDailyProgress(performedWorkDAO.getPerformedWork(iterationDAO.get(iterationId)));
		TimeSeriesCollection dataset = getDataset(averageDailyProgress, values2, startDate, endDate);
		JFreeChart chart1 = getChart(dataset, startDate, endDate);

		return getChartImageByteArray(chart1);
	}

	protected int transformAFTimeToInt(AFTime aftime) {
		long time = aftime.getTime();

		long hours = time / AFTime.HOUR_IN_MILLIS;
		time %= AFTime.HOUR_IN_MILLIS;

		long minutes = time / AFTime.MINUTE_IN_MILLIS;
		//time %= AFTime.MINUTE_IN_MILLIS;
		return (int)Math.ceil(hours + (minutes/60));
	}

	protected int getSumOfEstimates(Collection<EstimateHistoryEvent> estimates){
		int sum = 0;
		for(EstimateHistoryEvent event : estimates){
			AFTime estimate = event.getNewEstimate();

			if(estimate!=null){
				sum = sum + transformAFTimeToInt(estimate);				
			}	
		}		
		return sum;
	}

	public void setEstimateHistoryDAO(EstimateHistoryDAO estimateHistoryDAO) {
		this.estimateHistoryDAO = estimateHistoryDAO;
	}

	public void setIterationDAO(IterationDAO iterationDAO) {
		this.iterationDAO = iterationDAO;
	}

	public void setPerformedWorkDAO(PerformedWorkDAO performedWorkDAO) {
		this.performedWorkDAO = performedWorkDAO;
	}
}
