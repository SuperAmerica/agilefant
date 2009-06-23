package fi.hut.soberit.agilefant.business.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

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
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.ProjectBurnupBusiness;
import fi.hut.soberit.agilefant.db.BacklogHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.Pair;
import fi.hut.soberit.agilefant.util.ProjectBurnupData;

@Service("projectBurnupBusiness")
public class ProjectBurnupBusinessImpl implements ProjectBurnupBusiness {

    @Autowired
    private BacklogHistoryEntryDAO backlogHistoryEntryDAO;

    /* Chart sizes */
    protected static final int DEFAULT_WIDTH = 780;
    protected static final int DEFAULT_HEIGHT = 600;
    protected static final int SMALL_WIDTH = 110;
    protected static final int SMALL_HEIGHT = 85;

    /* Chart backgrounds */
    protected static final Color CHART_BACKGROUND_COLOR = Color.white;
    protected static final Color PLOT_BACKGROUND_COLOR = Color.white;
    protected static final Color GRIDLINE_COLOR = new Color(0xcc, 0xcc, 0xcc);

    /* Series colors */
    protected static final Color PLANNED_COLOR = new Color(220, 100, 87);
    protected static final Color DONE_COLOR = new Color(0, 187, 68);

    /* Axis titles */
    protected static final String DATE_AXIS_LABEL = "Date";
    protected static final String STORYPOINTS_AXIS_LABEL = "Story points";

    /* Names */
    protected static final String CHART_NAME = "Project burnup";
    protected static final String PLANNED_NAME = "Work planned";
    protected static final String DONE_NAME = "Work done";

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

    protected JFreeChart constructChart(Project project) {
        ProjectBurnupData data = backlogHistoryEntryDAO
                .retrieveBurnupData(project.getId());

        JFreeChart burnup = ChartFactory.createXYStepChart(CHART_NAME,
                DATE_AXIS_LABEL, STORYPOINTS_AXIS_LABEL, null,
                PlotOrientation.VERTICAL, true, true, false);

        Pair<TimeSeriesCollection, TimeSeriesCollection> datasets = convertToDatasets(data);

        XYPlot plot = burnup.getXYPlot();
        plot.setRenderer(0, getPlannedRenderer());
        plot.setDataset(0, datasets.first);
        plot.setRenderer(1, getDoneRenderer());
        plot.setDataset(1, datasets.second);

        formatChartAxes(burnup, new DateTime(project.getStartDate()),
                new DateTime(project.getEndDate()));

        formatChartStyle(burnup);

        return burnup;
    }

    protected Pair<TimeSeriesCollection, TimeSeriesCollection> convertToDatasets(
            ProjectBurnupData data) {
        TimeSeries planned = new TimeSeries(PLANNED_NAME);
        TimeSeries done = new TimeSeries(DONE_NAME);
        DateTime now = new DateTime();

        ProjectBurnupData.Entry lastEntry = null;
        ProjectBurnupData.Entry lastDoneEntry = null;
        for (ProjectBurnupData.Entry entry : data) {
            if (entry.timestamp.isAfter(now)) {
                break;
            }
            Second second = new Second(entry.timestamp.toDate());
            if (lastDoneEntry == null || entry.doneSum != lastDoneEntry.doneSum) {
                TimeSeriesDataItem item = new TimeSeriesDataItem(second,
                        entry.doneSum);
                done.add(item);
                lastDoneEntry = entry;
            }
            TimeSeriesDataItem item = new TimeSeriesDataItem(second,
                    entry.estimateSum);
            planned.add(item);
            lastEntry = entry;
        }

        Second nowSecond = new Second(now.toDate());
        if (lastEntry != null && !lastEntry.timestamp.isEqual(now)) {
            TimeSeriesDataItem nowItem = new TimeSeriesDataItem(nowSecond,
                    lastEntry.estimateSum);
            planned.add(nowItem);
        }
        if (lastDoneEntry != null && !lastDoneEntry.timestamp.isEqual(now)) {
            TimeSeriesDataItem nowItem = new TimeSeriesDataItem(nowSecond,
                    lastEntry.doneSum);
            done.add(nowItem);
        }

        return Pair.create(new TimeSeriesCollection(planned),
                new TimeSeriesCollection(done));
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

    protected XYStepRenderer getPlannedRenderer() {
        XYStepRenderer result = new XYStepRenderer();
        result.setSeriesPaint(0, new Color(220, 100, 87));
        Stroke stroke = new BasicStroke(1.5f);
        result.setSeriesStroke(0, stroke);
        result.setSeriesShape(0, null);
        return result;
    }

}
