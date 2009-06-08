package fi.hut.soberit.agilefant.model;

import javax.persistence.Embeddable;

@Embeddable
public class ExactEstimate {

    private Long minorUnits;

    public ExactEstimate() {
    }

    public ExactEstimate(long minorUnits) {
        this.minorUnits = minorUnits;
    }

    public void setMinorUnits(Long minorUnits) {
        this.minorUnits = minorUnits;
    }

    public Long getMinorUnits() {
        return minorUnits;
    }

}
