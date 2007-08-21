/**
 * 
 */
package fi.hut.soberit.agilefant.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.EffortHistory;
import fi.hut.soberit.agilefant.model.Iteration;

import fi.hut.soberit.agilefant.util.BacklogValueInjector;

/**
 * @author mpmerila, ialehto
 */
public class ChartManagerImpl implements ChartManager {
	private static final Log log = LogFactory.getLog(ChartManagerImpl.class);

	private IterationDAO iterationDAO;
	private EffortHistoryDAO effortHistoryDAO;
	private BacklogItemDAO backlogItemDAO;
	private TaskEventDAO taskEventDAO;
	
	private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Generates a byte array (a png image file) from a JFreeChart object
	 * 
	 * @param chart A chart object from which the image is created
	 * @return Byte array representing a png image file
	 */
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

	/**
	 * Retrives data from DAOs and inserts it into TimeSeriesCollections
	 * 
	 * @param backlog The backlog (iteration) of which time series are generated
	 * @param startDate First day to be plotted
	 * @param endDate Last day to be plotted
	 * @return
	 */
	protected TimeSeriesCollection getDataset(Backlog backlog, Date startDate, Date endDate) {
		EffortHistory effortHistory;
		long effLeft = 0L;
		TimeSeries estimateSeries = new TimeSeries("Actual velocity", Day.class);
		TimeSeries referenceSeries = new TimeSeries("Reference velocity", Day.class);

		/* First estimateSeries data point is the first original estimate */	
		BacklogValueInjector.injectMetrics(backlog, 
				new java.sql.Date(startDate.getTime()), 
				taskEventDAO, backlogItemDAO);

		effortHistory = effortHistoryDAO.getByDateAndBacklog(
				new java.sql.Date(startDate.getTime()), backlog);
		
		if (effortHistory != null) {
			effLeft = effortHistory.getOriginalEstimate().getTime();;
		} else {
			/* If no effort history found for given date, use the latest
			 * if available, if not the use zero. */
			effortHistory = effortHistoryDAO.getLatest(
					new java.sql.Date(startDate.getTime()), backlog);
			if (effortHistory != null) {
				effLeft = effortHistory.getOriginalEstimate().getTime();
			} else {
				effLeft = 0L;
			}
		}	
		estimateSeries.add(new Day(startDate), effLeft/3600000);
		
		/* Add effort left data points to estimateSeries*/
		GregorianCalendar i = new GregorianCalendar();
		GregorianCalendar end = new GregorianCalendar();
		i.setTime(startDate);
		end.setTime(endDate);
		while(!i.after(end) && !i.after(GregorianCalendar.getInstance())) {
			effortHistory = effortHistoryDAO.getByDateAndBacklog(
					new java.sql.Date(i.getTimeInMillis()), backlog);
			i.add(Calendar.DATE, 1);
			if(effortHistory != null)
				effLeft = effortHistory.getEffortLeft().getTime();
			estimateSeries.add(new Day(i.getTime()), effLeft/3600000);
		}

		/* Reference series first data point is the latest original estimate
		 * available. The last point is zero */
		referenceSeries.add(new Day(startDate), 
				backlog.getBliOrigEstSum().getTime()/3600000);
		
		/* EndDate should be one day bigger in chart */
		GregorianCalendar newEndDate = new GregorianCalendar();
		newEndDate.setTime(endDate);
		newEndDate.add(GregorianCalendar.DATE, 1);
		referenceSeries.add(new Day(newEndDate.getTime()), 0); 
		
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(estimateSeries);
		dataset.addSeries(referenceSeries);

		return dataset;
	} 
	
	/**
	 * Method for constructing a JFreeChart object. Topics, labels, axis, tick
	 * units and rendering types are set.
	 * 
	 * @param dataset Data points of effort left to be plotted on chart
	 * @param startDate First day to be plotted
	 * @param endDate Last day to be plotted
	 * @return
	 */
	protected JFreeChart getChart(TimeSeriesCollection dataset, Date startDate, Date endDate) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Iteration burndown",
				"Date",
				"Effort left",
				dataset,
				true,
				true,
				false);
		XYPlot plot = chart.getXYPlot();
		DateAxis axis = (DateAxis) plot.getDomainAxis();

		// Set time axis properties
		axis.setDateFormatOverride(new SimpleDateFormat("EEE d.M.")); 
		axis.setMinimumDate(startDate);
		
		GregorianCalendar newEndDate = new GregorianCalendar();
		newEndDate.setTime(endDate);
		newEndDate.add(GregorianCalendar.DATE, 1);
		axis.setMaximumDate(newEndDate.getTime());
		
		if((endDate.getTime() - startDate.getTime()) < (8*24*60*60*1000))
			axis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 1)); 
		else 
			axis.setAutoTickUnitSelection(true);	
		XYItemRenderer rend = plot.getRenderer();
		XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer) rend;
		rr.setShapesVisible(true);
		return chart;
	}
	
	/**
	 * Create an iteration burndown chart as a byte array that is
	 * interpreted as a .png file
	 * 
	 * @param iterationId Id of the iteration of which the burndown is generated
	 * @return Byte array representing a png image file
	 */
	public byte[] getIterationBurndown(int iterationId) {
		Iteration iteration = iterationDAO.get(iterationId);
		Date startDate = iteration.getStartDate();
		Date endDate = iteration.getEndDate();
		
		TimeSeriesCollection effLeftTimeSeries = getDataset(iteration, startDate, endDate);
		JFreeChart burndownGraph = getChart(effLeftTimeSeries, startDate, endDate);
		
		return getChartImageByteArray(burndownGraph);
	}

	/**
	 * @return the backlogItemDAO
	 */
	public BacklogItemDAO getBacklogItemDAO() {
		return backlogItemDAO;
	}

	/**
	 * @param backlogItemDAO the backlogItemDAO to set
	 */
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	/**
	 * @return the effortHistoryDAO
	 */
	public EffortHistoryDAO getEffortHistoryDAO() {
		return effortHistoryDAO;
	}

	/**
	 * @param effortHistoryDAO the effortHistoryDAO to set
	 */
	public void setEffortHistoryDAO(EffortHistoryDAO effortHistoryDAO) {
		this.effortHistoryDAO = effortHistoryDAO;
	}

	/**
	 * @return the iterationDAO
	 */
	public IterationDAO getIterationDAO() {
		return iterationDAO;
	}

	/**
	 * @param iterationDAO the iterationDAO to set
	 */
	public void setIterationDAO(IterationDAO iterationDAO) {
		this.iterationDAO = iterationDAO;
	}

	/**
	 * @return the taskEventDAO
	 */
	public TaskEventDAO getTaskEventDAO() {
		return taskEventDAO;
	}

	/**
	 * @param taskEventDAO the taskEventDAO to set
	 */
	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}

}
