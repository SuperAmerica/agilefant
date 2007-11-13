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
import org.jfree.ui.RectangleInsets;

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

    private static final int DEFAULT_WIDTH = 780;

    private static final int DEFAULT_HEIGHT = 600;

    private static final int SMALL_WIDTH = 150;

    private static final int SMALL_HEIGHT = 150;

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
        EffortHistory effortHistory;
        long effLeft = 0L;
        TimeSeries estimateSeries = new TimeSeries("Actual velocity", Day.class);
        TimeSeries referenceSeries = new TimeSeries("Reference velocity",
                Day.class);
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

        /* First estimateSeries data point is the first original estimate */
        BacklogValueInjector.injectMetrics(backlog, startDate, taskEventDAO,
                backlogItemDAO);

        effortHistory = effortHistoryDAO.getByDateAndBacklog(new java.sql.Date(
                startDate.getTime()), backlog);

        /*
         * Use start date's effort history as start point for effort left
         * series.
         */
        if (effortHistory != null) {
            effLeft = effortHistory.getOriginalEstimate().getTime();
        } else {
            /*
             * If no effort history found for start date, use the most recent
             * effort left from history
             */
            effortHistory = effortHistoryDAO.getMostRecent(new java.sql.Date(
                    startDate.getTime()), backlog);
            if (effortHistory != null) {
                effLeft = effortHistory.getEffortLeft().getTime();
            } else {
                effortHistory = effortHistoryDAO.getLatest(new java.sql.Date(
                        startDate.getTime()), new java.sql.Date(endDate
                        .getTime()), backlog);
                if (effortHistory != null) {
                    effLeft = effortHistory.getOriginalEstimate().getTime();
                    i.setTime(effortHistory.getDate());

                } else {
                    effLeft = 0L;
                }
            }
        }
        estimateSeries.add(new Day(i.getTime()), (float) effLeft / 3600000.0);
        /*
         * If first data point is after start date fill previous dates with zero
         * data points
         */
        if (i.getTime().after(startDate)) {
            GregorianCalendar fillDate = new GregorianCalendar();
            fillDate.setTime(startDate);
            fillDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
            fillDate.set(GregorianCalendar.MINUTE, 0);
            fillDate.set(GregorianCalendar.SECOND, 0);
            while (fillDate.before(i)) {
                estimateSeries.add(new Day(fillDate.getTime()), 0.0);
                fillDate.add(Calendar.DATE, 1);
            }
        }

        /* Add effort left data points to estimateSeries */

        /*
         * Display the effortLeft data point of each effort history day in the
         * next day (the effort left "tomorrow morning")
         */
        while (!i.after(end) && !i.after(GregorianCalendar.getInstance())) {
            effortHistory = effortHistoryDAO.getByDateAndBacklog(
                    new java.sql.Date(i.getTimeInMillis()), backlog);
            i.add(Calendar.DATE, 1);
            if (effortHistory != null)
                effLeft = effortHistory.getEffortLeft().getTime();
            estimateSeries.add(new Day(i.getTime()),
                    (float) effLeft / 3600000.0);
        }

        /*
         * Reference series first data point is the latest original estimate
         * available. The last point is zero
         */
        referenceSeries.add(new Day(startDate), (float) backlog
                .getBliOrigEstSum().getTime() / 3600000.0);

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
     * @param dataset
     *                Data points of effort left to be plotted on chart
     * @param startDate
     *                First day to be plotted
     * @param endDate
     *                Last day to be plotted
     * @return
     */
    protected JFreeChart getChart(TimeSeriesCollection dataset, Date startDate,
            Date endDate) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Iteration burndown", "Date", "Effort left", dataset, true,
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

        if ((endDate.getTime() - startDate.getTime()) < (8 * 24 * 60 * 60 * 1000))
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
                endDate);

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
                startDate, endDate));

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
     * @return the effortHistoryDAO
     */
    public EffortHistoryDAO getEffortHistoryDAO() {
        return effortHistoryDAO;
    }

    /**
     * @param effortHistoryDAO
     *                the effortHistoryDAO to set
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
     * @param iterationDAO
     *                the iterationDAO to set
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
     * @param taskEventDAO
     *                the taskEventDAO to set
     */
    public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
        this.taskEventDAO = taskEventDAO;
    }

}
