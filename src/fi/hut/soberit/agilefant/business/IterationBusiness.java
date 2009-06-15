package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.util.IterationDataContainer;

public interface IterationBusiness extends GenericBusiness<Iteration> {

    public IterationDataContainer getIterationContents(int iterationId);
    
    public IterationMetrics getIterationMetrics(Iteration iteration);
}
