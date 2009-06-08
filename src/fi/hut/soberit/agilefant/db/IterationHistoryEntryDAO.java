package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.util.Pair;

public interface IterationHistoryEntryDAO extends
        GenericDAO<IterationHistoryEntry> {

    IterationHistoryEntry retrieveLatest(int iterationId);

    Pair<ExactEstimate, ExactEstimate> calculateCurrentHistoryData(int iterationId);

}
