package fi.hut.soberit.agilefant.db.history;

import java.util.List;

import fi.hut.soberit.agilefant.transfer.HistoryRowTO;

public interface GenericHistoryDAO<T> {
    public List<HistoryRowTO> retrieveLatestChanges(int objectId, Integer numberOfChanges);

}
