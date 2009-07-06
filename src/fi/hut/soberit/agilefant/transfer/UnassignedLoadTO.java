package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;

public class UnassignedLoadTO {
    public ExactEstimate effortLeft;
    public int iterationId;
    public Iteration iteration;
    public short availabilitySum;
    public short availability;
    
    public UnassignedLoadTO(ExactEstimate effortLeft, int iterationId, short userAvailability) {
        this.availability = userAvailability;
        this.effortLeft = effortLeft;
        this.iterationId = iterationId;
    }
}
