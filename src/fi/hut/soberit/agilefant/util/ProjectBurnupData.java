package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

public class ProjectBurnupData implements Iterable<ProjectBurnupData.Entry> {

    private final List<ProjectBurnupData.Entry> entries;

    public ProjectBurnupData(List<ProjectBurnupData.Entry> entries) {
        this.entries = entries;
    }

    public static class Entry {

        public final long doneSum;
        public final long estimateSum;
        public final DateTime timestamp;

        public Entry(DateTime timestamp, long estimateSum, long doneSum) {
            this.timestamp = timestamp;
            this.estimateSum = estimateSum;
            this.doneSum = doneSum;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            builder.append(timestamp.toString());
            builder.append("]: ");
            builder.append(estimateSum);
            builder.append(" planned, ");
            builder.append(doneSum);
            builder.append(" done");
            return builder.toString();
        }

    }

    public Iterator<Entry> iterator() {
        return Collections.unmodifiableList(entries).iterator();
    }

    public static ProjectBurnupData createFromRawData(List<Object[]> rawData) {
        List<ProjectBurnupData.Entry> entries = new ArrayList<ProjectBurnupData.Entry>(
                rawData.size());
        for (Object[] rawEntry : rawData) {
            entries.add(new Entry((DateTime) rawEntry[0],
                    (Long) rawEntry[1], (Long) rawEntry[2]));
        }
        return new ProjectBurnupData(entries);
    }

}
