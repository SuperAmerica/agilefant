package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.IterationDataContainer;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;

public interface IterationBusiness extends GenericBusiness<Iteration> {

    public IterationDataContainer getIterationContents(int iterationId);
    
    public IterationMetrics getIterationMetrics(Iteration iteration);
}
