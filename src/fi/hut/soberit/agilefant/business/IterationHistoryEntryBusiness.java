package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;

public interface IterationHistoryEntryBusiness extends
        GenericBusiness<IterationHistoryEntry> {

    void updateIterationHistory(int iterationId);

    public ExactEstimate getLatestOriginalEstimateSum(Iteration iteration);
}
