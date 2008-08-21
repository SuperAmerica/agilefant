package fi.hut.soberit.agilefant.business;

public interface JSONBusiness {
    
    /**
     * Get the object as JSON notation.
     * @param object the object to JSONize
     * @return the JSON string
     */
    public String objectToJSON(Object object);
    
    /**
     * Get the JSON object needed for user chooser.
     * @return
     */
    public String getUserChooserJSON(int backlogItemId, int backlogId);
}
