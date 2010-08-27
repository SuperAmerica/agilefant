package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Task;

public class DailyWorkTaskTO extends TaskTO {
    private int workQueueRank;
    
    public DailyWorkTaskTO() {};
    public DailyWorkTaskTO(Task task) {
        super(task);
    }
    
    public DailyWorkTaskTO(Task task, int workQueueRank) {
        super(task);
        this.workQueueRank = workQueueRank;
    }
    
    public int getWorkQueueRank() {
        return workQueueRank;
    }

    public void setWorkQueueRank(int workQueueRank) {
        this.workQueueRank = workQueueRank;
    }

}
