package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.jfree.data.time.TimeSeriesCollection;
import org.joda.time.DateTime;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.ProjectBurnupBusinessImpl;
import fi.hut.soberit.agilefant.util.ProjectBurnupData;
import fi.hut.soberit.agilefant.util.Triple;

public class ProjectBurnupBusinessTest extends ProjectBurnupBusinessImpl {
    
    @Test
    public void testConvertToDatasets_entriesInFuture() {
        List<ProjectBurnupData.Entry> entries = new LinkedList<ProjectBurnupData.Entry>();
        entries.add(new ProjectBurnupData.Entry(new DateTime().plusHours(1), 10, 10, 20));
        entries.add(new ProjectBurnupData.Entry(new DateTime().plusHours(2), 20, 10, 30));
        entries.add(new ProjectBurnupData.Entry(new DateTime().plusHours(3), 30, 20, 40));
        ProjectBurnupData data = new ProjectBurnupData(entries);
        Triple<TimeSeriesCollection, TimeSeriesCollection, TimeSeriesCollection> datasets = convertToDatasets(data);
        assertEquals(0, datasets.first.getSeries(0).getItemCount());
        assertEquals(0, datasets.second.getSeries(0).getItemCount());
    }
    
    @Test
    public void testConvertToDatasets_plannedSums() {
        List<ProjectBurnupData.Entry> entries = new LinkedList<ProjectBurnupData.Entry>();
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(3), 10, 10, 20));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(2), 20, 10, 20));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(1), 30, 10, 20));
        ProjectBurnupData data = new ProjectBurnupData(entries);
        Triple<TimeSeriesCollection, TimeSeriesCollection, TimeSeriesCollection> datasets = convertToDatasets(data);
        assertEquals(4, datasets.first.getSeries(0).getItemCount());
        assertEquals(2, datasets.second.getSeries(0).getItemCount());
    }

    @Test
    public void testConvertToDatasets_doneSums() {
        List<ProjectBurnupData.Entry> entries = new LinkedList<ProjectBurnupData.Entry>();
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(3), 40, 10, 50));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(2), 40, 20, 60));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(1), 40, 30, 70));
        ProjectBurnupData data = new ProjectBurnupData(entries);
        Triple<TimeSeriesCollection, TimeSeriesCollection, TimeSeriesCollection> datasets = convertToDatasets(data);
        assertEquals(4, datasets.first.getSeries(0).getItemCount());
        assertEquals(4, datasets.second.getSeries(0).getItemCount());
    }

}
