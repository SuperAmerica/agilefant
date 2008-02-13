package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.AFTime;

/**
 * Class that holds the data for backlog items' effort lefts or original
 * estimates; includes hours and number of non-estimated items. ToString-method
 * always returns at least "0h".
 * 
 * @author Aleksi Toivonen
 */
public class EffortSumData {

    private AFTime effortHours = new AFTime(0);
    private int nonEstimatedItems = 0;

    /**
     * Overrides toString to show effort left/ original estimate hours and
     * number of non-estimated items. Returns never null, but at least "0h".
     * Shows e.g.: "0h", "5h", "5h + 1 non-estimated BLI" or "3 non-estimated
     * BLIs".
     */
    public String toString() {
        String effortHoursString = "" + effortHours;
        String nonEstimatedString = "";
        String plusString = " + ";
        // Don't show hours of the like "0h + X items", instead "X items"
        if (effortHours.getTime() == 0 && nonEstimatedItems > 0) {
            effortHoursString = "";
            plusString = "";
        }
        if (nonEstimatedItems == 1)
            nonEstimatedString += plusString + nonEstimatedItems
                    + " non-est. BLI";
        else if (nonEstimatedItems > 1)
            nonEstimatedString += plusString + nonEstimatedItems
                    + " non-est. BLIs";
        return effortHoursString + nonEstimatedString;
    }
    
    public void setEffortHours(AFTime effortHours) {
        this.effortHours = effortHours;
    }

    public void setNonEstimatedItems(int nonEstimatedItems) {
        this.nonEstimatedItems = nonEstimatedItems;
    }
    
    public AFTime getEffortHours() {
        return effortHours;
    }

    public int getNonEstimatedItems() {
        return nonEstimatedItems;
    }
}
