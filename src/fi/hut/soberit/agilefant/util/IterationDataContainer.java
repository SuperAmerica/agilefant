package fi.hut.soberit.agilefant.util;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.IterationGoal;
import flexjson.JSON;

public class IterationDataContainer {

    private List<IterationGoal> iterationGoals;
    private Collection<BacklogItem> itemsWithoutGoal;
    
    @JSON(include=true)
    public List<IterationGoal> getIterationGoals() {
        return iterationGoals;
    }
    public void setIterationGoals(List<IterationGoal> iterationGoals) {
        this.iterationGoals = iterationGoals;
    }
    @JSON(include=true)
    public Collection<BacklogItem> getItemsWithoutGoal() {
        return itemsWithoutGoal;
    }
    public void setItemsWithoutGoal(Collection<BacklogItem> collection) {
        this.itemsWithoutGoal = collection;
    }
}
