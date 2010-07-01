package fi.hut.soberit.agilefant.db;


import java.util.List;

import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WidgetCollection;

public interface WidgetCollectionDAO extends GenericDAO<WidgetCollection> {

    /**
     * Get all <code>WidgetCollection</code>s for the given user.
     * <p>
     * Will retrieve all public collections if user is null
     * @param user TODO
     * @return
     */
    public List<WidgetCollection> getCollectionsForUser(User user);
}