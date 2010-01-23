package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Label;

public interface LabelBusiness extends GenericBusiness<Label> {
   
    public void createStoryLabels(List<String> labelNames, Integer storyId);

    List<Label> lookupLabelsLike(String labelName);

}