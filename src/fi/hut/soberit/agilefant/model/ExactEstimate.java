package fi.hut.soberit.agilefant.model;

import javax.persistence.Embeddable;

@Embeddable
public class ExactEstimate {

    private long minorUnits;

    public ExactEstimate() {
    }

    public ExactEstimate(long minorUnits) {
        this.minorUnits = minorUnits;
    }

    public void setMinorUnits(long minorUnits) {
        this.minorUnits = minorUnits;
    }

    public long getMinorUnits() {
        return minorUnits;
    }

}
