package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.impl.IterationBurndownBusinessImpl;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.util.ExactEstimateUtils;
import fi.hut.soberit.agilefant.util.Pair;

/**
 * Test class for <code>IterationBurndownBusiness</code>.
 * <p>
 * Extends the class to be able to test protected methods.
 * 
 * @author rjokelai, jsorvett
 *
 */
public class IterationBurndownBusinessTest extends IterationBurndownBusinessImpl {

    IterationBurndownBusinessImpl iterationBurndownBusiness;
    IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    
    Iteration iteration;
    DateTime startDate;
    DateTime endDate;
    ExactEstimate originalEstimateSum;
    IterationHistoryEntry entry;
    JFreeChart chart;
    
    IterationHistoryEntry entry1;
    IterationHistoryEntry entry2;
    List<IterationHistoryEntry> entriesList;
    
    IterationHistoryEntry startEntry;
    IterationHistoryEntry endEntry;
       
    @Before
    public void setUp() {
        iterationBurndownBusiness = new IterationBurndownBusinessImpl();
        iterationHistoryEntryBusiness = createMock(IterationHistoryEntryBusiness.class);
        iterationBurndownBusiness
                .setIterationHistoryEntryBusiness(iterationHistoryEntryBusiness);
        super.setIterationHistoryEntryBusiness(iterationHistoryEntryBusiness);
        
        startDate = new DateTime(2009,1,1,0,0,0,0);
        endDate = new DateTime(2009,1,10,0,0,0,0);
        iteration = new Iteration();
        iteration.setId(123);
        iteration.setStartDate(startDate.toDate());
        iteration.setEndDate(endDate.toDate());
        originalEstimateSum = new ExactEstimate(100 * 60);
        
        chart = ChartFactory.createTimeSeriesChart(BURNDOWN_SERIES_NAME,
                DATE_AXIS_LABEL,
                EFFORT_AXIS_LABEL,
                null, true, true, false);
        
        entry = new IterationHistoryEntry();
        entry.setTimestamp(new DateTime(2009,1,1,0,0,0,0));
        entry.setEffortLeftSum(240);
        
        
        entry1 = new IterationHistoryEntry();
        entry1.setTimestamp(startDate);
        entry1.setEffortLeftSum(100);
        entry1.setOriginalEstimateSum(100);
        entry2 = new IterationHistoryEntry();
        entry2.setTimestamp(startDate.plusDays(1));
        entry2.setEffortLeftSum(0);
        entry2.setOriginalEstimateSum(100);
        
        entriesList = Arrays.asList(entry1, entry2);
    }
    
    @Test
    public void testGetIterationBurndown() {
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);
        
        assertNotNull(iterationBurndownBusiness.getIterationBurndown(iteration));
        
