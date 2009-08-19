package fi.hut.soberit.agilefant.model;

@SuppressWarnings("serial")
public class SignedExactEstimate extends ExactEstimate {
    public static SignedExactEstimate ZERO = new SignedExactEstimate(0);
    
    public SignedExactEstimate() {
    }

    public SignedExactEstimate(long minorUnits) {
        super(minorUnits);
    }

}
