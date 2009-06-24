package fi.hut.soberit.agilefant.business.impl;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.ui.RectangleInsets;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationBurndownBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.util.ExactEstimateUtils;
import fi.hut.soberit.agilefant.util.Pair;

/**
 * A business class for calculating the burndown for iterations.
 * <p>
 * All methods are marked initially as read-only transactions.
 * Override with <code>@Transactional</code>.
 * 
 * <b>Note</b>: the date handling in burndown chart calculations
 * Effort left sum for a day is drawn at next midnight.
 * I.e. The effort left sum of 4.6. is drawn in the burndown at 5.6. 00.00.
 * 
 * Scoping done, i.e. changes to original estimate, are drawn at the beginning
 * of each day.
 * I.e. The scoping done on 4.6. is drawn in the burndown at 4.6. 00.00.
 * 
 * @author rjokelai, jsorvett
 *
 */
@Service("iterationBurndownBusiness")
@Transactional(readOnly = true)
public class IterationBurndownBusinessImpl implements IterationBurndownBusiness {

    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;    
    
    @Autowired
    private IterationBusiness iterationBusiness;
    
    /* Chart sizes */
    protected static final int DEFAULT_WIDTH = 780;
    protected static final int DEFAULT_HEIGHT = 600;
    protected static final int SMALL_WIDTH = 110;
    protected static final int SMALL_HEIGHT = 85;


    /* Chart backgrounds */
    protected static final Color CHART_BACKGROUND_COLOR = Color.white;
    protected static final Color PLOT_BACKGROUND_COLOR  = Color.white;
    protected static final Color GRIDLINE_COLOR         = new Color(0xcc, 0xcc, 0xcc);
    
    /* Axis titles */
    protected static final String DATE_AXIS_LABEL       = "Date";
    protected static final String EFFORT_AXIS_LABEL     = "Effort left";
    
    /* Series numbers */
    protected static final int BURNDOWN_SERIES_NO       = 0;
    protected static final int CURRENT_DAY_SERIES_NO    = 1;
    protected static final int SCOPING_SERIES_NO        = 2;
    protected static final int REFERENCE_SERIES_NO      = 3;
    protected static final int EXPECTED_SERIES_NO       = 4;
    
    /* Series colors */
    protected static final Color BURNDOWN_SERIES_COLOR          = new Color(220, 100, 87);
    protected static final Color CURRENT_DAY_SERIES_COLOR       = BURNDOWN_SERIES_COLOR;
    protected static final Color SCOPING_SERIES_COLOR           = BURNDOWN_SERIES_COLOR;
    protected static final Color REFERENCE_SERIES_COLOR         = new Color(90, 145, 210);
    protected static final Color EXPECTED_SERIES_COLOR                 = new Color(80, 80, 80);
    
    /* Series shape */
    protected static final Shape BURNDOWN_SERIES_SHAPE                  = new Rectangle(-2, -2, 4, 4);
    protected static final boolean BURNDOWN_SERIES_SHAPE_VISIBLE        = true;
    protected static final Shape CURRENT_DAY_SERIES_SHAPE               = new Rectangle(-2, -2, 4, 4);
    protected static final boolean CURRENT_DAY_SERIES_SHAPE_VISIBLE     = true;
    protected static final boolean CURRENT_DAY_SERIES_SHAPE_FILLED      = false;
    
