package fi.hut.soberit.agilefant.web;

public interface Prefetching {

    /**
     * This method should fetch the pre
     */
    public void initializePrefetchedData(int objectId);
    
    /**
     * Get the name of the request parameter that should contain the
     * required object's id.
     */
    public String getIdFieldName();
}
