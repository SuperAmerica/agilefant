package fi.hut.soberit.agilefant.web;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.RectangleInsets;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.PerformedWorkDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Portfolio;
import fi.hut.soberit.agilefant.service.ChartManager;
import fi.hut.soberit.agilefant.service.PortfolioManager;

public class ChartAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

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

	private Color color1;

	private Color color2;

	private Color color3;

	private Color color4;

	private Color color5;

	private ChartManager chartManager;

	/**
	 * This method draws the iteration burndown chart.
	 */
	public String execute() {
		if (iterationId > 0) {
			result = chartManager.getIterationBurndown(iterationId);
		}

		// Create a time series chart
		/*
		 * if (taskId > 0){ works =
		 * performedWorkDAO.getPerformedWork(taskDAO.get(taskId)); } else if
		 * (backlogItemId > 0){ works =
		 * performedWorkDAO.getPerformedWork(backlogItemDAO.get(backlogItemId)); }
		 * else if (iterationId > 0){ estimates =
		 * estimateHistoryDAO.getEstimateHistory(iterationDAO.get(iterationId));
		 * works =
		 * performedWorkDAO.getPerformedWork(iterationDAO.get(iterationId));
		 * startDate = iterationDAO.get(iterationId).getStartDate(); // We set
		 * the start date for burndown graph endDate =
		 * iterationDAO.get(iterationId).getEndDate();// We set the end date for
		 * burndown graph } else if (deliverableId > 0){ works =
		 * performedWorkDAO.getPerformedWork(deliverableDAO.get(deliverableId)); }
		 */

		return Action.SUCCESS;
	}

	public String smallChart() {
		if (iterationId > 0) {
			result = chartManager.getSmallIterationBurndown(iterationId);
		}
		return Action.SUCCESS;
	}

	/**
	 * Bar chart takes two parameters, effort done and effort left. The
	 * procentage of work compleated is calculated based on theses two numbers.
	 * Finally the bar chart is returned back as a png-image.
	 * 
	 * @param effortDone
	 * @param effortLeft
	 * @return
	 */
	public String barChart() {

		// Intitializing variables
		double done = 0;
		double left = 0;

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart2 = null;

		if (effortDone > 0) {
			done = effortDone;
		}

		if (effortLeft > 0) {
			left = effortLeft;
		}

		// We want to avoid division by zero
		if (done > 0 || left > 0) { // Two parameters chart
			double donePros = ((double) done / (double) (done + left)) * 100;
			dataset.setValue(donePros, "done", "");
			dataset.setValue((100 - donePros), "left", "");
			double stringPros = Math.round(donePros); // We want the output
			// neat!
			chart2 = ChartFactory.createStackedBarChart("", "", "" + stringPros
					+ " % done", dataset, PlotOrientation.HORIZONTAL, true,
					true, false);
		} else { // Chart based on one "workDone" parameter
			dataset.setValue(workDone, "done", "");
			dataset.setValue((100 - workDone), "left", "");
			chart2 = ChartFactory.createStackedBarChart("", "", "" + workDone
					+ " % done", dataset, PlotOrientation.HORIZONTAL, true,
					true, false);
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
	 * Extended Bar chart takes five parameters, the amount of work not started,
	 * started, blocked, implemented and done. Finally the bar chart is returned
	 * back as a png-image.
	 * 
	 * @return
	 */
	public String extendedBarChart() {

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		JFreeChart chart2 = null;

		/*-- We want to show the relative percentages of different work states --*/
		double allTypes = 0;
		allTypes = this.getNotStarted() + this.getStarted() + this.getBlocked()
				+ this.getDone() + this.getImplemented();
		double ns = 20;
		double st = 20;
		double bl = 20;
		double im = 20;
		double dn = 20;
		if (allTypes > 0) {
			ns = (this.getNotStarted() / allTypes) * 100.0;
			st = (this.getStarted() / allTypes) * 100.0;
			bl = (this.getBlocked() / allTypes) * 100.0;
			im = (this.getImplemented() / allTypes) * 100.0;
			dn = (this.getDone() / allTypes) * 100.0;
		}

		dataset.setValue(ns, "Not started", "");
		dataset.setValue(st, "Started", "");
		dataset.setValue(bl, "Blocked", "");
		dataset.setValue(im, "Implemented", "");
		dataset.setValue(dn, "Done", "");
		chart2 = ChartFactory.createStackedBarChart(null, null, null, dataset,
				PlotOrientation.HORIZONTAL, false, false, false);

		CategoryPlot plot = chart2.getCategoryPlot();
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		// CategoryAxis axis = plot.getDomainAxis();
		ValueAxis axis = plot.getRangeAxis();

		/* -- some efforts to get rid of the outline scale, no success so far -- */
		plot.setDomainGridlinesVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setBackgroundPaint(Color.white);

		/* Make background transparent? */
		// plot.setBackgroundAlpha(100);
		chart2.setBorderVisible(false); // Disable chart border
		// Sets the background color for the chart
		chart2.setBackgroundPaint(Color.white);

		/* Make background transparent? */
		// chart2.setBackgroundImageAlpha(100);
		// No padding around chart
		chart2.setPadding(new RectangleInsets(0, 0, 0, 0));

		axis.setAxisLineVisible(false);
		axis.setTickLabelsVisible(false);
		axis.setTickMarksVisible(false);
		renderer.setDrawBarOutline(false);
		renderer.setOutlineStroke(null);
		/*-----------------------------------------*/

		// axis.setCategoryMargin(0.01); // one percent
		if (color1 != null) {
			renderer.setSeriesPaint(0, this.getColor1()); // color for not
			// started
		} else {
			renderer.setSeriesPaint(0, new Color(0xd3, 0xd3, 0xd3)); // color
			// for
			// not
			// started
			// (light
			// gray)
		}

		if (color2 != null) {
			renderer.setSeriesPaint(1, this.getColor2()); // color for started
		} else {
			renderer.setSeriesPaint(1, new Color(0xff, 0x99, 0x0)); // color for
			// started
			// (orange)
		}

		if (color3 != null) {
			renderer.setSeriesPaint(2, this.getColor3()); // color for blocked
		} else {
			renderer.setSeriesPaint(2, Color.red); // color for blocked
		}

		if (color4 != null) {
			renderer.setSeriesPaint(3, this.getColor4()); // color for
			// implemented
		} else {
			renderer.setSeriesPaint(3, Color.green); // color for implemented
		}

		if (color5 != null) {
			renderer.setSeriesPaint(4, this.getColor5()); // color for done
		} else {
			renderer.setSeriesPaint(4, new Color(0x00, 0x77, 0x00)); // color
			// for
			// done
		}

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ChartUtilities.writeChartAsPNG(out, chart2, 110, // here we set
					// the width of
					// the total bar
					// in pixels
					15); // here we set the hight of the bar in pixels
			result = out.toByteArray();
		} catch (IOException e) {
			System.err.println("Problem occurred creating chart.");
		}
		return Action.SUCCESS;
	}

	/**
	 * Draws the gantt chart with default settings for viewing current date plus
	 * three months
	 * 
	 * @return
	 */
	public String ganttChart() {

		TaskSeries s1 = new TaskSeries("Scheduled");
		portfolio = portfolioManager.getCurrentPortfolio();
		Collection<Deliverable> deliverables = portfolio.getDeliverables();
		for (Deliverable deliverable : deliverables) {
			Date starting = deliverable.getStartDate();
			Date ending = deliverable.getEndDate();
			String name = deliverable.getName();
			Task del1 = new Task(name, starting, ending);
			del1.setPercentComplete(1.00);

			Collection<Iteration> iterations = deliverable.getIterations();
			int count = 0;
			for (Iteration iteration : iterations) {
				Date iterStartDate = iteration.getStartDate();
				Date iterEndDate = iteration.getEndDate();
				count++;
				String iter_name = "Iteraation " + count + "";
				if (iterStartDate != null && iterEndDate != null) {
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
				"Development portfolio", // chart title
				"Activity", // domain axis label
				"Date", // range axis label
				dataset, // data
				true, // include legend
				true, // tooltips
				false // urls
				);
		CategoryPlot plot = (CategoryPlot) chart3.getPlot();

		/*----Adjusting the date axis---------*/
		ValueAxis rangeAxis = plot.getRangeAxis();
		DateAxis axis = (DateAxis) rangeAxis;
		Date current = new GregorianCalendar().getTime();
		Calendar calendar = Calendar.getInstance();
		int month;
		int day;
		int year;
		if (this.getStartDate() != null) { // User has set starting date
			calendar.setTime(this.getStartDate());
			day = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH) + 1; // January == 0
			year = calendar.get(Calendar.YEAR);
		} else {
			calendar.setTime(current);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH) + 1; // January == 0
			year = calendar.get(Calendar.YEAR);
		}

		/*-experimental for setting the start date */
		if (this.getStartDate() != null) { // User has set the starting date
			axis.setMinimumDate(this.getStartDate());
		} else { // start date field is left empty
			axis.setMinimumDate(current); // Sets the gantt to show dates
			// starting from today.
		}
		// Here create a calendar object that has a date 3 month from now
		int endMonth = (month + 3) % 13;
		if (endMonth == 13) {
			endMonth = 1;
		}
		if (endMonth == 1 || endMonth == 2 || endMonth == 3) {
			year++;
		}
		if (endMonth == 2 && day > 28) {
			day = 28; // February has only 28 days
		} else if ((endMonth == 4 || endMonth == 6 || endMonth == 9 || endMonth == 11)
				&& (day > 30)) {
			day = 30;
		}
		calendar.set(year, endMonth, day); // Sets the ending date

		/* --experimental for setting the end date */

		// String str2 = this.getStartDateString();
		Date d2 = this.getEndDate();
		Date d1 = this.getStartDate();
		if (d1 != null && d2 != null) { // User has set both starting and ending
			// dates
			if (d1.before(d2)) {
				axis.setMaximumDate(this.getEndDate());
			} else { // Ending date was before starting date, not allowed to
				// be set for axis
				Date endingDate = calendar.getTime();
				axis.setMaximumDate(endingDate); // Set ending date three
				// month from user defined
				// starting date.
			}
		} else { // the end date field is left empty
			Date endingDate = calendar.getTime(); // Get the new modified date
			// three month from the
			// original
			axis.setMaximumDate(endingDate);
		}

		/*----------------------------------------------*/

		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.blue);
		CategoryURLGenerator generator = new StandardCategoryURLGenerator(
				"index.html", "series", "category");
		renderer.setItemURLGenerator(generator);
		// renderer.setURLGenerator(new
		// StandardCategoryURLGenerator("gant_chart_juttu.jsp"));
		ChartRenderingInfo info = new ChartRenderingInfo(
				new StandardEntityCollection());

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
	 * @param day
	 *            the date.
	 * @param month
	 *            the month.
	 * @param year
	 *            the year.
	 * 
	 * @return a date.
	 */
	@SuppressWarnings("unused")
	private static Date date(final int day, final int month, final int year) {

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		Date result = calendar.getTime();
		return result;

	}

	/*---------------------------------------------------------------------------*/

	public InputStream getInputStream() {
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

	public Color getColor1() {
		return color1;
	}

	public void setColor1(Color color1) {
		this.color1 = color1;
	}

	public Color getColor2() {
		return color2;
	}

	public void setColor2(Color color2) {
		this.color2 = color2;
	}

	public Color getColor3() {
		return color3;
	}

	public void setColor3(Color color3) {
		this.color3 = color3;
	}

	public Color getColor4() {
		return color4;
	}

	public void setColor4(Color color4) {
		this.color4 = color4;
	}

	public Color getColor5() {
		return color5;
	}

	public void setColor5(Color color5) {
		this.color5 = color5;
	}

	public void setChartManager(ChartManager chartManager) {
		this.chartManager = chartManager;
	}
}
