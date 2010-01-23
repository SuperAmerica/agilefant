package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;

public interface LabelDAO extends GenericDAO<Label> {

    public boolean labelExists(String labelName, Story story);

    List<Label> lookupLabelsLike(String labelName);

}