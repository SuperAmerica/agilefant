package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.IterationHistoryEntry;

public interface IterationHistoryEntryBusiness extends
        GenericBusiness<IterationHistoryEntry> {

    void updateIterationHistory(int iterationId);

}
