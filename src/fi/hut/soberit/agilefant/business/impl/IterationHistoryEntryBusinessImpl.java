package fi.hut.soberit.agilefant.business.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.util.Pair;

@Service("iterationHistoryEntryBusiness")
@Transactional
public class IterationHistoryEntryBusinessImpl extends
        GenericBusinessImpl<IterationHistoryEntry> implements
        IterationHistoryEntryBusiness {
    
    private IterationHistoryEntryDAO iterationHistoryEntryDAO;
    
    @Autowired
    private IterationDAO iterationDAO;
    
    @Autowired
    public void setIterationHistoryEntryDAO(
            IterationHistoryEntryDAO iterationHistoryEntryDAO) {
        this.iterationHistoryEntryDAO = iterationHistoryEntryDAO;
        this.genericDAO = iterationHistoryEntryDAO;
    }

    public void updateIterationHistory(int iterationId) {
        Iteration iteration = iterationDAO.get(iterationId);
        IterationHistoryEntry latest = iterationHistoryEntryDAO.retrieveLatest(iterationId);
        IterationHistoryEntry newEntry = new IterationHistoryEntry();
        newEntry.setIteration(iteration);
        newEntry.setTimestamp(new DateTime());
        Pair<ExactEstimate, ExactEstimate> sums = iterationHistoryEntryDAO.calculateCurrentHistoryData(iterationId);
        long oldEffortLeftSum = (latest == null) ? 0 : latest.getEffortLeftSum();
        long oldOriginalEstimateSum = (latest == null) ? 0 : latest.getOriginalEstimateSum();
        long effortLeftSum = (sums.first == null) ? 0 : sums.first.getMinorUnits();
        long originalEstimateSum = (sums.second == null) ? 0 : sums.second.getMinorUnits();
        newEntry.setEffortLeftSum(effortLeftSum);
        newEntry.setOriginalEstimateSum(originalEstimateSum);
        newEntry.setDeltaEffortLeft(effortLeftSum - oldEffortLeftSum);
        newEntry.setDeltaOriginalEstimate(originalEstimateSum - oldOriginalEstimateSum);
        iterationHistoryEntryDAO.store(newEntry);        
    }
    
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }
    
}
