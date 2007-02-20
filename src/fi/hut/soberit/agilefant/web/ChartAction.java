package fi.hut.soberit.agilefant.web;


import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
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
import fi.hut.soberit.agilefant.model.Portfolio;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.service.PortfolioManager;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.StandardCategoryURLGenerator;


public class ChartAction extends ActionSupport {
	private static final Log log = LogFactory.getLog(ChartAction.class);
	
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
	private int workDone;
	private double effortDone;
	private double effortLeft;
	private PortfolioManager portfolioManager;
	private Portfolio portfolio;
	private double notStarted;
	private double started;
	private double blocked;
	private double implemented;
	private double done;
	private Date startDate;
	private Date endDate;
	
	
	/**
	 * This method draws the iteration burndown chart.
	 * 
	 * 
	 */
	public String execute(){
//		 Create a time series chart
		
		if (taskId > 0){
			works = performedWorkDAO.getPerformedWork(taskDAO.get(taskId));
		} else if (backlogItemId > 0){
			works = performedWorkDAO.getPerformedWork(backlogItemDAO.get(backlogItemId));
		} else if (iterationId > 0){
			works = performedWorkDAO.getPerformedWork(iterationDAO.get(iterationId));
			startDate = iterationDAO.get(iterationId).getStartDate();
			endDate = iterationDAO.get(iterationId).getEndDate();
		} else if (deliverableId > 0){
			works = performedWorkDAO.getPerformedWork(deliverableDAO.get(deliverableId));
		}
		
		log.info("TaskID = " + taskId);
		
		/*-------------------------------------------------------------*/
		// The code for dataset: actual workhours
		
		TimeSeries workSeries = new TimeSeries("Workhours", Day.class);
			
		int day_last=0;
		int month_last=0;
		int year_last=0;
		int worksum=0;
		int count=0;
		int worktime=0;
		
		// The date for the first work effort
		int day_f=0;
		int month_f=0;
		int year_f=0;
		double totalWorkDone=0;		
		
		for(PerformedWork performedWork : works){
			
			AFTime effort = performedWork.getEffort();
			if(effort!=null){
				long time = effort.getTime();
				/* not needed, when we want to know the sum in hours only */
				//long days = time / AFTime.WORKDAY_IN_MILLIS;
				//time %= AFTime.WORKDAY_IN_MILLIS;
				//long days = time / AFTime.DAY_IN_MILLIS;
				//time %= AFTime.DAY_IN_MILLIS; // We remove the full days from the sum
				
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
				if ((day!=day_last || month != month_last || year!=year_last) && (count > 1)){
					workSeries.add(new Day(day_last, month_last, year_last), worksum);
					//worksum=0; Can be used if the effort is wanted per day
				}else if(count==1){
					day_f=day;
					month_f=month;
					year_f=year;
				}
				
				worksum=worksum+worktime;
				day_last=day;
				month_last=month;
				year_last=year;
			}		
			
		}
		if((worksum > 0) && (day_last > 0)){ // pop the last days hours
			workSeries.add(new Day(day_last, month_last, year_last), worksum);
			totalWorkDone=worksum;
		}
		
		Date d1 = new GregorianCalendar(year_f, month_f, day_f, 00, 00).getTime();
		Date d2 = new GregorianCalendar(year_last, month_last, day_last, 00, 00).getTime();
		long diff = d2.getTime() - d1.getTime();
		double usedCalendarDays = (double)(diff / (1000 * 60 * 60 * 24)) + 1; // If all the work is done in one day the value is 1

		
		double averageDailyProgress = totalWorkDone/usedCalendarDays; // Here we account how fast the trendline will drop
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		
		/*-- Shows the done workhours
		//dataset.addSeries(workSeries); 
		
		/*-------------------------------------------------------------*/
		// The code for dataset: effort estimates
		// we only want to keep the last estimate for the day
		
		TimeSeries estimateSeries = new TimeSeries("Effort estimates", Day.class);
		TimeSeries trendSeries = new TimeSeries("Estimated progress", Day.class);
		
		day_last=0;
		month_last=0;
		year_last=0;
		worksum=0;
		count=0;	
		worktime=0;
		
		
		for(PerformedWork performedWork : works){
			
			AFTime effort = performedWork.getNewEstimate();
			
			if(effort!=null){
				
				long time = effort.getTime();
				/*
				long days = time / AFTime.DAY_IN_MILLIS;
				time %= AFTime.DAY_IN_MILLIS;
				*/
				long hours = time / AFTime.HOUR_IN_MILLIS;
				time %= AFTime.HOUR_IN_MILLIS;
				
				long minutes = time / AFTime.MINUTE_IN_MILLIS;
				time %= AFTime.MINUTE_IN_MILLIS;
				
				//worktime = Math.round(days * 24 + hours + (minutes/60));
				worktime = Math.round(hours + (minutes/60)); // we want to know the total in hours
				Date date = performedWork.getCreated();
				//String dateStr = date.toString(); // for debugging purposes
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int month = calendar.get(Calendar.MONTH) + 1; // January == 0
				int year = calendar.get(Calendar.YEAR);
				count++;
				
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
		}
		
		dataset.addSeries(estimateSeries);
		
		// Variables that are used to account date setting to the trend line
		double workRemaining = worksum;
		int day_tr = day_last;
		int month_tr = month_last;
		int year_tr = year_last;
		
		if(workRemaining>0){ // To avoid a gap in the graph
			trendSeries.add(new Day(day_tr, month_tr, year_tr), workRemaining);
		}
		if(averageDailyProgress > 0){
			while(workRemaining>0){
				workRemaining = workRemaining - averageDailyProgress;
				if(workRemaining<0){
					workRemaining=0;
				}
				
				if(month_tr == 2 && day_tr==29){
					month_tr=3;
					day_tr=1;
				}else if(month_tr==12 && day_tr==31){
					day_tr=1;
					month_tr=1;
					year_tr++;
				}else if((month_tr==4 || month_tr==6 || month_tr==9 || month_tr==11) && (day_tr==30)){
					day_tr=1;
					month_tr++;
				}else if((month_tr==1 || month_tr==3 || month_tr==5 || month_tr==7 || month_tr==8 || month_tr==10) && (day_tr==31)){
					day_tr=1;
					month_tr++;
				}else {
					day_tr++;
				}
				trendSeries.add(new Day(day_tr, month_tr, year_tr), workRemaining);
			}
			dataset.addSeries(trendSeries);
		}
		
		
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
		
		axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy")); // Here we set how the time axis should look like
		
		/* we want to set the start date to be official start day*/
		Date iterStartDate = this.getStartDate();
		if(iterStartDate != null){
			Date min = axis.getMinimumDate();
			if(min.after(iterStartDate)){
				axis.setMinimumDate(iterStartDate); // If there is no work done before the start of the iteration
			}
		}
		
		/* We want to set the end date to be official end day */
		Date iterEndDate = this.getEndDate();
		if(iterEndDate != null){
			Date max = axis.getMaximumDate();
			if(max.before(iterEndDate)){
				axis.setMaximumDate(iterEndDate); // If there is no work done after the end of the iteration
			}
		}
		
		//axis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 7)); // A way to set how often dates are showing in the time axis
		
		XYItemRenderer rend = plot.getRenderer();
		XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer)rend;
		rr.setShapesVisible(true);
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(out, chart1, 780, 600);
			result = out.toByteArray();		
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}
	
	
	/**
	 * Bar chart takes two parameters, effort done and effort left. 
	 * The procentage of work compleated is calculated based on theses two numbers.
	 * Finally the bar chart is returned back as a png-image.
	 * 
	 * @param effortDone
	 * @param effortLeft
	 * @return
	 */
	public String barChart(){
		
		// Intitializing variables
		double done = 0;
		double left = 0;
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart2 = null;
		
		
		if(effortDone > 0){
			done = effortDone;
		}
		
		if(effortLeft > 0){
			left = effortLeft;
		}

		// We want to avoid division by zero
		if(done > 0 || left > 0){ // Two parameters chart
			double donePros = ((double)done / (double)(done+left))*100;
			dataset.setValue(donePros, "done", "");
			dataset.setValue((100 - donePros), "left", "");
			double stringPros = Math.round(donePros); // We want the output neat!
			chart2 = ChartFactory.createStackedBarChart("",
					"", ""+ stringPros +" % done", dataset, PlotOrientation.HORIZONTAL,
					true, true, false);
		} else { // Chart based on one "workDone" parameter
			dataset.setValue(workDone, "done", "");
			dataset.setValue((100 - workDone), "left", "");
			chart2 = ChartFactory.createStackedBarChart("",
					"", ""+ workDone +" % done", dataset, PlotOrientation.HORIZONTAL,
					true, true, false);
		}
		
		CategoryPlot plot = chart2.getCategoryPlot();
		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesItemLabelsVisible(0, false);
		renderer.setSeriesPaint(0, Color.green);
		renderer.setSeriesPaint(1, Color.red);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(out, chart2, 200, 100);
			result = out.toByteArray();		
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}	
	
