package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;

public class UnassignedLoadTO {
    public ExactEstimate effortLeft;
    public int iterationId;
    public Iteration iteration;
    public int availabilitySum;
    public int availability;
    
    public UnassignedLoadTO() {};
    public UnassignedLoadTO(ExactEstimate effortLeft, int iterationId, int userAvailability) {
        this.availability = userAvailability;
        this.effortLeft = effortLeft;
        this.iterationId = iterationId;
    }
}
