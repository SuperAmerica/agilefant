package fi.hut.soberit.agilefant.business.impl;


import java.awt.Color;
import java.awt.GradientPaint;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationBurndownBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.util.ExactEstimateUtils;

/**
 * A business class for calculating the burndown for iterations.
 * <p>
 * All methods are marked initially as read-only transactions.
 * Override with <code>@Transactional</code>.
 * 
 * @author rjokelai, jsorvett
 *
 */
@Service("iterationBurndownBusiness")
@Transactional(readOnly = true)
public class IterationBurndownBusinessImpl implements IterationBurndownBusiness {

    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;    
    
    /* Chart sizes */
    protected static final int DEFAULT_WIDTH = 780;
    protected static final int DEFAULT_HEIGHT = 600;
    
    
    
    /* Series colors */
    protected static Color BURNDOWN_COLOR = new Color(220, 100, 87);
    protected static Color REFERENCE_COLOR = new Color(90, 145, 210);
    protected static Color EXPECTED_COLOR = new Color(80, 80, 80);
    
    /* Chart backgrounds */
    protected static Color CHART_BACKGROUND_COLOR = Color.white;
    protected static Color PLOT_BACKGROUND_COLOR = new Color(0xee, 0xee, 0xee);

    /* Chart titles */
    protected static final String BURNDOWN_NAME = "Iteration burndown";
    protected static final String DATE_AXIS_LABEL = "Date";
    protected static final String EFFORT_AXIS_LABEL = "Effort left";
    
    /* Series names */
    protected static final String REFERENCE_SERIES_NAME = "Reference velocity"; 

    @Autowired
    public void setIterationHistoryEntryBusiness(
            IterationHistoryEntryBusiness iterationHistoryEntryBusiness) {
        this.iterationHistoryEntryBusiness = iterationHistoryEntryBusiness;
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
    
    protected JFreeChart constructChart(Iteration iteration) {
        JFreeChart burndown = ChartFactory.createTimeSeriesChart(BURNDOWN_NAME,
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
    
    /**
     * Sets the chart's and plot's background colors.
     */
    protected void formatChartStyle(JFreeChart chart) {
        chart.setBackgroundPaint(CHART_BACKGROUND_COLOR);
        chart.getPlot().setBackgroundPaint(PLOT_BACKGROUND_COLOR);
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
        axis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
    }
    
    /**
     * Assembles all the needed <code>TimeSeries</code>.
     */
    protected TimeSeriesCollection getDataset(Iteration iteration) {
        TimeSeriesCollection chartDataset = new TimeSeriesCollection();
        
        chartDataset.addSeries(getReferenceVelocityTimeSeries(
                new DateTime(iteration.getStartDate()),
                new DateTime(iteration.getEndDate()),
                iterationHistoryEntryBusiness.getLatestOriginalEstimateSum(iteration)));
        
        return chartDataset;
    }

    /**
     * Constructs the <code>TimeSeries</code> for the reference velocity.
     * <p>
     * Start point is at (startDate, originalEstimateSum).
     * End point is at (endDate + 1, 0.0)
     */
    protected TimeSeries getReferenceVelocityTimeSeries(DateTime startDate,
            DateTime endDate, ExactEstimate originalEstimateSum) {
        TimeSeries referenceSeries = new TimeSeries(REFERENCE_SERIES_NAME);
        
        referenceSeries.add(this.exactEstimateToDataItem(startDate
                .toDateMidnight().toDateTime(), originalEstimateSum));
        referenceSeries.add(this.exactEstimateToDataItem(endDate.plusDays(1)
                .toDateMidnight().toDateTime(), new ExactEstimate(0)));
        
        return referenceSeries;
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
