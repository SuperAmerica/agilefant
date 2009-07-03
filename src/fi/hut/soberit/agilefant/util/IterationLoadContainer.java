package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.Iteration;

public class IterationLoadContainer {
    private Iteration iteration;
    private long totalAssignedLoad = 0L;
    private long totalUnassignedLoad = 0L;
    private long totalBaselineLoad = 0L;
    
    
    public Iteration getIteration() {
        return iteration;
    }
    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }
    public long getTotalAssignedLoad() {
        return totalAssignedLoad;
    }
    public void setTotalAssignedLoad(long totalAssignedLoad) {
        this.totalAssignedLoad = totalAssignedLoad;
    }
    public long getTotalUnassignedLoad() {
        return totalUnassignedLoad;
    }
    public void setTotalUnassignedLoad(long totalUnassignedLoad) {
        this.totalUnassignedLoad = totalUnassignedLoad;
    }
    public long getTotalBaselineLoad() {
        return totalBaselineLoad;
    }
    public void setTotalBaselineLoad(long totalBaselineLoad) {
        this.totalBaselineLoad = totalBaselineLoad;
    }

}
