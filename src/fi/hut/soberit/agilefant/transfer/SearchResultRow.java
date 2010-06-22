package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.NamedObject;

public class SearchResultRow {
    private String label;
    private NamedObject originalObject;
    
    public SearchResultRow() {
    
    }
    
    public SearchResultRow(String label, NamedObject original) {
        this.label = label;
        this.originalObject = original;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public NamedObject getOriginalObject() {
        return originalObject;
    }
    public void setOriginalObject(NamedObject originalObject) {
        this.originalObject = originalObject;
    }
}
