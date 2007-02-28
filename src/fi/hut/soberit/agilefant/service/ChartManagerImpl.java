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
import java.util.TreeMap;

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
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.PerformedWork;

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

	protected Collection<EstimateHistoryEvent> filterEstimates(Collection<EstimateHistoryEvent> estimates) {
		/*-------------------------------------------------------------*/
		// The code for dataset: effort estimates
		// we only want to keep the last estimate for the day
		
		Date previousTime = null;
		
		HashMap<Integer, EstimateHistoryEvent> map = new HashMap<Integer, EstimateHistoryEvent>();
		TreeMap<Long, EstimateHistoryEvent> map2 = new TreeMap<Long, EstimateHistoryEvent>();
		Calendar calendar2 = Calendar.getInstance();
		
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
						for (EstimateHistoryEvent estimateEvent2 : map.values()) {
							map2.put(estimateEvent2.getCreated().getTime(), estimateEvent2);
						}
					}
				}
				previousTime = creationTime;
				int id1 = estimateEvent.getTask().getId();
				map.put(id1, estimateEvent);

			}
		}
		
		/* for the last day*/
		Collection<EstimateHistoryEvent> values = map.values();
		for (EstimateHistoryEvent estimateEvent2 : values){
			Date estimate2 = estimateEvent2.getCreated();
			map2.put(estimate2.getTime(), estimateEvent2);
		}
		
		return map2.values();
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
		
		// The date for the first work effort
		int day_f=0;
		int month_f=0;
		int year_f=0;
		double totalWorkDone=0;
		
		for(PerformedWork performedWork : workCollection){
			
			AFTime effort = performedWork.getEffort();
			if(effort!=null){
				long time = effort.getTime();
			
				long hours = time / AFTime.HOUR_IN_MILLIS;
				time %= AFTime.HOUR_IN_MILLIS; // we remove the full hours from the sum
				
				long minutes = time / AFTime.MINUTE_IN_MILLIS;
				time %= AFTime.MINUTE_IN_MILLIS; // we remove the full minutes from the sum 
				
				worktime = Math.round(hours + (minutes/60)); // we account the total sum in hours.
				Date date = performedWork.getCreated();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH) + 1; // January == 0
				int year = calendar.get(Calendar.YEAR);
				count++;
				
//				 day changed, time to pop the previous days hours
				if ((day!=lastDay || month != lastMonth || year!=lastYear) && (count > 1)){
					//workSeries.add(new Day(day_last, month_last, year_last), worksum);
					//worksum=0; Can be used if the effort is wanted per day
				}else if(count==1){
					day_f=day;
					month_f=month;
					year_f=year;
				}
				
				workSum=workSum+worktime;
				lastDay=day;
				lastMonth=month;
				lastYear=year;
			}		
			
		}
		if((workSum > 0) && (lastDay > 0)){ // pop the last days hours
			//workSeries.add(new Day(day_last, month_last, year_last), worksum);
			totalWorkDone=workSum;
		}
		
		Date d1 = new GregorianCalendar(year_f, month_f, day_f, 00, 00).getTime();
		Date d2 = new GregorianCalendar(lastYear, lastMonth, lastDay, 00, 00).getTime();
		long diff = d2.getTime() - d1.getTime();
		double usedCalendarDays = (double)(diff / AFTime.DAY_IN_MILLIS) + 1; // If all the work is done in one day the value is 1
		
		return totalWorkDone / usedCalendarDays;
	}
	
	protected TimeSeriesCollection getDataset(double averageDailyProgress, Collection<EstimateHistoryEvent> values2, Date startDate, Date endDate) {
		TimeSeries estimateSeries = new TimeSeries("Actual velocity", Day.class);
		TimeSeries referenceSeries = new TimeSeries("Estimated velocity", Day.class);
		
		int day_last=0;
		int month_last=0;
		int year_last=0;
		int worksum=0;
		int count=0;	
		int worktime=0;
		boolean baseIsSet = false;

		for(EstimateHistoryEvent estimateEvent : values2){
			
			AFTime estimate = estimateEvent.getNewEstimate();
			
			if(estimate!=null){
				
				long time = estimate.getTime();
				
				long hours = time / AFTime.HOUR_IN_MILLIS;
				time %= AFTime.HOUR_IN_MILLIS;
				
				long minutes = time / AFTime.MINUTE_IN_MILLIS;
				time %= AFTime.MINUTE_IN_MILLIS;
				
				worktime = Math.round(hours + (minutes/60)); // we want to know the total rounded up in hours
				Date date = estimateEvent.getCreated();
				//String dateStr = date.toString(); // for debugging purposes
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH) + 1; // January == 0
				int year = calendar.get(Calendar.YEAR);
				count++;
				
				/* Adds the first days estimate to be the base for the reference chart */
				if ((day!=day_last || month != month_last || year!=year_last) && (count > 1) && baseIsSet==false){
					
					//referenceSeries.add(new Day(day_last, month_last, year_last), worksum);
					Date start = startDate;
					Calendar cal = Calendar.getInstance();
					cal.setTime(start);
					referenceSeries.add(new Day(cal.get(Calendar.DAY_OF_MONTH), // The date that has first effort for this iteration 
							(cal.get(Calendar.MONTH) +1), 
							cal.get(Calendar.YEAR)), 
							worksum); // The value in the beginning of the baseline	
					
					/* Adds the last day of the estimated velocity to be the last day of iteration to be 0 */
					Date end = endDate;
					cal.setTime(end);
					referenceSeries.add(new Day(cal.get(Calendar.DAY_OF_MONTH), // The date that has first effort for this iteration 
							(cal.get(Calendar.MONTH) +1), 
							cal.get(Calendar.YEAR)), 
							0); // The value in the end of the baseline					
					baseIsSet = true;
				}
				
				// day changed, time to pop the previous days hours
				if ((day!=day_last || month != month_last || year!=year_last) && (count > 1)){
					estimateSeries.add(new Day(day_last, month_last, year_last), worksum);
					worksum=0;
				}
				
				worksum=worksum+worktime; // Summ up the efforts from different tasks
				day_last=day;
				month_last=month;
				year_last=year;		
				
			}	
		}
		if(day_last > 0){ // pop the last days hours
			estimateSeries.add(new Day(day_last, month_last, year_last), worksum);
			/* Create the baseline in the case of only one day that has effort estimates */
			if(baseIsSet==false){
				
				//referenceSeries.add(new Day(day_last, month_last, year_last), worksum);
				Date start = startDate;
				Calendar cal = Calendar.getInstance();
				cal.setTime(start);
				referenceSeries.add(new Day(cal.get(Calendar.DAY_OF_MONTH), // The date that has first effort for this iteration 
						(cal.get(Calendar.MONTH) +1), 
						cal.get(Calendar.YEAR)), 
						worksum); // The value in the beginning of the baseline	
				
				/* Adds the last day of the estimated velocity to be the last day of iteration to be 0 */
				Date end = endDate;
				if(end!=null && (cal.get(Calendar.DAY_OF_MONTH))!=day_last){ // We don't want two same dates to one serie
					cal.setTime(end);
					referenceSeries.add(new Day(cal.get(Calendar.DAY_OF_MONTH), // The date that has first effort for this iteration 
							(cal.get(Calendar.MONTH) +1), 
							cal.get(Calendar.YEAR)), 
							0); // The value in the end of the baseline	
				}
								
				baseIsSet = true;
			}
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(estimateSeries);
		dataset.addSeries(referenceSeries);
		
		TimeSeries trendSeries = createTrendSeries(averageDailyProgress, day_last, month_last, year_last, worksum);
		if (trendSeries != null) {
			dataset.addSeries(trendSeries);
		}
		
		return dataset;
	}

	private TimeSeries createTrendSeries(double averageDailyProgress, int day_last, int month_last, int year_last, double workSum) {
		// Variables that are used to account date setting to the trend line
		int day_tr = day_last;
		int month_tr = month_last - 1; // Convert to Calendar format 0 equals January...
		int year_tr = year_last;
		Calendar cal3 = Calendar.getInstance();
		TimeSeries trendSeries = new TimeSeries("Reference velocity", Day.class);
		
		if (workSum > 0) { // To avoid a gap in the graph
			trendSeries.add(new Day(day_tr, month_tr+1, year_tr), workSum);
		}
		if (averageDailyProgress > 0) {
			while (workSum>0) {
				workSum = workSum - averageDailyProgress;
				if (workSum<0) {
					workSum=0;
				}
				
				cal3.set(year_tr, month_tr, day_tr);
				cal3.add(Calendar.DAY_OF_MONTH, 1);
				day_tr = cal3.get(Calendar.DAY_OF_MONTH);
				month_tr = cal3.get(Calendar.MONTH);
				year_tr = cal3.get(Calendar.YEAR);

				trendSeries.add(new Day(day_tr, month_tr+1, year_tr), workSum);
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
		Date startDate = iterationDAO.get(iterationId).getStartDate(); // We set the start date for burndown graph
		Date endDate = iterationDAO.get(iterationId).getEndDate();// We set the end date for burndown graph

		/*-- Remove the comments of the line below if you want to see the actual workhours in the system --*/
		//dataset.addSeries(workSeries); 
		
		Collection<EstimateHistoryEvent> estimates = estimateHistoryDAO.getEstimateHistory(iterationDAO.get(iterationId));
		Collection<EstimateHistoryEvent> values2 = filterEstimates(estimates);
		double averageDailyProgress = getAverageDailyProgress(performedWorkDAO.getPerformedWork(iterationDAO.get(iterationId)));
		TimeSeriesCollection dataset = getDataset(averageDailyProgress, values2, startDate, endDate);
		JFreeChart chart1 = getChart(dataset, startDate, endDate);
		
		return getChartImageByteArray(chart1);
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
