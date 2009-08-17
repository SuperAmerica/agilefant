package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Backlog;
import flexjson.JSON;


public abstract class BacklogLoadContainer {

    private long totalBaselineLoad = 0L;
    private long totalFutureLoad = 0L;


    public long getTotalBaselineLoad() {
        return totalBaselineLoad;
    }

    public void setTotalBaselineLoad(long totalBaselineLoad) {
        this.totalBaselineLoad = totalBaselineLoad;
    }

    public long getTotalFutureLoad() {
        return totalFutureLoad;
    }

    public void setTotalFutureLoad(long totalFutureLoad) {
        this.totalFutureLoad = totalFutureLoad;
    }
    
    @JSON
    public abstract Backlog getBacklog();

}