        verify(iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testGetSmallIterationBurndown() {
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
        .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);

        assertNotNull(iterationBurndownBusiness.getSmallIterationBurndown(iteration));

        verify(iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testConstructChart() {
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);
        
        JFreeChart newChart = super.constructChart(iteration);
        
        assertEquals(REFERENCE_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(REFERENCE_SERIES_NO));
        assertEquals(BURNDOWN_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(BURNDOWN_SERIES_NO));
        assertEquals(CURRENT_DAY_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(CURRENT_DAY_SERIES_NO));
        assertEquals(SCOPING_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(SCOPING_SERIES_NO));
        
        verify(iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testConstructSmallChart() {
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);
        
        JFreeChart newChart = super.constructSmallChart(iteration);
        
        assertEquals(REFERENCE_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(REFERENCE_SERIES_NO));
        assertEquals(BURNDOWN_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(BURNDOWN_SERIES_NO));
        assertEquals(CURRENT_DAY_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(CURRENT_DAY_SERIES_NO));
        assertEquals(SCOPING_SERIES_NAME,
                newChart.getXYPlot().getDataset().getSeriesKey(SCOPING_SERIES_NO));
               
        testSmallChartFormating(newChart);
        
        verify(iterationHistoryEntryBusiness);
    }
    
    private void testSmallChartFormating(JFreeChart chart) {
        assertEquals(PLOT_BACKGROUND_COLOR, chart.getPlot().getBackgroundPaint());
       
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer rend = (XYLineAndShapeRenderer)chart.getXYPlot().getRenderer();
        assertEquals(BURNDOWN_SERIES_COLOR, rend.getSeriesPaint(BURNDOWN_SERIES_NO));
        assertEquals(BURNDOWN_SERIES_COLOR, rend.getSeriesPaint(SCOPING_SERIES_NO));
        assertEquals(BURNDOWN_SERIES_COLOR, rend.getSeriesPaint(CURRENT_DAY_SERIES_NO));
        assertEquals(REFERENCE_SERIES_COLOR, rend.getSeriesPaint(REFERENCE_SERIES_NO));
        
        assertFalse(plot.getDomainAxis().isVisible());
        assertFalse(plot.getRangeAxis().isVisible());
        assertFalse(plot.isDomainGridlinesVisible());
        assertFalse(plot.isRangeGridlinesVisible());
        
        assertFalse(rend.getSeriesShapesVisible(BURNDOWN_SERIES_NO));
        assertFalse(rend.getSeriesShapesVisible(REFERENCE_SERIES_NO));
        assertFalse(rend.getSeriesShapesVisible(SCOPING_SERIES_NO));
        assertFalse(rend.getSeriesShapesVisible(CURRENT_DAY_SERIES_NO));
        
        assertEquals(null, plot.getDomainAxis().getLabel());
        assertEquals(null, plot.getRangeAxis().getLabel());
        
        assertNull(chart.getLegend());
    }
    
    @Test
    public void testTransformToSmallChart() {
        JFreeChart newChart = super.transformToSmallChart(chart);
        testSmallChartFormating(newChart);
    }
    
    @Test
    public void testFormatChartAxes() {
        super.formatChartAxes(chart, startDate, endDate);
        
        DateAxis actualAxis = (DateAxis)chart.getXYPlot().getDomainAxis();
        
        assertEquals("Start date not same", startDate.toDateMidnight().toDate(), actualAxis.getMinimumDate());
        assertEquals("End date not same", endDate.plusDays(1).toDateMidnight().toDate(), actualAxis.getMaximumDate());
        
        assertEquals(GRIDLINE_COLOR, chart.getXYPlot().getRangeGridlinePaint());
        assertEquals(GRIDLINE_COLOR, chart.getXYPlot().getDomainGridlinePaint());
    } 
    
    @Test
    public void testFormatChartStyle() {
        super.formatChartStyle(chart);
        assertEquals(CHART_BACKGROUND_COLOR, chart.getBackgroundPaint());
        assertEquals(PLOT_BACKGROUND_COLOR, chart.getPlot().getBackgroundPaint());
        
        testSetSeriesStyles();
    }
    
    private void testSetSeriesStyles() {
        XYLineAndShapeRenderer rend = (XYLineAndShapeRenderer) ((XYPlot) chart
                .getPlot()).getRenderer();
        
        assertEquals(BURNDOWN_SERIES_COLOR, rend.getSeriesPaint(BURNDOWN_SERIES_NO));
        assertEquals(BURNDOWN_SERIES_SHAPE, rend.getSeriesShape(BURNDOWN_SERIES_NO));
        assertEquals(BURNDOWN_SERIES_SHAPE_VISIBLE, rend.getSeriesShapesVisible(BURNDOWN_SERIES_NO));
        
        assertEquals(REFERENCE_SERIES_COLOR, rend.getSeriesPaint(REFERENCE_SERIES_NO));
        
        assertEquals(CURRENT_DAY_SERIES_COLOR, rend.getSeriesPaint(CURRENT_DAY_SERIES_NO));
        assertEquals(CURRENT_DAY_SERIES_STROKE, rend.getSeriesStroke(CURRENT_DAY_SERIES_NO));
        assertEquals(CURRENT_DAY_SERIES_SHAPE, rend.getSeriesShape(CURRENT_DAY_SERIES_NO));
        assertEquals(CURRENT_DAY_SERIES_SHAPE_VISIBLE, rend.getSeriesShapesVisible(CURRENT_DAY_SERIES_NO));
        assertEquals(CURRENT_DAY_SERIES_SHAPE_FILLED, rend.getSeriesShapesFilled(CURRENT_DAY_SERIES_NO));
        
        assertEquals(SCOPING_SERIES_STROKE, rend.getSeriesStroke(SCOPING_SERIES_NO));
        assertEquals(SCOPING_SERIES_COLOR, rend.getSeriesPaint(SCOPING_SERIES_NO));
    }
    
    @Test
    public void testGetDataset() {
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);
        
        TimeSeriesCollection actualTimeSeries = super.getDataset(iteration);
        assertNotNull(actualTimeSeries.getSeries(REFERENCE_SERIES_NAME));
        assertNotNull(actualTimeSeries.getSeries(BURNDOWN_SERIES_NAME));
        assertNotNull(actualTimeSeries.getSeries(CURRENT_DAY_SERIES_NAME));
        assertNotNull(actualTimeSeries.getSeries(SCOPING_SERIES_NAME));
        
        verify(iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testDetermineEndDate_currentDateBeforeEndDate() {
        assertEquals(new LocalDate(), super.determineEndDate(new LocalDate().plusDays(3)));
    }
    
    @Test
    public void testDetermineEndDate_currentDateEqualsEndDate() {
        assertEquals(new LocalDate(), super.determineEndDate(new LocalDate().plusDays(3)));
    }
    
    @Test
    public void testDetermineEndDate_currentDateAfterEndDate() {
        assertEquals(new LocalDate().minusDays(2), super.determineEndDate(new LocalDate().minusDays(3)));
    }
    
    @Test
    public void testGetHistoryEntryForDate_dateFound() {
        assertEquals(entry1, super.getHistoryEntryForDate(entriesList, entry1
                .getTimestamp().toLocalDate()));
    }
    
    @Test
    public void testGetHistoryEntryForDate_entryFromYesterday() {
        entry2.setDeltaEffortLeft(125);
        entry2.setDeltaOriginalEstimate(228);
        IterationHistoryEntry actualEntry = super.getHistoryEntryForDate(
                entriesList, entry2.getTimestamp().plusDays(1).toLocalDate());
        
        assertEquals(entry2.getOriginalEstimateSum(), actualEntry.getOriginalEstimateSum());
        assertEquals(entry2.getEffortLeftSum(), actualEntry.getEffortLeftSum());
        assertEquals(0l, actualEntry.getDeltaEffortLeft());
        assertEquals(0l, actualEntry.getDeltaOriginalEstimate());
    }
    
    @Test
    public void testGetHistoryEntryForDate_entryFromFarBehind() {
        entry2.setDeltaEffortLeft(125);
        entry2.setDeltaOriginalEstimate(228);
        IterationHistoryEntry actualEntry = super.getHistoryEntryForDate(
                entriesList, entry2.getTimestamp().plusDays(102).toLocalDate());
        
        assertEquals(entry2.getOriginalEstimateSum(), actualEntry.getOriginalEstimateSum());
        assertEquals(entry2.getEffortLeftSum(), actualEntry.getEffortLeftSum());
        assertEquals(0l, actualEntry.getDeltaEffortLeft());
        assertEquals(0l, actualEntry.getDeltaOriginalEstimate());
    }
    
    @Test
    public void testGetHistoryEntryForDate_endDateBetweenEntries() {
        IterationHistoryEntry forecomingEntry = new IterationHistoryEntry();
        forecomingEntry.setTimestamp(new DateTime().plusDays(66));
        
        List<IterationHistoryEntry> newEntries = new ArrayList<IterationHistoryEntry>();
        newEntries.addAll(entriesList);
        newEntries.add(forecomingEntry);
        
        entry2.setDeltaEffortLeft(125);
        entry2.setDeltaOriginalEstimate(228);
        IterationHistoryEntry actualEntry = super.getHistoryEntryForDate(
                entriesList, entry2.getTimestamp().plusDays(16).toLocalDate());
        
        assertEquals(entry2.getOriginalEstimateSum(), actualEntry.getOriginalEstimateSum());
        assertEquals(entry2.getEffortLeftSum(), actualEntry.getEffortLeftSum());
        assertEquals(0l, actualEntry.getDeltaEffortLeft());
        assertEquals(0l, actualEntry.getDeltaOriginalEstimate());
    }
    
    @Test
    public void testGetHistoryEntryForDate_emptyEntry() {
        List<IterationHistoryEntry> emptyList = new ArrayList<IterationHistoryEntry>();
        IterationHistoryEntry actualEntry = super.getHistoryEntryForDate(emptyList, startDate.toLocalDate());
        assertEquals(0l , actualEntry.getEffortLeftSum());
        assertEquals(0l , actualEntry.getOriginalEstimateSum());
        assertEquals(0l , actualEntry.getDeltaOriginalEstimate());
        assertEquals(0l , actualEntry.getDeltaEffortLeft());
    }
    
    @Test
    public void testGetIterationHistoryEntries() {
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(entriesList);
        replay(iterationHistoryEntryBusiness);
        
        assertSame(entriesList, super.getIterationHistoryEntries(iteration));
        
        verify(iterationHistoryEntryBusiness);
    }
    
    private void initializeEntriesForScopingTest() {
        startEntry = new IterationHistoryEntry();
        startEntry.setOriginalEstimateSum(120);
        startEntry.setEffortLeftSum(120);
        startEntry.setDeltaEffortLeft(0);
        startEntry.setDeltaOriginalEstimate(0);
        startEntry.setTimestamp(new DateTime(2009,1,1,14,38,25,0));
        
        endEntry = new IterationHistoryEntry();
        endEntry.setOriginalEstimateSum(120);
        endEntry.setEffortLeftSum(60);
        endEntry.setDeltaEffortLeft(-60);
        endEntry.setDeltaOriginalEstimate(0);
        endEntry.setTimestamp(new DateTime(2009,1,2,12,12,25,0));
    }
    
    @Test
    public void testGetTodaysStartValueWithScoping_noScope() {
        initializeEntriesForScopingTest();
        assertEquals(new Long(120),
                super.getTodaysStartValueWithScoping(startEntry, endEntry).getMinorUnits());
    }
    
    @Test
    public void testGetTodaysStartValueWithScoping_minusScope() {
        initializeEntriesForScopingTest();
        endEntry.setDeltaOriginalEstimate(-20);
        endEntry.setOriginalEstimateSum(100);
        assertEquals(new Long(100),
                super.getTodaysStartValueWithScoping(startEntry, endEntry).getMinorUnits());
    }
    
    @Test
    public void testGetTodaysStartValueWithScoping_plusScope() {
        initializeEntriesForScopingTest();
        endEntry.setDeltaOriginalEstimate(97);
        endEntry.setOriginalEstimateSum(217);
        assertEquals(new Long(217),
                super.getTodaysStartValueWithScoping(startEntry, endEntry).getMinorUnits());   
    }
    
    @Test
    public void testGetReferenceVelocityTimeSeries() {
        
        TimeSeriesDataItem startPoint = new TimeSeriesDataItem(new Second(
                startDate.toDate()), 100.0);
        TimeSeriesDataItem endPoint = new TimeSeriesDataItem(new Second(endDate
                .plusDays(1).toDate()), 0.0);
        
        TimeSeries actualSeries
            = super.getReferenceVelocityTimeSeries(startDate, endDate, originalEstimateSum);
        
        assertEquals("Reference series name incorrect",
                REFERENCE_SERIES_NAME, actualSeries.getKey());
        
        assertEquals("Reference start value not correct",
                startPoint.getValue(), actualSeries.getDataItem(0).getValue());
        assertEquals("Reference start instant not correct", startPoint
                .getPeriod(), actualSeries.getDataItem(0).getPeriod());
        assertEquals("Reference end value not correct", endPoint.getValue(),
                actualSeries.getDataItem(1).getValue());
        assertEquals("Reference end instant not correct", endPoint.getPeriod(),
                actualSeries.getDataItem(1).getPeriod());
        
    }
    
    @Test
    public void testGetBurndownTimeSeries() {
        replay(iterationHistoryEntryBusiness);
        
        TimeSeries actualSeries = super.getBurndownTimeSeries(entriesList,
                startDate.toLocalDate(), endDate.toLocalDate());
        
        Second startInstant = new Second(startDate.plusDays(1).toDateMidnight().toDate());
        Second secondInstant = new Second(startDate.plusDays(2).toDateMidnight().toDate());
        Second endInstant = new Second(endDate.toDateMidnight().toDate());
        Second afterEndInstant = new Second(endDate.plusDays(1).toDateMidnight().toDate());
        
        
        assertEquals(ExactEstimateUtils.extractMajorUnits(new ExactEstimate(entry1.getEffortLeftSum())),
                actualSeries.getDataItem(startInstant).getValue());
        assertEquals(ExactEstimateUtils.extractMajorUnits(new ExactEstimate(entry2.getEffortLeftSum())),
                actualSeries.getDataItem(secondInstant).getValue());
        assertEquals(ExactEstimateUtils.extractMajorUnits(new ExactEstimate(entry2.getEffortLeftSum())),
                actualSeries.getDataItem(endInstant).getValue());
        
        assertNull(actualSeries.getDataItem(afterEndInstant));
        
        verify(iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testGetBurndownTimeSeries_scoped() {
        DateTime startTime = new DateTime(2012, 7, 4, 12, 38, 12, 57);
        IterationHistoryEntry firstEntry = new IterationHistoryEntry();
        firstEntry.setTimestamp(startTime);
        firstEntry.setOriginalEstimateSum(130);
        firstEntry.setDeltaOriginalEstimate(0);
        IterationHistoryEntry secondEntry = new IterationHistoryEntry();
        secondEntry.setTimestamp(startTime.plusDays(1));
        secondEntry.setOriginalEstimateSum(156);
        secondEntry.setDeltaOriginalEstimate(26);
        IterationHistoryEntry thirdEntry = new IterationHistoryEntry();
        thirdEntry.setTimestamp(startTime.plusDays(2));
        thirdEntry.setOriginalEstimateSum(88);
        thirdEntry.setDeltaOriginalEstimate(-68);
        
        List<IterationHistoryEntry> entries = Arrays.asList(firstEntry, secondEntry, thirdEntry);
        
        TimeSeries actualSeries = super.getBurndownTimeSeries(entries,
                startTime.toLocalDate(), startTime.toLocalDate().plusDays(5));
        
        assertEquals(10, actualSeries.getItemCount());
    }
    
    @Test
    public void testGetBurndownDataItemForDay() {
        TimeSeriesDataItem actualItem = super.getBurndownDataItemForDay(entry1);
        
        assertEquals(ExactEstimateUtils.extractMajorUnits(
                new ExactEstimate(entry1.getEffortLeftSum())),
                actualItem.getValue());
    }
    
    @Test
    public void testGetBurndownScopedDataItemForDay() {
        initializeEntriesForScopingTest();
        
        startEntry.setTimestamp(new DateTime(2008, 4, 2, 12, 25, 32, 0));
        endEntry.setTimestamp(new DateTime(2008, 4, 3, 10, 19, 22, 0));
        startEntry.setEffortLeftSum(128);
        endEntry.setDeltaOriginalEstimate(150);
        DateTime expectedTimestamp = new DateTime(2008, 4, 3, 0, 0, 2, 0);
        
        Pair<TimeSeriesDataItem, TimeSeriesDataItem> scopedEntries
            = getBurndownScopedDataItemForDay(startEntry, endEntry);
        TimeSeriesDataItem nullEntry = scopedEntries.getFirst();
        TimeSeriesDataItem actualEntry = scopedEntries.getSecond();
        ExactEstimate estimatedValue = new ExactEstimate(128 + 150);
        
        assertEquals(new Second(expectedTimestamp.minusSeconds(1).toDate()), nullEntry.getPeriod());
        assertEquals(null, nullEntry.getValue());
        assertEquals(new Second(expectedTimestamp.toDate()), actualEntry.getPeriod());
        assertEquals(ExactEstimateUtils.extractMajorUnits(estimatedValue), actualEntry.getValue());
    }
    
    @Test
    public void testIsScopingDone() {
        IterationHistoryEntry entryWithoutScoping = new IterationHistoryEntry();
        IterationHistoryEntry entryWithScoping = new IterationHistoryEntry();
        entryWithScoping.setDeltaOriginalEstimate(-200);
        
        assertFalse(super.isScopingDone(entryWithoutScoping));
        assertTrue(super.isScopingDone(entryWithScoping));
    }
    
    @Test
    public void testGetCurrentDayTimeSeries() {
        initializeEntriesForScopingTest();
        endEntry.setDeltaEffortLeft(-100);
        endEntry.setOriginalEstimateSum(endEntry.getOriginalEstimateSum() - 100);
        endEntry.setEffortLeftSum(endEntry.getEffortLeftSum() - 80);
        endEntry.setDeltaEffortLeft(endEntry.getDeltaEffortLeft() - 80);
        
        ExactEstimate expectedStartValue = new ExactEstimate(
                startEntry.getEffortLeftSum() + endEntry.getDeltaOriginalEstimate());
        ExactEstimate expectedEndvalue = new ExactEstimate(endEntry.getEffortLeftSum());
        
        TimeSeries actualSeries = super.getCurrentDayTimeSeries(startEntry, endEntry);
        testSeriesStartAndEndCorrect(actualSeries, expectedStartValue, expectedEndvalue);
        
        assertEquals(CURRENT_DAY_SERIES_NAME, actualSeries.getKey());
        assertEquals(ExactEstimateUtils.extractMajorUnits(expectedStartValue),
                actualSeries.getDataItem(0).getValue());
    }
    
    
    @Test
    public void testGetScopingTimeSeries() {
        DateTime startTime = new DateTime(2012, 7, 4, 12, 38, 12, 57);
        IterationHistoryEntry firstEntry = new IterationHistoryEntry();
        firstEntry.setTimestamp(startTime);
        firstEntry.setOriginalEstimateSum(130);
        firstEntry.setDeltaOriginalEstimate(0);
        IterationHistoryEntry secondEntry = new IterationHistoryEntry();
        secondEntry.setTimestamp(startTime.plusDays(1));
        secondEntry.setOriginalEstimateSum(156);
        secondEntry.setDeltaOriginalEstimate(26);
        IterationHistoryEntry thirdEntry = new IterationHistoryEntry();
        thirdEntry.setTimestamp(startTime.plusDays(2));
        thirdEntry.setOriginalEstimateSum(88);
        thirdEntry.setDeltaOriginalEstimate(-68);
        
        List<IterationHistoryEntry> entries = Arrays.asList(firstEntry, secondEntry, thirdEntry);
        
        TimeSeries actualSeries = super.getScopingTimeSeries(entries,
                startDate.toLocalDate(), startTime.plusDays(5).toLocalDate());
        
        assertEquals(SCOPING_SERIES_NAME, actualSeries.getKey());
        assertEquals(6, actualSeries.getItemCount());
    }
    
    
    @Test
    public void testGetSeriesByStartAndEndPoints() {
        ExactEstimate value1 = new ExactEstimate(entry1.getEffortLeftSum());
        ExactEstimate value2 = new ExactEstimate(entry2.getEffortLeftSum());
        TimeSeries actualSeries = super.getSeriesByStartAndEndPoints(
                "Test series",
                entry1.getTimestamp(), value1,
                entry2.getTimestamp(), value2);
        
        testSeriesStartAndEndCorrect(actualSeries, value1, value2);
        assertEquals("Test series", actualSeries.getKey());
    }
    
    @Test
    public void testGetScopeSeriesDataItem() {
        initializeEntriesForScopingTest();
        startEntry.setTimestamp(new DateTime(2008, 4, 2, 12, 25, 32, 0));
        endEntry.setTimestamp(new DateTime(2008, 4, 3, 10, 19, 22, 0));
        startEntry.setEffortLeftSum(128);
        endEntry.setDeltaOriginalEstimate(150);
        DateTime expectedTimestamp = new DateTime(2008, 4, 3, 0, 0, 0, 0);
        
        List<TimeSeriesDataItem> actualItems
            = super.getScopeSeriesDataItems(startEntry, endEntry);
        
        TimeSeriesDataItem firstItem = actualItems.get(0);
        TimeSeriesDataItem secondItem = actualItems.get(1);
        TimeSeriesDataItem nullItem = actualItems.get(2);
        ExactEstimate expectedFirstValue = new ExactEstimate(128);
        ExactEstimate expectedSecondValue = new ExactEstimate(128 + 150);
        
        assertEquals(new Second(expectedTimestamp.toDate()), firstItem.getPeriod());
        assertEquals(ExactEstimateUtils.extractMajorUnits(expectedFirstValue), firstItem.getValue());
        assertEquals(new Second(expectedTimestamp.plusSeconds(2).toDate()), secondItem.getPeriod());
        assertEquals(ExactEstimateUtils.extractMajorUnits(expectedSecondValue), secondItem.getValue());
        assertEquals(new Second(expectedTimestamp.plusSeconds(3).toDate()), nullItem.getPeriod());
        assertNull(nullItem.getValue());
    }

    private void testSeriesStartAndEndCorrect(TimeSeries series, ExactEstimate value1, ExactEstimate value2) {
        assertEquals(ExactEstimateUtils.extractMajorUnits(value1), series.getDataItem(0).getValue());
        assertEquals(ExactEstimateUtils.extractMajorUnits(value2), series.getDataItem(1).getValue());
    }
    
    @Test
    public void testExactEstimateToDataItem() {
        ExactEstimate expectedValue = new ExactEstimate(120);
        Second expectedInstant = new Second(startDate.toDate());
        
        TimeSeriesDataItem actualDataItem
            = super.exactEstimateToDataItem(startDate, expectedValue);
        
        assertEquals(ExactEstimateUtils.extractMajorUnits(expectedValue),
                actualDataItem.getValue());
        assertEquals(0, expectedInstant.compareTo(actualDataItem.getPeriod()));
    }
    
}
