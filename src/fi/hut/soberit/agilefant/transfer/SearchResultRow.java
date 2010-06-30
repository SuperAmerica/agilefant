package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.NamedObject;

public class SearchResultRow {
    private String label;
    private int value;
    
    private NamedObject originalObject;
    
    public SearchResultRow() {
    
    }
    
    public SearchResultRow(String label, NamedObject original) {
        this.label = label;
        this.value = original.getId();
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