    /* Series stroke */
    protected static final Stroke SMALL_BURNDOWN_STROKE = new BasicStroke(1.0f);
    protected static final Stroke CURRENT_DAY_SERIES_STROKE = new BasicStroke(
            1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0.0f, new float[] { 7.0f, 3.0f }, 0.0f);
    protected static final Stroke SCOPING_SERIES_STROKE = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
            0.0f, new float[] { 2.0f, 4.0f }, 0.0f);
    protected static final Stroke EXPECTED_SERIES_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 4.0f }, 0.0f);
    
    /* Series names */
    protected static final String BURNDOWN_SERIES_NAME = "Iteration burndown";
    protected static final String REFERENCE_SERIES_NAME = "Reference velocity";
    protected static final String SCOPING_SERIES_NAME = "Scoping";
    protected static final String CURRENT_DAY_SERIES_NAME = "Current day";
    protected static final String EXPECTED_SERIES_NAME = "Predicted velocity";

    @Autowired
    public void setIterationHistoryEntryBusiness(
            IterationHistoryEntryBusiness iterationHistoryEntryBusiness) {
        this.iterationHistoryEntryBusiness = iterationHistoryEntryBusiness;
    }
    
    public void setIterationBusiness(IterationBusiness iterationBusiness) {
        this.iterationBusiness = iterationBusiness;
    }

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
            e.printStackTrace();
        }
        return null;
    }
    
    
    /** {@inheritDoc} */
    public byte[] getIterationBurndown(Iteration iteration) {
        return getChartImageByteArray(constructChart(iteration));
    }
    
    public byte[] getSmallIterationBurndown(Iteration iteration) {
        return getChartImageByteArray(constructSmallChart(iteration), SMALL_WIDTH, SMALL_HEIGHT);
    }
    
    protected JFreeChart constructChart(Iteration iteration, boolean drawLegend) {
        return constructChart(iteration);
    }

    protected JFreeChart constructChart(Iteration iteration) {
        JFreeChart burndown = ChartFactory.createTimeSeriesChart(BURNDOWN_SERIES_NAME,
                DATE_AXIS_LABEL,
                EFFORT_AXIS_LABEL,
                getDataset(iteration),
                true, true, false);
        
        formatChartAxes(burndown,
                new DateTime(iteration.getStartDate()),
                new DateTime(iteration.getEndDate()));
        
        formatChartStyle(burndown);
        
        return burndown;
    }
    
    protected JFreeChart constructSmallChart(Iteration iteration) {
        JFreeChart burndown = this.constructChart(iteration);
        return transformToSmallChart(burndown);
    }
    
    /**
     * Trims and transforms a big burndown chart to a small one.
     */
    protected JFreeChart transformToSmallChart(JFreeChart burndownChart) {
        JFreeChart chart = burndownChart;
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)plot.getRenderer();
        
        chart.setBackgroundPaint(CHART_BACKGROUND_COLOR);
        plot.setBackgroundPaint(PLOT_BACKGROUND_COLOR);
        
        renderer.setSeriesPaint(BURNDOWN_SERIES_NO, BURNDOWN_SERIES_COLOR);
        renderer.setSeriesPaint(CURRENT_DAY_SERIES_NO, BURNDOWN_SERIES_COLOR);
        renderer.setSeriesPaint(SCOPING_SERIES_NO, BURNDOWN_SERIES_COLOR);
        renderer.setSeriesPaint(REFERENCE_SERIES_NO, REFERENCE_SERIES_COLOR);
        
        renderer.setSeriesStroke(BURNDOWN_SERIES_NO, SMALL_BURNDOWN_STROKE);
        renderer.setSeriesStroke(CURRENT_DAY_SERIES_NO, SMALL_BURNDOWN_STROKE);
        renderer.setSeriesStroke(SCOPING_SERIES_NO, SMALL_BURNDOWN_STROKE);
        renderer.setSeriesStroke(REFERENCE_SERIES_NO, SMALL_BURNDOWN_STROKE);
        
        renderer.setSeriesShapesVisible(BURNDOWN_SERIES_NO, false);
        renderer.setSeriesShapesVisible(CURRENT_DAY_SERIES_NO, false);
        renderer.setSeriesShapesVisible(SCOPING_SERIES_NO, false);
        renderer.setSeriesShapesVisible(REFERENCE_SERIES_NO, false);
        
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.getDomainAxis().setVisible(false);
        plot.getRangeAxis().setVisible(false);
        
        plot.getDomainAxis().setLabel(null);
        plot.getRangeAxis().setLabel(null);
        
        RectangleInsets ins = new RectangleInsets(-6, -8, -3, -7);
        chart.setPadding(ins);

        if (plot.getDataset() != null) {
            TimeSeriesCollection dataset = (TimeSeriesCollection) plot.getDataset();            
            // HORROR!
            // Will break horribly if some series is missing because the indexes cannot be trusted!!  
            // The indexes are defined as constants but the indexes come directly
            // from the order in which the series are added
            // If one series is missing, EXPECTED_SERIES_NO = 4 but the index for expected series is 3
            // (and it's even possible that it doesn't exist!)
            if (dataset.getSeriesCount() > EXPECTED_SERIES_NO) {
                dataset.removeSeries(EXPECTED_SERIES_NO);
            }
        }
        
        chart.removeLegend();
        chart.setTitle("");
        
        return chart;
    }
    
    /**
     * Sets the chart's and plot's background colors.
     */
    protected void formatChartStyle(JFreeChart chart) {
        chart.setBackgroundPaint(CHART_BACKGROUND_COLOR);
        chart.getPlot().setBackgroundPaint(PLOT_BACKGROUND_COLOR);
        
        setSeriesStyles(chart);
    }
    
    protected void setSeriesStyles(JFreeChart chart) {
        XYLineAndShapeRenderer rend = (XYLineAndShapeRenderer)chart.getXYPlot().getRenderer();
        
        rend.setSeriesPaint(BURNDOWN_SERIES_NO, BURNDOWN_SERIES_COLOR);
        rend.setSeriesShape(BURNDOWN_SERIES_NO, BURNDOWN_SERIES_SHAPE);
        rend.setSeriesShapesVisible(BURNDOWN_SERIES_NO, BURNDOWN_SERIES_SHAPE_VISIBLE);
        
        rend.setSeriesPaint(REFERENCE_SERIES_NO, REFERENCE_SERIES_COLOR);
        
        rend.setSeriesPaint(CURRENT_DAY_SERIES_NO, CURRENT_DAY_SERIES_COLOR);
        rend.setSeriesStroke(CURRENT_DAY_SERIES_NO, CURRENT_DAY_SERIES_STROKE);
        rend.setSeriesShape(CURRENT_DAY_SERIES_NO, CURRENT_DAY_SERIES_SHAPE);
        rend.setSeriesShapesVisible(CURRENT_DAY_SERIES_NO, CURRENT_DAY_SERIES_SHAPE_VISIBLE);
        rend.setSeriesShapesFilled(CURRENT_DAY_SERIES_NO, CURRENT_DAY_SERIES_SHAPE_FILLED);
        
        rend.setSeriesPaint(SCOPING_SERIES_NO, SCOPING_SERIES_COLOR);
        rend.setSeriesStroke(SCOPING_SERIES_NO, SCOPING_SERIES_STROKE);
        
        rend.setSeriesPaint(EXPECTED_SERIES_NO, EXPECTED_SERIES_COLOR);
        rend.setSeriesStroke(EXPECTED_SERIES_NO, EXPECTED_SERIES_STROKE);
    }
    
    /**
     * Sets the correct start and end dates and date format.
     */
    protected void formatChartAxes(JFreeChart chart, DateTime start, DateTime end) {
        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis)plot.getDomainAxis();
        axis.setMaximumDate(end.plusDays(1).toDateMidnight().toDate());
        axis.setMinimumDate(start.toDateMidnight().toDate());
        axis.setDateFormatOverride(new SimpleDateFormat("EEE d.M."));
        
        plot.setDomainGridlinePaint(GRIDLINE_COLOR);
        plot.setRangeGridlinePaint(GRIDLINE_COLOR);
    }
    
    /**
     * Assembles all the needed <code>TimeSeries</code>.
     */
    protected TimeSeriesCollection getDataset(Iteration iteration) {
        TimeSeriesCollection chartDataset = new TimeSeriesCollection();
        
        List<IterationHistoryEntry> iterationEntries = iterationHistoryEntryBusiness
                .getHistoryEntriesForIteration(iteration);
        
        LocalDate yesterday = new LocalDate().minusDays(1);
        LocalDate today = new LocalDate();
        IterationHistoryEntry yesterdayEntry = getHistoryEntryForDate(iterationEntries, yesterday);
        IterationHistoryEntry todayEntry = getHistoryEntryForDate(iterationEntries, today);
        DateTime iterationStartDate = new DateTime(iteration.getStartDate());
        DateTime iterationEndDate = new DateTime(iteration.getEndDate());
        
        chartDataset.addSeries(getBurndownTimeSeries(iterationEntries,
                new LocalDate(iteration.getStartDate()),
                determineEndDate(new LocalDate(iteration.getEndDate()))));

        chartDataset.addSeries(getCurrentDayTimeSeries(yesterdayEntry, todayEntry));
        
        chartDataset.addSeries(getScopingTimeSeries(iterationEntries,
                iterationStartDate.toLocalDate(),
                iterationEndDate.toLocalDate()));
        
        chartDataset.addSeries(getReferenceVelocityTimeSeries(
                iterationStartDate, iterationEndDate,
                new ExactEstimate(todayEntry.getOriginalEstimateSum())));
        
        TimeSeries predictedVelocity = getPredictedVelocityTimeSeries(iterationStartDate.toLocalDate(), iterationEndDate.toLocalDate(), yesterdayEntry, todayEntry);
        if (predictedVelocity != null) {
            chartDataset.addSeries(predictedVelocity);
        }
        
        return chartDataset;
    }
    
    protected LocalDate determineEndDate(LocalDate iterationEndDate) {
        LocalDate currentDate = new LocalDate();
        if (currentDate.compareTo(iterationEndDate) <= 0) {
            return currentDate; 
        }
        return iterationEndDate.plusDays(1);
    }
    
    protected IterationHistoryEntry getHistoryEntryForDate(List<IterationHistoryEntry> entries, LocalDate date) {
        IterationHistoryEntry foundEntry = new IterationHistoryEntry();
        for ( IterationHistoryEntry entry : entries ) {
            if (entry.getTimestamp().equals(date)) {
                return entry;
            }
            if (entry.getTimestamp().compareTo(date) > 0) {
                break;
            }
            foundEntry = entry;
        }
        IterationHistoryEntry returnable = new IterationHistoryEntry();
        returnable.setTimestamp(date.toDateMidnight().toLocalDate());
        returnable.setEffortLeftSum(foundEntry.getEffortLeftSum());
        returnable.setOriginalEstimateSum(foundEntry.getOriginalEstimateSum());
        return returnable;
    }
    
    protected List<IterationHistoryEntry> getIterationHistoryEntries(Iteration iteration) {
        return iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration);
    }

    protected ExactEstimate getTodaysStartValueWithScoping(
            IterationHistoryEntry yesterdayEntry,
            IterationHistoryEntry todayEntry) {
        
        long minorUnits = yesterdayEntry.getEffortLeftSum();
        minorUnits += todayEntry.getDeltaOriginalEstimate();
        
        return new ExactEstimate(minorUnits);
    }
    
    /**
     * Constructs the <code>TimeSeries</code> for the reference velocity.
     * <p>
     * Start point is at (startDate, originalEstimateSum).
     * End point is at (endDate + 1, 0.0)
     */
    protected TimeSeries getReferenceVelocityTimeSeries(DateTime startDate,
            DateTime endDate, ExactEstimate originalEstimateSum) {
        return this.getSeriesByStartAndEndPoints(REFERENCE_SERIES_NAME,
                startDate, originalEstimateSum, endDate.plusDays(1), new ExactEstimate(0));
    }
    
    protected TimeSeries getPredictedVelocityTimeSeries(LocalDate iterationStart, LocalDate iterationEnd, IterationHistoryEntry yesterdayEntry, IterationHistoryEntry todayEntry) {
        LocalDate today = new LocalDate();
        ExactEstimate startValue = getTodaysStartValueWithScoping(yesterdayEntry, todayEntry);
        ExactEstimate velocity = iterationBusiness.calculateDailyVelocity(iterationStart, yesterdayEntry);
        LocalDate startDate = (iterationEnd.isBefore(today)) ? iterationEnd : today;
        LocalDate endDate = iterationHistoryEntryBusiness.calculateExpectedEffortDoneDate(startDate, startValue, velocity);
        if (endDate == null) return null;
        return this.getSeriesByStartAndEndPoints(EXPECTED_SERIES_NAME, today.toDateTimeAtStartOfDay(), startValue, endDate.toDateTimeAtStartOfDay(), ExactEstimate.ZERO);
    }
    
    /**
     * Get the <code>TimeSeries</code> for drawing the current day line.
     */
    protected TimeSeries getCurrentDayTimeSeries(IterationHistoryEntry yesterdayEntry, IterationHistoryEntry todayEntry) {
        ExactEstimate startValue = this.getTodaysStartValueWithScoping(yesterdayEntry, todayEntry);

        ExactEstimate endValue = new ExactEstimate(todayEntry.getEffortLeftSum());
        
        return this.getSeriesByStartAndEndPoints(CURRENT_DAY_SERIES_NAME,
                todayEntry.getTimestamp().toDateMidnight().toDateTime(), startValue,
                todayEntry.getTimestamp().toDateMidnight().toDateTime().plusDays(1), endValue);
    }
    
    /**
     * Gets the history entry for each day and transforms it to a
     * <code>JFreeChart</code> entry.
     */
    protected TimeSeries getBurndownTimeSeries(List<IterationHistoryEntry> iterationHistoryEntries, LocalDate startDate, LocalDate endDate) {
        TimeSeries burndownSeries = new TimeSeries(BURNDOWN_SERIES_NAME);
        
        for (LocalDate iter = startDate.minusDays(1); iter.compareTo(endDate) < 0; iter = iter.plusDays(1)) {
            IterationHistoryEntry todayEntry = getHistoryEntryForDate(iterationHistoryEntries, iter);
            IterationHistoryEntry yesterdayEntry = getHistoryEntryForDate(iterationHistoryEntries, iter.minusDays(1));
            
            if (isScopingDone(todayEntry)) {
                Pair<TimeSeriesDataItem, TimeSeriesDataItem> scopedEntries
                    = getBurndownScopedDataItemForDay(yesterdayEntry, todayEntry);
                burndownSeries.add(scopedEntries.getFirst());
                burndownSeries.add(scopedEntries.getSecond());
            }
            
            burndownSeries.add(getBurndownDataItemForDay(todayEntry));
        } 
        
        return burndownSeries;
    }
    
    protected TimeSeriesDataItem getBurndownDataItemForDay(IterationHistoryEntry entry) {
        TimeSeriesDataItem item = new TimeSeriesDataItem(new Second(entry.getTimestamp()
                .toDateMidnight().plusDays(1).toDate()), ExactEstimateUtils
                .extractMajorUnits(new ExactEstimate(entry.getEffortLeftSum())));
        return item;
        
    }
    
    protected Pair<TimeSeriesDataItem, TimeSeriesDataItem> getBurndownScopedDataItemForDay(IterationHistoryEntry yesterdayEntry, IterationHistoryEntry todayEntry) {
        DateTime timestamp = todayEntry.getTimestamp().toDateMidnight().toDateTime().plusSeconds(2);
        Second period = new Second(timestamp.toDate());
        
        long longValue = yesterdayEntry.getEffortLeftSum() + todayEntry.getDeltaOriginalEstimate();
        ExactEstimate scopedValue = new ExactEstimate(longValue);
        
        TimeSeriesDataItem nullItem = new TimeSeriesDataItem(new Second(timestamp.minusSeconds(1).toDate()), null);
        TimeSeriesDataItem scopedItem = new TimeSeriesDataItem(period, ExactEstimateUtils.extractMajorUnits(scopedValue));
        
        return Pair.create(nullItem, scopedItem);
    }
    
    
    protected List<TimeSeriesDataItem> getScopeSeriesDataItems(
            IterationHistoryEntry yesterdayEntry,
            IterationHistoryEntry todayEntry) {
        
        // Second item is places 2 seconds after the first
        // Resulting in a almost vertical line in the graph
        // Null value is added to break the line
        Second firstItemPeriod = new Second(todayEntry.getTimestamp().toDateMidnight().toDate());
        Second secondItemPeriod = new Second(todayEntry.getTimestamp().toDateMidnight().toDateTime().plusSeconds(2).toDate());
        Second nullItemPeriod = new Second(todayEntry.getTimestamp().toDateMidnight().toDateTime().plusSeconds(3).toDate());
        
        ExactEstimate firstValue = new ExactEstimate(yesterdayEntry.getEffortLeftSum());
        long secondValueAsLong = yesterdayEntry.getEffortLeftSum() + todayEntry.getDeltaOriginalEstimate();
        ExactEstimate secondValue = new ExactEstimate(secondValueAsLong);
        
        TimeSeriesDataItem firstItem = new TimeSeriesDataItem(firstItemPeriod, ExactEstimateUtils.extractMajorUnits(firstValue));
        TimeSeriesDataItem secondItem = new TimeSeriesDataItem(secondItemPeriod, ExactEstimateUtils.extractMajorUnits(secondValue));
        TimeSeriesDataItem nullItem = new TimeSeriesDataItem(nullItemPeriod, null);
        
        return Arrays.asList(firstItem, secondItem, nullItem);
    }
    
    protected boolean isScopingDone(IterationHistoryEntry entry) {
        return (entry.getDeltaOriginalEstimate() != 0);
    }
    
    
    protected TimeSeries getScopingTimeSeries(List<IterationHistoryEntry> iterationHistoryEntries, LocalDate startDate, LocalDate endDate) {
        TimeSeries scopingSeries = new TimeSeries(SCOPING_SERIES_NAME);
        for (LocalDate iter = startDate.minusDays(1); iter.compareTo(endDate.plusDays(1)) < 0; iter = iter.plusDays(1)) {
            IterationHistoryEntry todayEntry = getHistoryEntryForDate(iterationHistoryEntries, iter);
            IterationHistoryEntry yesterdayEntry = getHistoryEntryForDate(iterationHistoryEntries, iter.minusDays(1));
            
            if (isScopingDone(todayEntry)) {
                List<TimeSeriesDataItem> scopeItems
                    = getScopeSeriesDataItems(yesterdayEntry, todayEntry);
                scopingSeries.add(scopeItems.get(0));
                scopingSeries.add(scopeItems.get(1));
                scopingSeries.add(scopeItems.get(2));
            }
        }
        return scopingSeries;
    }
    
    
    protected TimeSeries getSeriesByStartAndEndPoints(String seriesKey,
            DateTime startInstant, ExactEstimate startValue,
            DateTime endInstant, ExactEstimate endValue) {
        TimeSeries timeSeries = new TimeSeries(seriesKey);
        
        timeSeries.add(exactEstimateToDataItem(startInstant.toDateMidnight().toDateTime(), startValue));
        timeSeries.add(exactEstimateToDataItem(endInstant.toDateMidnight().toDateTime(), endValue));
        
        return timeSeries;
    }
    
    /**
     * Transforms an <code>ExactEstimate</code> to a JFree data item.
     */
    protected TimeSeriesDataItem exactEstimateToDataItem(DateTime instant, ExactEstimate value) {
        Second startInstant = new Second(instant.toDate());
        return new TimeSeriesDataItem(startInstant, ExactEstimateUtils
                .extractMajorUnits(value));
    }

}
