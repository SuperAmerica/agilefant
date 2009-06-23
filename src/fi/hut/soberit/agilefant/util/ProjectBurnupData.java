package fi.hut.soberit.agilefant.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.DateTime;

public class ProjectBurnupData implements Iterable<ProjectBurnupData.Entry> {

    private final List<Object[]> data;

    public ProjectBurnupData(List<Object[]> data) {
        this.data = data;
    }

    public class Entry {

        public final long doneSum;
        public final long estimateSum;
        public final DateTime timestamp;

        private Entry(DateTime timestamp, long estimateSum, long doneSum) {
            this.timestamp = timestamp;
            this.estimateSum = estimateSum;
            this.doneSum = doneSum;
        }

    }

    private class EntryIterator implements Iterator<Entry> {

        private int index = 0;

        public boolean hasNext() {
            return (index < data.size());
        }

        public Entry next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            Object[] values = data.get(index);
            index++;
            return new Entry((DateTime) values[0], (Long) values[1],
                    (Long) values[2]);
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported!");
        }

    }

    public Iterator<Entry> iterator() {
        return new EntryIterator();
    }

}