	/**
	 * Extended Bar chart takes five parameters, the amount of work not started, started, blocked, implemented and done. 
	 * Finally the bar chart is returned back as a png-image.
	 * 
	 * @return
	 */
	public String extendedBarChart(){
		
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart2 = null;
		
		/*-- We want to show the relative percentages of different work states --*/
		double allTypes = 0;
		allTypes = this.getNotStarted() + this.getStarted() + this.getBlocked() + this.getDone() + this.getImplemented();
		double ns = (this.getNotStarted() / allTypes)*100;
		double st = (this.getStarted() / allTypes)*100;
		double bl = (this.getBlocked() / allTypes)*100;
		double im = (this.getImplemented() / allTypes)*100;
		double dn = (this.getDone() / allTypes)*100;
		if(allTypes>0){
			dataset.setValue(ns, "Not started", "");
			dataset.setValue(st, "Started", "");
			dataset.setValue(bl, "Blocked", "");
			dataset.setValue(im, "Implemented", "");
			dataset.setValue(dn, "Done", "");
			chart2 = ChartFactory.createStackedBarChart(null,
					null, null, dataset, PlotOrientation.HORIZONTAL,
					false, false, false);
			
		}
		
		CategoryPlot plot = chart2.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		CategoryAxis axis = plot.getDomainAxis();
		
		/* -- some efforts to get rid of the outline scale, no success so far -- */
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);		
		axis.setAxisLineVisible(false);
		axis.setTickLabelsVisible(false);
		axis.setTickMarksVisible(false);
		renderer.setDrawBarOutline(false);
		renderer.setOutlineStroke(null);
		/*-----------------------------------------*/
		
