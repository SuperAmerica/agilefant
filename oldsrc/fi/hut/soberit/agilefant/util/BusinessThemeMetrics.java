package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.AFTime;

public class BusinessThemeMetrics {
    
    private int numberOfBlis;
    private int numberOfDoneBlis;
    private int donePercentage;
    private AFTime effortLeft = new AFTime(0);
    private AFTime originalEstimate = new AFTime(0);
    
    public AFTime getEffortLeft() {
        return effortLeft;
    }
    public void setEffortLeft(AFTime effortLeft) {
        this.effortLeft = effortLeft;
    }
    public AFTime getOriginalEstimate() {
        return originalEstimate;
    }
    public void setOriginalEstimate(AFTime originalEstimate) {
        this.originalEstimate = originalEstimate;
    }
    public int getNumberOfBlis() {
        return numberOfBlis;
    }
    public void setNumberOfBlis(int numberOfBlis) {
        this.numberOfBlis = numberOfBlis;
    }
    public int getNumberOfDoneBlis() {
        return numberOfDoneBlis;
    }
    public void setNumberOfDoneBlis(int numberOfDoneBlis) {
        this.numberOfDoneBlis = numberOfDoneBlis;
    }
    public int getDonePercentage() {
        return donePercentage;
    }
    public void setDonePercentage(int donePercentage) {
        this.donePercentage = donePercentage;
    }

}
