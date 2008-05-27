package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Setting;
/**
 * Business interface for handling functionality related to settings.
 * 
 * @author kjniiran
 *
 */

public interface SettingBusiness {

    /**
     * Get setting by ID.
     * @param settingID The id of the setting
     * @return the requested setting
     */
    public Setting getSetting(int settingID);
    
    /**
     * Removes the specified setting.
     * @param settingID the id of the setting to be removed
     */
    public void delete(int settingID);
    
    /**
     * Stores the specified setting.
     * @param setting the setting to be stored
     */
    public void store(Setting setting);
    
    /**
     * Get all settings
     * @return all settings
     */
    public Collection<Setting> getAll();
    
    /**
     * Get all settings ordered by name
     * @return all settings ordered by name
     */
    public List<Setting> getAllOrderByName();
    
}
