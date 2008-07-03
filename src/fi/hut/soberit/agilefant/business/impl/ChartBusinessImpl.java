package fi.hut.soberit.agilefant.business.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
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
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.RectangleInsets;

import fi.hut.soberit.agilefant.business.ChartBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.HistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.DailyWorkLoadData;

/**
 * @author mpmerila, ialehto
 */
public class ChartBusinessImpl implements ChartBusiness {
    private static final Log log = LogFactory.getLog(ChartBusinessImpl.class);

    private IterationDAO iterationDAO;
    
    private ProjectDAO projectDAO;

    private BacklogItemDAO backlogItemDAO;
    
    private UserDAO userDAO;
    
    private SettingBusiness settingBusiness;
    
    private ProjectBusiness projectBusiness;
    
    private HistoryBusiness historyBusiness;
   
    private Date expectedDate;

    
    /* Default chart size */
    private static final int DEFAULT_WIDTH = 780;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int SMALL_WIDTH = 110;
    private static final int SMALL_HEIGHT = 85;
    
    /* Series numbers */
    private static final int BURNDOWN_SERIES = 0;
    private static final int CURRENT_DAY_SERIES = 1;
    private static final int SCOPING_SERIES = 2;
    private static final int REFERENCE_SERIES = 3;
    private static final int EXPECTED_SERIES = 4;
    
    /* Series colors */
    private static Color BURNDOWN_COLOR = new Color(220, 100, 87);
    private static Color REFERENCE_COLOR = new Color(90, 145, 210);
    private static Color EXPECTED_COLOR = new Color(247, 150, 70); //new Color(30, 180, 100);

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
        TimeSeries estimateSeries = new TimeSeries("Actual velocity", Second.class);
        TimeSeries referenceSeries = new TimeSeries("Reference velocity",
                Day.class);
        TimeSeries currentDaySeries = new TimeSeries("Current day", Day.class);
        TimeSeries deltaEffortLeftSeries = new TimeSeries("Scoping", Second.class);
        TimeSeries expectedSeries = new TimeSeries("Expected velocity", Day.class);        
        
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
        estimateSeries.add(new Second(i.getTime()), (float) history 
                .getDateEntry(startDate).getOriginalEstimate()
                .getTime() / 3600.0);
        referenceSeries.add(new Day(i.getTime()), (float) history
                .getLatestEntry().getOriginalEstimate()
                .getTime() / 3600.0);
        GregorianCalendar newEndDate = new GregorianCalendar();
        newEndDate.setTime(endDate);
        newEndDate.add(GregorianCalendar.DATE, 1);
        referenceSeries.add(new Day(newEndDate.getTime()), 0);

        /*
         * Set the "now" calendar to be one day late, so that current day
         * can be left out
         */
        GregorianCalendar now = new GregorianCalendar();
        now.setTime( new Date() );
        now.add(Calendar.DATE, -1);
       
        
        //TODO: Refactor this if possible
        while (!i.after(now)) {
            HistoryEntry<BacklogHistory> entry = history.getDateEntry(i.getTime()); 
            i.add(Calendar.DATE, 1);
            Second second = new Second(i.getTime());
            
            // Add the point to the estimate series
            estimateSeries.add(second,
                    (float) entry.getEffortLeft()
                            .getTime() / 3600.0);
            
            /*
             * If there is scoping done during the day, draw it accordingly.
             * I.e. draw the scoping line between effort left and EL-scope
             * and the new effort left dot.
             */ 
            if (entry.getDeltaEffortLeft() != null &&
                    entry.getDeltaEffortLeft().getTime() != 0) {
                float original = (float) (entry.getEffortLeft().getTime() / 3600.0);
                float scoped = original + (float)(entry.getDeltaEffortLeft().getTime() / 3600.0);
                
                // Add the scoping line
                deltaEffortLeftSeries.add(second, original);
                deltaEffortLeftSeries.add(second.next(), scoped);
                deltaEffortLeftSeries.add(second.next().next(), null);
                
                // The effort estimate line
                estimateSeries.add(second.next(), null);
                estimateSeries.add(second.next().next(), scoped);
            }
        }
        
