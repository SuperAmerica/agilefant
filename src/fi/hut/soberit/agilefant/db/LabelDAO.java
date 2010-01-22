package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;

public interface LabelDAO extends GenericDAO<Label> {

    public boolean labelExists(String labelName, Story story);
}