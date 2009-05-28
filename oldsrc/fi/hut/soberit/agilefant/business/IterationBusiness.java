package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.util.IterationDataContainer;

public interface IterationBusiness {

    public IterationDataContainer getIterationContents(int iterationId,
            boolean excludeBacklogItems);

    public IterationDataContainer getIterationContents(Iteration iter,
            boolean excludeBacklogItems);

    int count();

}
