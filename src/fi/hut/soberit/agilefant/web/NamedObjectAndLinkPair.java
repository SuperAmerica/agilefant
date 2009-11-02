package fi.hut.soberit.agilefant.web;

import fi.hut.soberit.agilefant.model.NamedObject;

public class NamedObjectAndLinkPair {
    private String link;
    private NamedObject item;
    private String name;

    public NamedObjectAndLinkPair(NamedObject item, String link) {
        this.setItem(item);
        this.setLink(link);
        this.setName(item.getName());
    }

    public String getLink() {
        return link;
    }
    
    public void setName(String value) {
        this.name = value;
    }

    public String getName() {
        return name;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setItem(NamedObject item) {
        this.item = item;
    }

    public NamedObject getItem() {
        return item;
    }
}

