/**
 * 
 */
package fi.hut.soberit.agilefant.business.impl;

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
import org.jfree.ui.RectangleInsets;

import fi.hut.soberit.agilefant.business.ChartBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.HistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.db.ProjectDAO;

/**
 * @author mpmerila, ialehto
 */
public class ChartBusinessImpl implements ChartBusiness {
    private static final Log log = LogFactory.getLog(ChartBusinessImpl.class);

    private IterationDAO iterationDAO;
    
    private ProjectDAO projectDAO;

    private BacklogItemDAO backlogItemDAO;

    private static final int DEFAULT_WIDTH = 780;

    private static final int DEFAULT_HEIGHT = 600;

    private static final int SMALL_WIDTH = 110;

    private static final int SMALL_HEIGHT = 85;

    /**
     * Generates a byte array (a png image file) from a JFreeChart object
     * 
     * @param chart
     *                A chart object from which the image is created
     * @return Byte array representing a png image file
     */
    protected byte[] getChartImageByteArray(JFreeChart chart) {
        return getChartImageByteArray(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Generates a byte array (a png image file) from a JFreeChart object
     * 
     * @param chart
     *                A chart object from which the image is created
     * @param width
     *                Width of the created image
     * @param height
     *                Height of the created image
     * @return Byte array representing a png image file
     */
    protected byte[] getChartImageByteArray(JFreeChart chart, int width,
            int height) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(out, chart, width, height);
            return out.toByteArray();
        } catch (IOException e) {
            log.warn("Problem occurred creating chart", e);
            return null;
        }
    }

    /**
     * Retrieves data from DAOs and inserts it into TimeSeriesCollections
     * 
     * @param backlog
     *                The backlog (iteration) of which time series are generated
     * @param startDate
     *                First day to be plotted
     * @param endDate
     *                Last day to be plotted
     * @return
     */

    protected TimeSeriesCollection getDataset(Backlog backlog, Date startDate,
            Date endDate) {
        BacklogHistory history;
        long effLeft = 0L;
        TimeSeries estimateSeries = new TimeSeries("Actual velocity", Day.class);
        TimeSeries referenceSeries = new TimeSeries("Reference velocity",
                Day.class);
        TimeSeries currentDaySeries = new TimeSeries("Current day", Day.class);
        
        GregorianCalendar i = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        end.set(GregorianCalendar.HOUR_OF_DAY, 0);
        end.set(GregorianCalendar.MINUTE, 0);
        end.set(GregorianCalendar.SECOND, 0);
        i.setTime(startDate);
        i.set(GregorianCalendar.HOUR_OF_DAY, 0);
        i.set(GregorianCalendar.MINUTE, 0);
        i.set(GregorianCalendar.SECOND, 0);
        end.setTime(endDate);

        history = backlog.getBacklogHistory();
        estimateSeries.add(new Day(i.getTime()), (float) history 
                .getDateEntry(startDate).getOriginalEstimate()
                .getTime() / 3600.0);
        referenceSeries.add(new Day(i.getTime()), (float) history
                .getLatestEntry().getOriginalEstimate()
                .getTime() / 3600.0);
        GregorianCalendar newEndDate = new GregorianCalendar();
        newEndDate.setTime(endDate);
        newEndDate.add(GregorianCalendar.DATE, 1);
        referenceSeries.add(new Day(newEndDate.getTime()), 0);

        /*for (HistoryEntry<?> entry : history.getEffortHistoryEntries()) {
            i.setTime(entry.getDate());
            i.add(Calendar.DATE, 1);
            estimateSeries.add(new Day(i.getTime()), (float) entry
                    .getEffortLeft().getTime() / 3600000.0);
        }*/
        
        GregorianCalendar now = new GregorianCalendar();
        now.setTime( new Date() );
       
        
        //TODO: Refactor this if possible
        while (!i.after(now)) {
            HistoryEntry<BacklogHistory> entry = history.getDateEntry(i.getTime()); 
            i.add(Calendar.DATE, 1);
            estimateSeries.add(new Day(i.getTime()),
                    (float) entry.getEffortLeft()
                            .getTime() / 3600.0);
        }
        
       
         /* Create the current day "series" */
        // First remove the current (last) date from the estimated data series
        estimateSeries.delete(
                estimateSeries.getItemCount() -1,
                estimateSeries.getItemCount() -1);
        
        // Then create the data "series" for the current date
        i.set(GregorianCalendar.DATE, i.get(GregorianCalendar.DATE) - 1);
        HistoryEntry<BacklogHistory> entry = history.getDateEntry(i.getTime());
        currentDaySeries.add(new Day(i.getTime()),
                (float) estimateSeries.getValue(new Day(i.getTime())).floatValue());
        i.add(Calendar.DATE, 1);
        entry = history.getDateEntry(i.getTime());
        currentDaySeries.add(new Day(i.getTime()),
                (float) entry.getEffortLeft().getTime() / 3600.0);
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(estimateSeries);
        dataset.addSeries(referenceSeries);
        dataset.addSeries(currentDaySeries);
        return dataset;
    }

