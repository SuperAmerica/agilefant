package fi.hut.soberit.agilefant.model;

import javax.persistence.Embeddable;

@Embeddable
public class ExactEstimate implements Comparable<ExactEstimate> {

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
    
    public int compareTo(ExactEstimate o) {
        long thisMinorUnits = this.getMinorUnits();
        long otherMinorUnits = o.getMinorUnits();
        if (otherMinorUnits < thisMinorUnits) {
            return -1;
        }
        else if (otherMinorUnits > thisMinorUnits) {
            return 1;
        }
        return 0;
    }
}