        // Create the data "series" for the current date
        i.setTime(new Date());
        i.add(Calendar.DATE, -1);
        HistoryEntry<BacklogHistory> entry = history.getDateEntry(i.getTime());
        long startPoint = entry.getEffortLeft().getTime() + entry.getDeltaEffortLeft().getTime();
        i.add(Calendar.DATE, 1);
        currentDaySeries.add(new Day(i.getTime()), (float)(startPoint / 3600.0));
        i.add(Calendar.DATE, 1);
        entry = history.getDateEntry(i.getTime());
        currentDaySeries.add(new Day(i.getTime()),
                (float) entry.getEffortLeft().getTime() / 3600.0);
       
        // Create the expected series
        i.setTime(new Date());
        i.add(Calendar.DATE, -1);
        entry = history.getDateEntry(i.getTime());
        AFTime velocity = historyBusiness.calculateDailyVelocity(backlog.getId());
        i.add(Calendar.DATE, 1);
        entry = history.getDateEntry(i.getTime());
        Date expectedDate = historyBusiness.calculateExpectedDate(backlog, entry.getOriginalEstimate(), velocity);
        if (expectedDate != null) {
            this.expectedDate = expectedDate;
            expectedSeries.add(new Day(i.getTime()), (float)(startPoint / 3600.0));
            expectedSeries.addOrUpdate(new Day(expectedDate), 0);
        }
        
        // Add the series in correct order
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(estimateSeries);
        dataset.addSeries(currentDaySeries);
        dataset.addSeries(deltaEffortLeftSeries);
        dataset.addSeries(referenceSeries);
        if (expectedDate != null &&
                (new Date()).before(backlog.getEndDate())) {
            dataset.addSeries(expectedSeries);
        }
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
        if (this.expectedDate == null ||
                endDate.after(this.expectedDate)) {
            newEndDate.setTime(new java.sql.Date(endDate.getTime()));
        }
        else {
            newEndDate.setTime(new java.sql.Date(this.expectedDate.getTime()));
            newEndDate.add(Calendar.DATE, 1);
        }

        // Set the burndown to end at midnight
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
              
        // Set estimate series properties
        rr.setSeriesPaint(BURNDOWN_SERIES, BURNDOWN_COLOR);
        rr.setSeriesShape(BURNDOWN_SERIES, new java.awt.Rectangle(-2, -2, 4, 4));
        rr.setSeriesShapesVisible(BURNDOWN_SERIES, true);
        rr.setSeriesStroke(BURNDOWN_SERIES, new BasicStroke(2.0f));
        
