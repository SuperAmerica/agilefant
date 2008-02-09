package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.AFTime;

/**
 * Class that holds the data for backlog items' effort lefts; includes hours and
 * number of non-estimated items. ToString-method always returns at least "0h".
 * 
 * @author Aleksi Toivonen
 */
public class EffortLeftSumData {

    private AFTime effortLeftHours = new AFTime(0);
    private int nonEstimatedItems = 0;
    
    public void setEffortLeftHours(AFTime effortLeftHours) {
        this.effortLeftHours = effortLeftHours;
    }

    public void setNonEstimatedItems(int nonEstimatedItems) {
        this.nonEstimatedItems = nonEstimatedItems;
    }

    /**
     * Overrides toString to show effort left hours and number of non-estimated items.
     * Returns never null, but at least "0h".
     * Shows e.g.:
     * "0h",
     * "5h",
     * "5h + 1 non-estimated BLI" or
     * "3 non-estimated BLIs".
     */
    public String toString() {
        String effortLeftHoursString = "" + effortLeftHours;
        String nonEstimatedString = "";
        String plusString = " + ";
        // Don't show hours of the like "0h + X items", instead "X items" 
        if (effortLeftHours.getTime() == 0 && nonEstimatedItems > 0) {
            effortLeftHoursString = "";
            plusString = "";
        }
        if (nonEstimatedItems == 1)
            nonEstimatedString += plusString + nonEstimatedItems + " non-estimated BLI";
        else if (nonEstimatedItems > 1)
            nonEstimatedString += plusString + nonEstimatedItems + " non-estimated BLIs";
        return effortLeftHoursString + nonEstimatedString;
    }
}
