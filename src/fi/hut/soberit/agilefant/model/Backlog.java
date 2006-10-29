package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

public abstract class Backlog {
    
    private int id;
    private String name;
    private String description;
    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();
    public Collection<BacklogItem> getBacklogItems() {
        return backlogItems;
    }
    public void setBacklogItems(Collection<BacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