    /**
     * Method for constructing a JFreeChart object. Topics, labels, axis, tick
     * units and rendering types are set.
     * 
     * @param dataset
     *                Data points of effort left to be plotted on chart
     * @param startDate
     *                First day to be plotted
     * @param endDate
     *                Last day to be plotted
     * @return
     */
    protected JFreeChart getChart(TimeSeriesCollection dataset, Date startDate,
            Date endDate, String title) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                title, "Date", "Effort left", dataset, true,
                true, false);
        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        GregorianCalendar newEndDate;
        GregorianCalendar newStartDate;
        // Set time axis properties
        axis.setDateFormatOverride(new SimpleDateFormat("EEE d.M."));
        // Use java.sql.Date to use only days, months and years

        newStartDate = new GregorianCalendar();
        newStartDate.setTime(startDate);
        newStartDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
        newStartDate.set(GregorianCalendar.MINUTE, 0);
        newStartDate.set(GregorianCalendar.SECOND, 0);

        axis.setMinimumDate(newStartDate.getTime());

        newEndDate = new GregorianCalendar();

        // Use java.sql.Date to use only days, months and years
        newEndDate.setTime(new java.sql.Date(endDate.getTime()));

        newEndDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
        newEndDate.set(GregorianCalendar.MINUTE, 0);
        newEndDate.set(GregorianCalendar.SECOND, 0);

        newEndDate.add(GregorianCalendar.DATE, 1);

        axis.setMaximumDate(newEndDate.getTime());

        if ((endDate.getTime() - startDate.getTime()) < (8 * 24 * 60 * 60))
            axis.setTickUnit(new DateTickUnit(DateTickUnit.DAY, 1));
        else
            axis.setAutoTickUnitSelection(true);
        XYItemRenderer rend = plot.getRenderer();
        XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer) rend;
        rr.setShapesVisible(true);
        return chart;
    }

    /**
     * Remove details from a JFreeChart object representing a burndown graph to
     * make it better suited for viewing in small size
     * 
     * @param burndownChart
     *                A chart object to be trimmed
     * @return the trimmed JFreeChart object
     */
    protected JFreeChart trimChart(JFreeChart burndownChart) {
        JFreeChart chart = burndownChart;
        XYPlot plot = chart.getXYPlot();

        chart.setTitle("");
        chart.removeLegend();
        plot.getRangeAxis().setVisible(false);
        plot.getDomainAxis().setVisible(false);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);

        XYItemRenderer rend = plot.getRenderer();
        XYLineAndShapeRenderer rr = (XYLineAndShapeRenderer) rend;
        rr.setShapesVisible(false);
        rr.setSeriesPaint(0, java.awt.Color.red);
        rr.setSeriesPaint(1, java.awt.Color.blue);

        // Trims the padding around the chart
        RectangleInsets ins = new RectangleInsets(-6, -8, -3, -7);
        chart.setPadding(ins);

        return chart;
    }

    /**
     * Create an iteration burndown chart as a byte array that is interpreted as
     * a .png file
     * 
     * @param iterationId
     *                Id of the iteration of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public byte[] getIterationBurndown(int iterationId) {
        Iteration iteration = iterationDAO.get(iterationId);
        Date startDate = iteration.getStartDate();
        Date endDate = iteration.getEndDate();

        TimeSeriesCollection effLeftTimeSeries = getDataset(iteration,
                startDate, endDate);
        JFreeChart burndownGraph = getChart(effLeftTimeSeries, startDate,
                endDate, "Iteration burndown");

        return getChartImageByteArray(burndownGraph);
    }
    
    /**
     * Create a project burndown chart as a byte array that is interpreted as
     * a .png file
     * 
     * @param projectId
     *                Id of the project of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public byte[] getProjectBurndown(int projectId) {
        Project project = projectDAO.get(projectId);
        Date startDate = project.getStartDate();
        Date endDate = project.getEndDate();

        TimeSeriesCollection effLeftTimeSeries = getDataset(project,
                startDate, endDate);
        JFreeChart burndownGraph = getChart(effLeftTimeSeries, startDate,
                endDate, "Project burndown");

        return getChartImageByteArray(burndownGraph);
    }

    /**
     * Create a small iteration burndown chart as a byte array that is
     * interpreted as a .png file
     * 
     * @param iterationId
     *                Id of the iteration of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public byte[] getSmallIterationBurndown(int iterationId) {
        Iteration iteration = iterationDAO.get(iterationId);
        Date startDate = iteration.getStartDate();
        Date endDate = iteration.getEndDate();

        TimeSeriesCollection effLeftTimeSeries = getDataset(iteration,
                startDate, endDate);
        JFreeChart burndownGraph = trimChart(getChart(effLeftTimeSeries,
                startDate, endDate, "Iteration burndown"));

        return getChartImageByteArray(burndownGraph, SMALL_WIDTH, SMALL_HEIGHT);
    }

    /**
     * Create a small project burndown chart as a byte array that is
     * interpreted as a .png file
     * 
     * @param projectId
     *                Id of the iteration of which the burndown is generated
     * @return Byte array representing a png image file
     */
    public byte[] getSmallProjectBurndown(int projectId) {
        Project project = projectDAO.get(projectId);
        Date startDate = project.getStartDate();
        Date endDate = project.getEndDate();

        TimeSeriesCollection effLeftTimeSeries = getDataset(project,
                startDate, endDate);
        JFreeChart burndownGraph = trimChart(getChart(effLeftTimeSeries,
                startDate, endDate, "Project burndown"));

        return getChartImageByteArray(burndownGraph, SMALL_WIDTH, SMALL_HEIGHT);
    }
    
    /**
     * @return the backlogItemDAO
     */
    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    /**
     * @param backlogItemDAO
     *                the backlogItemDAO to set
     */
    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    /**
     * @return the iterationDAO
     */
    public IterationDAO getIterationDAO() {
        return iterationDAO;
    }

    /**
     * @param iterationDAO
     *                the iterationDAO to set
     */
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }
}