		axis.setCategoryMargin(0.01); // one percent
		renderer.setSeriesPaint(0, Color.red); // color for not started
		renderer.setSeriesPaint(1, Color.pink); // color for started
		renderer.setSeriesPaint(2, Color.blue); // color for blocked
		renderer.setSeriesPaint(3, Color.green); // color for implemented
		renderer.setSeriesPaint(4, Color.orange); // color for done
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(out, chart2, 100, 30);
			result = out.toByteArray();		
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}	
	
	/**
	 * Draws the gantt chart with default settings for viewing 
	 * current date plus three months
	 * 
	 * @return
	 */	
	public String ganttChart(){
		
		TaskSeries s1 = new TaskSeries("Scheduled");
		portfolio = portfolioManager.getCurrentPortfolio();
		Collection<Deliverable> deliverables = portfolio.getDeliverables();
		for(Deliverable deliverable : deliverables){
			Date startDate = deliverable.getStartDate();
			Date endDate = deliverable.getEndDate();
			String name = deliverable.getName();
			Task del1 = new Task(
		            name, startDate, endDate
		    );
		    del1.setPercentComplete(1.00);
		    
		    
		    Collection<Iteration> iterations = deliverable.getIterations();
		    int count = 0;
		    for(Iteration iteration : iterations){
		    	Date iterStartDate = iteration.getStartDate();
				Date iterEndDate = iteration.getEndDate();
				count++;
				String iter_name = "Iteraation "+count+"";
				if(iterStartDate!=null && iterEndDate!=null){
					Task iter1 = new Task(iter_name, iterStartDate, iterEndDate);
					del1.addSubtask(iter1);
				}
				
		    }
		    
		    s1.add(del1);
		    
		}
		
		TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(s1);
        
        IntervalCategoryDataset dataset = collection;
        
        // create the chart...
        JFreeChart chart3 = ChartFactory.createGanttChart(
            "Development portfolio",  // chart title
            "Activity",              // domain axis label
            "Date",              // range axis label
            dataset,             // data
            true,                // include legend
            true,                // tooltips
            false                // urls
        );
        CategoryPlot plot = (CategoryPlot) chart3.getPlot();
        
        
        /*----Adjusting the date axis---------*/
        ValueAxis rangeAxis = plot.getRangeAxis();
        DateAxis axis = (DateAxis) rangeAxis;
        Date current = new GregorianCalendar().getTime();
        Calendar calendar = Calendar.getInstance();
		calendar.setTime(current);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1; // January == 0
		int year = calendar.get(Calendar.YEAR);
		
		/*-experimental for setting the start date */
		
		if(this.getStartDate()!=null){
			axis.setMinimumDate(this.getStartDate());
		}else {
			Date startingDate = calendar.getTime();
			axis.setMinimumDate(startingDate); // Sets the gantt to show dates starting from today.
		}
		
		//Date startingDate = calendar.getTime();
		axis.setMinimumDate(current); // Sets the gantt to show dates starting from today.

        int endMonth = (month + 3) % 13;
        if(endMonth == 13){
        	endMonth = 1;
        }
        if(endMonth==1 || endMonth==2 || endMonth==3){ 
        	year++;
        }
        if(endMonth == 2 && day>28){
        	day = 28; // February has only 28 days
        }else if((endMonth == 4 || endMonth == 6 || endMonth == 9 || endMonth == 11) && (day>30)){
        	day = 30; 
        }
        calendar.set(year, endMonth, day); // Sets the ending date
        
        /* --experimental for setting the end date */
        
        if(this.getEndDate()!=null){
			axis.setMinimumDate(this.getEndDate()); // Sets the gantt to show dates until the specified ending date.
		}else {
			Date endingDate = calendar.getTime(); // Get the new modified date three month from the original
	        axis.setMaximumDate(endingDate);
		}
        
        Date endingDate = calendar.getTime(); // Get the new modified date three month from the original
        axis.setMaximumDate(endingDate);
        
        
        /*----------------------------------------------*/
        
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, Color.blue);
        CategoryURLGenerator generator = new StandardCategoryURLGenerator(
        "index.html", "series", "category"); 
        renderer.setItemURLGenerator(generator);
        //renderer.setURLGenerator(new StandardCategoryURLGenerator("gant_chart_juttu.jsp"));
        ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection()); 
        
        try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(out, chart3, 780, 600, info);
			result = out.toByteArray();		
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}	
		    
	    /**
	     * Utility method for creating <code>Date</code> objects.
	     *
	     * @param day  the date.
	     * @param month  the month.
	     * @param year  the year.
	     *
	     * @return a date.
	     */
	    private static Date date(final int day, final int month, final int year) {

	        Calendar calendar = Calendar.getInstance();
	        calendar.set(year, month, day);
	        Date result = calendar.getTime();
	        return result;

	    }
	

	/*---------------------------------------------------------------------------*/

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
	
	public int getWorkDone() {
		return workDone;
	}

	public void setWorkDone(int workDone) {
		this.workDone = workDone;
	}


	public double getEffortDone() {
		return effortDone;
	}


	public void setEffortDone(double effortDone) {
		this.effortDone = effortDone;
	}


	public double getEffortLeft() {
		return effortLeft;
	}


	public void setEffortLeft(double effortLeft) {
		this.effortLeft = effortLeft;
	}
	
		
	public Portfolio getPortfolio() {
		return portfolio;
	}

	
	public void setPortfolioManager(PortfolioManager portfolioManager) {
		this.portfolioManager = portfolioManager;
	}
	

	public double getBlocked() {
		return blocked;
	}


	public void setBlocked(double blocked) {
		this.blocked = blocked;
	}


	public double getDone() {
		return done;
	}


	public void setDone(double done) {
		this.done = done;
	}


	public double getImplemented() {
		return implemented;
	}


	public void setImplemented(double implemented) {
		this.implemented = implemented;
	}


	public double getNotStarted() {
		return notStarted;
	}


	public void setNotStarted(double notStarted) {
		this.notStarted = notStarted;
	}


	public double getStarted() {
		return started;
	}


	public void setStarted(double started) {
		this.started = started;
	}


	public Date getEndDate() {
		return endDate;
	}


	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Date getStartDate() {
		return startDate;
	}


	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}








}
