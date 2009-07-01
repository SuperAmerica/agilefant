package fi.hut.soberit.agilefant.web;

public interface SingleFieldEditable {

    /**
     * The method should fetch the persisted data for single field editing.
     * @param objectId TODO
     */
    public void initializeDataForEditing(int objectId);
    
    public String getIdFieldName();
}