        // Set current day series properties
        rr.setSeriesPaint(CURRENT_DAY_SERIES, BURNDOWN_COLOR);
        rr.setSeriesShape(CURRENT_DAY_SERIES, new Rectangle(-2, -2, 4, 4));
        rr.setSeriesShapesFilled(CURRENT_DAY_SERIES, false);
        rr.setSeriesShapesVisible(CURRENT_DAY_SERIES, true);
        rr.setSeriesStroke(CURRENT_DAY_SERIES, new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 7.0f, 3.0f }, 0.0f));
        
        // Set scoping series properties
        rr.setSeriesPaint(SCOPING_SERIES, BURNDOWN_COLOR);
        rr.setSeriesShapesVisible(SCOPING_SERIES, false);
        rr.setSeriesStroke(SCOPING_SERIES, new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 4.0f }, 0.0f));
        
        // Set reference series properties
        rr.setSeriesPaint(REFERENCE_SERIES, REFERENCE_COLOR);
        rr.setSeriesShapesVisible(REFERENCE_SERIES, false);
        rr.setSeriesStroke(REFERENCE_SERIES, new BasicStroke(2.0f));
        
        // Set expected series properties
        rr.setSeriesPaint(EXPECTED_SERIES, EXPECTED_COLOR);
        rr.setSeriesStroke(EXPECTED_SERIES, new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 7.0f, 3.0f, 2.0f, 3.0f, 2.0f, 3.0f }, 10.0f));
        
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
        rr.setStroke(new BasicStroke(1.0f));
        rr.setSeriesPaint(0, BURNDOWN_COLOR);
        rr.setSeriesPaint(1, BURNDOWN_COLOR);
        rr.setSeriesPaint(2, BURNDOWN_COLOR);
        rr.setSeriesPaint(3, REFERENCE_COLOR);

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
        
        try {
            effLeftTimeSeries.removeSeries(EXPECTED_SERIES);
        }
        catch (Exception e) {}
        
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
        
        try {
            effLeftTimeSeries.removeSeries(EXPECTED_SERIES);
        }
        catch (Exception e) {}
        
        JFreeChart burndownGraph = trimChart(getChart(effLeftTimeSeries,
                startDate, endDate, "Project burndown"));

        return getChartImageByteArray(burndownGraph, SMALL_WIDTH, SMALL_HEIGHT);
    }
    
    public byte[] getLoadMeter(int userId) {               
        
        User user = userDAO.get(userId);               
        // Scope load by days left in the week.
        int totalWeekDays = 5;
        Calendar cal = GregorianCalendar.getInstance();
        int daysLeft = 1;
        while(cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && daysLeft < 6) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
            daysLeft++;
        }
        // If user not found in database, set weekhours to 40h and load to 0h.
        // Now we at least get a 0h-chart drawn.
        long weekHours = 40*60*60;
        if (user != null)
            weekHours = (long) (user.getWeekHours().getTime() * (1.0 * daysLeft / totalWeekDays));
        
        DailyWorkLoadData loadData = projectBusiness.getDailyWorkLoadData(user, 1);
        long loadTime = 0;
        if (loadData != null)
            loadTime = (long) loadData.getWeeklyTotals().get(loadData.getWeekNumbers().get(0)).getTime();
        int load = 0;
        if (weekHours > 0)
            load = (int)(loadTime * 100.0 / weekHours);
        
        int rangeLow = settingBusiness.getRangeLow();
        int optimalLow = settingBusiness.getOptimalLow();
        int optimalHigh = settingBusiness.getOptimalHigh();
        int criticalLow = settingBusiness.getCriticalLow();
        int rangeHigh = settingBusiness.getRangeHigh();  
        String loadWarning = "";        
        if (load < rangeLow)
            load = rangeLow;
        if (load > rangeHigh)
            load = rangeHigh;
        if (load < optimalLow)
            loadWarning = "Low";
        else if (load < optimalHigh)
            loadWarning = "Optimal";
        else if (load < criticalLow)
            loadWarning = "High";
        else if (load <= rangeHigh)
            loadWarning = "Far too high!";
                           
        DefaultValueDataset data = new DefaultValueDataset(); 
        data.setValue(Integer.valueOf(load));
        
        MeterPlot plot = new MeterPlot(data);        
        plot.setDialShape(DialShape.CHORD);       
        plot.setDialBackgroundPaint(new Color(100, 100, 100));
        plot.setBackgroundAlpha(0.0f);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRange(new Range(rangeLow, rangeHigh));
        plot.setDialOutlinePaint(Color.BLACK);
        plot.setNeedlePaint(Color.BLACK);
        plot.setTickLabelsVisible(false);
        plot.setTickLabelPaint(Color.darkGray);
        plot.setTickPaint(new Color(0, 0, 0, 100));
        plot.setTickLabelFormat(NumberFormat.getNumberInstance());
        plot.setTickSize(rangeHigh);
        plot.setValuePaint(new Color(255, 255, 255, 0));
        plot.addInterval(new MeterInterval("Low", new Range(rangeLow, optimalLow), Color.black, new BasicStroke(2.0f), Color.lightGray));
        plot.addInterval(new MeterInterval("High", new Range(optimalLow, optimalHigh), Color.black, new BasicStroke(2.0f), new Color(80, 140, 10, 255 ) ));
        plot.addInterval(new MeterInterval("Critical Low", new Range(optimalHigh, criticalLow), Color.black, new BasicStroke(2.0f), new Color(200, 100, 0, 255)));
        plot.addInterval(new MeterInterval("Critical High", new Range(criticalLow, rangeHigh), Color.black, new BasicStroke(2.0f), new Color(160, 0, 0, 255)));
        plot.setValueFont(new Font("Arial", Font.PLAIN, 12));
        
        JFreeChart chart = new JFreeChart("Load: " + loadWarning, new Font("Arial", Font.PLAIN, 12), plot, false);
        chart.setBackgroundPaint(Color.WHITE);
        
        return getChartImageByteArray(chart, 150, 150);
               
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

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public SettingBusiness getSettingBusiness() {
        return settingBusiness;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

    public ProjectBusiness getProjectBusiness() {
        return projectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }
   
}
