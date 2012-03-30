package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    
    public IterationHistoryEntryBusinessImpl() {
        super(IterationHistoryEntry.class);
    }
    
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
        newEntry.setTimestamp(new LocalDate());
        Pair<ExactEstimate, ExactEstimate> sums = iterationHistoryEntryDAO.calculateCurrentHistoryData(iterationId);
        long oldOriginalEstimateSum = (latest == null) ? 0 : latest.getOriginalEstimateSum();
        long effortLeftSum = (sums.first == null) ? 0 : sums.first.getMinorUnits();
        long originalEstimateSum = (sums.second == null) ? 0 : sums.second.getMinorUnits();
        
        if (latest != null &&
                Days.daysBetween(latest.getTimestamp().toDateMidnight(), new DateTime().toDateMidnight()).getDays() == 0) {
            latest.setEffortLeftSum(effortLeftSum);
            latest.setOriginalEstimateSum(originalEstimateSum);
            latest.setDeltaOriginalEstimate(latest.getDeltaOriginalEstimate() + originalEstimateSum - oldOriginalEstimateSum);
            iterationHistoryEntryDAO.store(latest);
        }
        else {
            newEntry.setEffortLeftSum(effortLeftSum);
            newEntry.setOriginalEstimateSum(originalEstimateSum);
            newEntry.setDeltaOriginalEstimate(originalEstimateSum - oldOriginalEstimateSum);
            iterationHistoryEntryDAO.store(newEntry);
        }
    }
    
    @Transactional(readOnly = true)
    public ExactEstimate getLatestOriginalEstimateSum(Iteration iteration) {
        IterationHistoryEntry latestEntry = iterationHistoryEntryDAO.retrieveLatest(iteration.getId());
        if (latestEntry == null) {
            return new ExactEstimate(0);
        }
        return new ExactEstimate(latestEntry.getOriginalEstimateSum());
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public List<IterationHistoryEntry> getHistoryEntriesForIteration(Iteration iteration) {
        return iterationHistoryEntryDAO.getHistoryEntriesForIteration(iteration.getId());
    }
    
    @Transactional(readOnly = true)
    public IterationHistoryEntry retrieveLatest(Iteration iteration) {
        return iterationHistoryEntryDAO.retrieveLatest(iteration.getId());
    }
    
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public LocalDate calculateExpectedEffortDoneDate(LocalDate startDate,
            ExactEstimate effortLeft, ExactEstimate velocity) {
        if (velocity.getMinorUnits() == 0) return null;
        int days = (int)Math.ceil(effortLeft.getMinorUnits() / velocity.getMinorUnits());
        return startDate.plusDays(days);
    }
    
}
