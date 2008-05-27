package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Setting;

/**
 * Interface for a DAO of a Setting. 
 * 
 * @see GenericDAO
 */
public interface SettingDAO extends GenericDAO<Setting> {
   
    /**
     * Get setting by name
     * @param settingName The name of the setting
     * @return the requested setting
     */
    public Setting getSetting(String settingName);
    
    /**
     * Get all settings ordered by name in descending order
     * @return all settings ordered by name in descending order 
     */
    public List<Setting> getAllOrderByName();
    
}
