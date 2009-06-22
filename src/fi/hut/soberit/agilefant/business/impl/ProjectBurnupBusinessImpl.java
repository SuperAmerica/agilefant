package fi.hut.soberit.agilefant.business.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBurnupBusiness;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.Pair;

@Service("projectBurnupBusiness")
public class ProjectBurnupBusinessImpl implements ProjectBurnupBusiness {

    @Autowired
    private BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    
    /* Chart sizes */
    protected static final int DEFAULT_WIDTH = 780;
    protected static final int DEFAULT_HEIGHT = 600;
    protected static final int SMALL_WIDTH = 110;
    protected static final int SMALL_HEIGHT = 85;
    protected static final int DEFAULT_DATAPOINTS = 20;

    /* Chart backgrounds */
    protected static final Color CHART_BACKGROUND_COLOR = Color.white;
    protected static final Color PLOT_BACKGROUND_COLOR = Color.white;
    protected static final Color GRIDLINE_COLOR = new Color(0xcc, 0xcc, 0xcc);

    /* Series colors */
    protected static final Color BURNUP_COLOR = new Color(220, 100, 87);
    protected static final Color DONE_COLOR = new Color(0, 187, 68);

    /* Axis titles */
    protected static final String DATE_AXIS_LABEL = "Date";
    protected static final String STORYPOINTS_AXIS_LABEL = "Story points";

    /* Names */
    protected static final String CHART_NAME = "Project burnup";
    protected static final String BURNUP_NAME = "Work planned";
    protected static final String DONE_NAME = "Work done";

    /* Series shape */
    protected static final boolean BURNUP_SERIES_SHAPE_VISIBLE = false;
    protected static final Shape DONE_SERIES_SHAPE = new Rectangle(-2, -2, 4, 4);
    protected static final boolean DONE_SERIES_SHAPE_VISIBLE = true;

    /**
     * Generates a byte array (a png image file) from a JFreeChart object
     * 
     * @param chart
     *            A chart object from which the image is created
     * @param width
     *            Width of the created image
     * @param height
     *            Height of the created image
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

    protected byte[] getChartImageByteArray(JFreeChart chart) {
        return getChartImageByteArray(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Transactional(readOnly = true)
    public byte[] getBurnup(Project project) {
        return getChartImageByteArray(constructChart(project));
    }

    /**
     * Assembles all the needed <code>TimeSeries</code>.
     */
    protected TimeSeriesCollection getBurnupDataset(
            List<Pair<DateTime, BacklogHistoryEntry>> entries) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        TimeSeries series = new TimeSeries(BURNUP_NAME);

        for (Pair<DateTime, BacklogHistoryEntry> entry : entries) {
            Second second = new Second(entry.first.toDate());
            TimeSeriesDataItem item = new TimeSeriesDataItem(second,
                    entry.second.getEstimateSum());
            series.add(item);
        }

        dataset.addSeries(series);
        return dataset;
    }

    protected TimeSeriesCollection getDoneDataset(
            List<Pair<DateTime, BacklogHistoryEntry>> entries) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        TimeSeries series = new TimeSeries(DONE_NAME);

        for (Pair<DateTime, BacklogHistoryEntry> entry : entries) {
            Second second = new Second(entry.first.toDate());
            TimeSeriesDataItem item = new TimeSeriesDataItem(second,
                    entry.second.getDoneSum());
            series.add(item);
        }

        dataset.addSeries(series);
        return dataset;
    }

    protected JFreeChart constructChart(Project project) {
        DateMidnight start = new DateTime(project.getStartDate())
                .toDateMidnight();

        DateTime end = new DateTime(project.getEndDate()).withTime(23, 59, 00, 00);

        long millis = end.getMillis() - start.getMillis();

        double offset = millis / DEFAULT_DATAPOINTS;

        List<Pair<DateTime, BacklogHistoryEntry>> entries = new ArrayList<Pair<DateTime, BacklogHistoryEntry>>(
                DEFAULT_DATAPOINTS);

        List<DateTime> timestamps = new ArrayList<DateTime>(DEFAULT_DATAPOINTS + 1);
        for (int i = 0; i < DEFAULT_DATAPOINTS; i++) {
            DateTime stamp = start.toDateTime().plusMillis((int) (i * offset));
            
            timestamps.add(stamp);
        }

        //timestamps.add(end.toDateTime());
        timestamps.add(end.plusDays(1).toDateTime());
        
        List<BacklogHistoryEntry> rawEntries = backlogHistoryEntryBusiness.retrieveForTimestamps(timestamps, project.getId());
        
        for (int i = 0; i < rawEntries.size(); i++) {
            entries.add(Pair.create(timestamps.get(i), rawEntries.get(i)));
        }

        JFreeChart burnup = ChartFactory.createXYStepChart(CHART_NAME,
                DATE_AXIS_LABEL, STORYPOINTS_AXIS_LABEL, null,
                PlotOrientation.VERTICAL, true, true, false);

        XYPlot plot = burnup.getXYPlot();
        plot.setRenderer(0, getBurnupRenderer());
        plot.setDataset(0, getBurnupDataset(entries));
        plot.setRenderer(1, getDoneRenderer());
        plot.setDataset(1, getDoneDataset(entries));

        formatChartAxes(burnup, new DateTime(project.getStartDate()),
                new DateTime(project.getEndDate()));

        formatChartStyle(burnup);

        return burnup;
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
    protected void formatChartAxes(JFreeChart chart, DateTime start,
            DateTime end) {
        XYPlot plot = chart.getXYPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setMaximumDate(end.toDateMidnight().plusDays(1).toDate());
        axis.setMinimumDate(start.toDateMidnight().toDate());
        axis.setDateFormatOverride(new SimpleDateFormat("EEE d.M."));
        plot.setDomainGridlinePaint(GRIDLINE_COLOR);
        plot.setRangeGridlinePaint(GRIDLINE_COLOR);
    }

    protected XYAreaRenderer getDoneRenderer() {
        XYAreaRenderer result = new XYAreaRenderer();
        result.setSeriesShape(0, new Rectangle(-2, -2, 4, 4));
        result.setSeriesPaint(0, new Color(0, 187, 0, 100));
        return result;
    }

    protected XYStepRenderer getBurnupRenderer() {
        XYStepRenderer result = new XYStepRenderer();
        result.setSeriesPaint(0, new Color(220, 100, 87));
        Stroke stroke = new BasicStroke(1.5f);
        result.setSeriesStroke(0, stroke);
        result.setSeriesShape(0, null);
        return result;
    }

}
