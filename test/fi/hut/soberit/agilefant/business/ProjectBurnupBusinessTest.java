package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.jfree.data.time.TimeSeriesCollection;
import org.joda.time.DateTime;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.ProjectBurnupBusinessImpl;
import fi.hut.soberit.agilefant.util.Pair;
import fi.hut.soberit.agilefant.util.ProjectBurnupData;

public class ProjectBurnupBusinessTest extends ProjectBurnupBusinessImpl {
    
    @Test
    public void testConvertToDatasets_entriesInFuture() {
        List<ProjectBurnupData.Entry> entries = new LinkedList<ProjectBurnupData.Entry>();
        entries.add(new ProjectBurnupData.Entry(new DateTime().plusHours(1), 10, 10));
        entries.add(new ProjectBurnupData.Entry(new DateTime().plusHours(2), 20, 10));
        entries.add(new ProjectBurnupData.Entry(new DateTime().plusHours(3), 30, 20));
        ProjectBurnupData data = new ProjectBurnupData(entries);
        Pair<TimeSeriesCollection, TimeSeriesCollection> datasets = convertToDatasets(data);
        assertEquals(0, datasets.first.getSeries(0).getItemCount());
        assertEquals(0, datasets.second.getSeries(0).getItemCount());
    }
    
    @Test
    public void testConvertToDatasets_plannedSums() {
        List<ProjectBurnupData.Entry> entries = new LinkedList<ProjectBurnupData.Entry>();
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(3), 10, 10));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(2), 20, 10));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(1), 30, 10));
        ProjectBurnupData data = new ProjectBurnupData(entries);
        Pair<TimeSeriesCollection, TimeSeriesCollection> datasets = convertToDatasets(data);
        assertEquals(4, datasets.first.getSeries(0).getItemCount());
        assertEquals(2, datasets.second.getSeries(0).getItemCount());
    }

    @Test
    public void testConvertToDatasets_doneSums() {
        List<ProjectBurnupData.Entry> entries = new LinkedList<ProjectBurnupData.Entry>();
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(3), 40, 10));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(2), 40, 20));
        entries.add(new ProjectBurnupData.Entry(new DateTime().minusHours(1), 40, 30));
        ProjectBurnupData data = new ProjectBurnupData(entries);
        Pair<TimeSeriesCollection, TimeSeriesCollection> datasets = convertToDatasets(data);
        assertEquals(4, datasets.first.getSeries(0).getItemCount());
        assertEquals(4, datasets.second.getSeries(0).getItemCount());
    }

}
