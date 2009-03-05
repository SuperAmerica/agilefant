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
    
    public Setting getSetting(String name);
    
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
    
    /**
     * Is hour reporting enabled
     */
    public boolean isHourReportingEnabled();
    
    /**
     * Set hour reporting mode
     */
    public void setHourReporting(String mode);
    
    /**
     * Is project burndown enabled
     */
    public boolean isProjectBurndownEnabled();
    
    /**
     * Set project burndown mode
     */
    public void setProjectBurndown(String mode);
    
    /**
     * Set value range low limit for load meter
     * @param value
     */
    public void setRangeLow(String value);
    
    /**
     * Get value range low limit for load meter
     * @return the percentage value
     */
    public int getRangeLow();
    
    /**
     * Set value range high limit for load meter
     * @param value
     */
    public void setRangeHigh(String value);
    
    /**
     * Get value range high limit for load meter
     * @return the percentage value
     */
    public int getRangeHigh();
    
    /**
     * Set optimal low value for load meter
     * @param value the percentage value
     */
    public void setOptimalLow(String value);
    
    /**
     * Get optimal low value for load meter
     * @return the percentage value
     */
    public int getOptimalLow();
    
    /**
     * Set optimal high value for load meter
     * @param value the percentage value
     */
    public void setOptimalHigh(String value);
    
    /**
     * Get optimal high value for load meter
     * @return the percentage value
     */
    public int getOptimalHigh();
    
    /**
     * Set critical low value for load meter
     * @param value the percentage value
     */
    public void setCriticalLow(String value);
    
    /**
     * Get critical low value for load meter
     * @return the percentage value
     */
    public int getCriticalLow();
    
}
