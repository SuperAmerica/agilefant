package fi.hut.soberit.agilefant.web;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.PerformedWorkDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.PerformedWork;



public class ChartAction extends ActionSupport {
	
	private byte[] result;
	private PerformedWork performedWork;
	private int taskId;
	private int backlogItemId;
	private int iterationId;
	private int deliverableId;
	private TaskDAO taskDAO;
	private BacklogItemDAO backlogItemDAO;
	private IterationDAO iterationDAO;
	private DeliverableDAO deliverableDAO;
	private PerformedWorkDAO performedWorkDAO;
	private Collection<PerformedWork> works;

	
	
	public String execute(){
//		 Create a time series chart
		
		if (taskId > 0){
			works = performedWorkDAO.getPerformedWork(taskDAO.get(taskId));
		} else if (backlogItemId > 0){
			works = performedWorkDAO.getPerformedWork(backlogItemDAO.get(backlogItemId));
		} else if (iterationId > 0){
			works = performedWorkDAO.getPerformedWork(iterationDAO.get(iterationId));
		} else if (deliverableId > 0){
			works = performedWorkDAO.getPerformedWork(deliverableDAO.get(deliverableId));
		}
		
		
		/*-------------------------------------------------------------*/
		// The code for dataset: actual workhours
		
		TimeSeries pop = new TimeSeries("Workhours", Day.class);
	
		int day_last=0;
		int month_last=0;
		int year_last=0;
		int worksum=0;
		int count=0;
		
		for(PerformedWork performedWork : works){
			
			AFTime effort = performedWork.getEffort();
			if(effort!=null){
				long time = effort.getTime();
				long days = time / AFTime.WORKDAY_IN_MILLIS;
				time %= AFTime.WORKDAY_IN_MILLIS;
				
				long hours = time / AFTime.HOUR_IN_MILLIS;
				time %= AFTime.HOUR_IN_MILLIS;
				
				long minutes = time / AFTime.MINUTE_IN_MILLIS;
				time %= AFTime.MINUTE_IN_MILLIS;
				
				int worktime = Math.round(days * 24 + hours + (minutes/60));
				Date date = performedWork.getCreated();
				String dateStr = date.toString(); // for debugging purposes
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH) + 1; // January == 0
				int year = calendar.get(Calendar.YEAR);
				count++;
				
//				 day changed, time to pop the previous days hours
				if ((day!=day_last || month != month_last || year!=year_last) && (count > 1)){
					pop.add(new Day(day_last, month_last, year_last), worksum);
					worksum=0;
				}
				
				worksum=worksum+worktime;
				day_last=day;
				month_last=month;
				year_last=year;
			}
			
			
			
			
		}
		if(worksum > 0){ // pop the last days hours
			pop.add(new Day(day_last, month_last, year_last), worksum);
		}
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(pop);
		
		/*-------------------------------------------------------------*/
		// The code for dataset: effort estimates
		// we only want to keep the last estimate for the day
		
		TimeSeries hip = new TimeSeries("Effort estimates", Day.class);
		
		day_last=0;
		month_last=0;
		year_last=0;
		worksum=0;
		count=0;
		
		for(PerformedWork performedWork : works){
			
			AFTime effort = performedWork.getNewEstimate();
			
			if(effort!=null){
				
				long time = effort.getTime();
				long days = time / AFTime.WORKDAY_IN_MILLIS;
				time %= AFTime.WORKDAY_IN_MILLIS;
				
				long hours = time / AFTime.HOUR_IN_MILLIS;
				time %= AFTime.HOUR_IN_MILLIS;
				
				long minutes = time / AFTime.MINUTE_IN_MILLIS;
				time %= AFTime.MINUTE_IN_MILLIS;
				
				int worktime = Math.round(days * 24 + hours + (minutes/60));
				Date date = performedWork.getCreated();
				String dateStr = date.toString(); // for debugging purposes
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH) + 1; // January == 0
				int year = calendar.get(Calendar.YEAR);
				count++;
				
				// day changed, time to pop the previous days hours
				if ((day!=day_last || month != month_last || year!=year_last) && (count > 1)){
					hip.add(new Day(day_last, month_last, year_last), worksum);
					worksum=0;
				}
				
				worksum=worktime; // No summing up
				day_last=day;
				month_last=month;
				year_last=year;
				
			}	
		}
		if(worksum > 0){ // pop the last days hours
			hip.add(new Day(day_last, month_last, year_last), worksum);
		}
		
		dataset.addSeries(hip);
		
		/*-------------------------------------------------------------*/
		
		JFreeChart chart1 = ChartFactory.createTimeSeriesChart(
		"Agilefant07 workhours per day",
		"Date",
		"Workhours",
		dataset,
		true,
		true,
		false);
		XYPlot plot = chart1.getXYPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
		
		XYItemRenderer rend = plot.getRenderer();
		XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer)rend;
		rr.setShapesVisible(true);
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(out, chart1, 500, 300);
			result = out.toByteArray();		
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}
	
	public InputStream getInputStream(){
		return new ByteArrayInputStream(result);
	}

	public BacklogItemDAO getBacklogItemDAO() {
		return backlogItemDAO;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	public int getBacklogItemId() {
		return backlogItemId;
	}

	public void setBacklogItemId(int backlogItemId) {
		this.backlogItemId = backlogItemId;
	}

	public DeliverableDAO getDeliverableDAO() {
		return deliverableDAO;
	}

	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
		this.deliverableDAO = deliverableDAO;
	}

	public int getDeliverableId() {
		return deliverableId;
	}

	public void setDeliverableId(int deliverableId) {
		this.deliverableId = deliverableId;
	}

	public IterationDAO getIterationDAO() {
		return iterationDAO;
	}

	public void setIterationDAO(IterationDAO iterationDAO) {
		this.iterationDAO = iterationDAO;
	}

	public int getIterationId() {
		return iterationId;
	}

	public void setIterationId(int iterationId) {
		this.iterationId = iterationId;
	}

	public PerformedWork getPerformedWork() {
		return performedWork;
	}

	public void setPerformedWork(PerformedWork performedWork) {
		this.performedWork = performedWork;
	}

	public PerformedWorkDAO getPerformedWorkDAO() {
		return performedWorkDAO;
	}

	public void setPerformedWorkDAO(PerformedWorkDAO performedWorkDAO) {
		this.performedWorkDAO = performedWorkDAO;
	}

	public byte[] getResult() {
		return result;
	}

	public void setResult(byte[] result) {
		this.result = result;
	}

	public TaskDAO getTaskDAO() {
		return taskDAO;
	}

	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Collection<PerformedWork> getWorks() {
		return works;
	}

	public void setWorks(Collection<PerformedWork> works) {
		this.works = works;
	}

}
