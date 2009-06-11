package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.IterationBurndownBusinessImpl;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.util.ExactEstimateUtils;

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
    }
    
    @Test
    public void testGetIterationBurndown() {
        expect(iterationHistoryEntryBusiness.getLatestOriginalEstimateSum(iteration))
        .andReturn(originalEstimateSum);
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);
        
        assertNotNull(iterationBurndownBusiness.getIterationBurndown(iteration));
        
        verify(iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testConstructChart() {
        expect(iterationHistoryEntryBusiness.getLatestOriginalEstimateSum(iteration))
            .andReturn(originalEstimateSum);
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);
        
        assertEquals(JFreeChart.class, super.constructChart(iteration).getClass());
        
        verify(iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testFormatChartAxes() {
        super.formatChartAxes(chart, startDate, endDate);
        
        DateAxis actualAxis = (DateAxis)chart.getXYPlot().getDomainAxis();
        
        assertEquals("Start date not same", startDate.toDateMidnight().toDate(), actualAxis.getMinimumDate());
        assertEquals("End date not same", endDate.plusDays(1).toDateMidnight().toDate(), actualAxis.getMaximumDate());
        assertEquals("Date format not correct", new SimpleDateFormat("EEE d.M."), actualAxis.getDateFormatOverride());
    }
    
    @Test
    public void testFormatChartStyle() {
        super.formatChartStyle(chart);
        assertEquals(CHART_BACKGROUND_COLOR, chart.getBackgroundPaint());
        assertEquals(PLOT_BACKGROUND_COLOR, chart.getPlot().getBackgroundPaint());
    }
    
    @Test
    public void testGetDataset() {
        
        expect(iterationHistoryEntryBusiness.getLatestOriginalEstimateSum(iteration))
            .andReturn(originalEstimateSum);
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(Arrays.asList(entry));
        replay(iterationHistoryEntryBusiness);
        
        TimeSeriesCollection actualTimeSeries = super.getDataset(iteration);
        assertNotNull(actualTimeSeries.getSeries(REFERENCE_SERIES_NAME));
        assertNotNull(actualTimeSeries.getSeries(BURNDOWN_SERIES_NAME));
        
        verify(iterationHistoryEntryBusiness);
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
        IterationHistoryEntry entry1 = new IterationHistoryEntry();
        entry1.setTimestamp(startDate);
        entry1.setEffortLeftSum(100);
        entry1.setOriginalEstimateSum(100);
        IterationHistoryEntry entry2 = new IterationHistoryEntry();
        entry2.setTimestamp(startDate.plusDays(1));
        entry2.setEffortLeftSum(0);
        entry2.setOriginalEstimateSum(100);
        
        List<IterationHistoryEntry> returnedList = Arrays.asList(entry1, entry2);
        
        expect(iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration))
            .andReturn(returnedList);
        replay(iterationHistoryEntryBusiness);
        
        TimeSeries actualSeries = super.getBurndownTimeSeries(iteration);
        
        assertEquals(ExactEstimateUtils.extractMajorUnits(new ExactEstimate(entry1.getEffortLeftSum())),
                actualSeries.getDataItem(0).getValue());
        assertEquals(ExactEstimateUtils.extractMajorUnits(new ExactEstimate(entry2.getEffortLeftSum())),
                actualSeries.getDataItem(1).getValue());
        
        verify(iterationHistoryEntryBusiness);
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
