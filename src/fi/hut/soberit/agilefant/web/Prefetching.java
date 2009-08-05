package fi.hut.soberit.agilefant.web;

public interface Prefetching {

    /**
     * This method should pre-fetch the data
     */
    public void initializePrefetchedData(int objectId);
}
