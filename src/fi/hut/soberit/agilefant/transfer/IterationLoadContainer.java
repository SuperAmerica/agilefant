package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import flexjson.JSON;

public class IterationLoadContainer extends BacklogLoadContainer {
    private Iteration iteration;
    private long totalAssignedLoad = 0L;
    private long totalUnassignedLoad = 0L;

    @JSON(include=false)
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

    @Override
    public Backlog getBacklog() {
        return this.iteration;
    }
}
