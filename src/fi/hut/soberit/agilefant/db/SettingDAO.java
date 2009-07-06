package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Setting;

/**
 * Interface for a DAO of a Setting. 
 * 
 * @see GenericDAO
 */
public interface SettingDAO extends GenericDAO<Setting> {

    Setting getByName(String name);

    Collection<Setting> getAllOrderByName();

}
