package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;
import fi.hut.soberit.agilefant.util.BeanCopier;
import flexjson.JSON;

public class AssignmentTO extends Assignment {

    private ExactEstimate assignedLoad = new ExactEstimate(0);
    private ExactEstimate unassignedLoad = new ExactEstimate(0);
    private ExactEstimate availableWorktime = new ExactEstimate(0);
    private SignedExactEstimate totalLoad = new SignedExactEstimate(0);
    private SignedExactEstimate baselineLoad = new SignedExactEstimate(0);
    private boolean unassigned = false;
    private int loadPercentage = 0;

    public AssignmentTO() {};
    public AssignmentTO(Assignment assignment) {
        BeanCopier.copy(assignment, this);
    }

    @JSON
    public ExactEstimate getAssignedLoad() {
        return assignedLoad;
    }

    public void setAssignedLoad(ExactEstimate assignedLoad) {
        this.assignedLoad = assignedLoad;
    }

    @JSON
    public ExactEstimate getUnassignedLoad() {
        return unassignedLoad;
    }

    public void setUnassignedLoad(ExactEstimate unassignedLoad) {
        this.unassignedLoad = unassignedLoad;
    }

    @JSON
    public SignedExactEstimate getTotalLoad() {
        return this.totalLoad;
    }

    @JSON
    public ExactEstimate getAvailableWorktime() {
        return availableWorktime;
    }

    public void setAvailableWorktime(ExactEstimate availableWorktime) {
        this.availableWorktime = availableWorktime;
    }

    @JSON
    public int getLoadPercentage() {
        return loadPercentage;
    }

    public void setLoadPercentage(int loadPercentage) {
        this.loadPercentage = loadPercentage;
    }

    public void setTotalLoad(SignedExactEstimate totalLoad) {
        this.totalLoad = totalLoad;
    }

    @JSON
    public boolean isUnassigned() {
        return unassigned;
    }

    public void setUnassigned(boolean unassigned) {
        this.unassigned = unassigned;
    }
    
    @JSON
    public SignedExactEstimate getBaselineLoad() {
        return baselineLoad;
    }

    public void setBaselineLoad(SignedExactEstimate baselineLoad) {
        this.baselineLoad = baselineLoad;
    }
}
