package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Setting;

/**
 * Business interface for handling functionality related to settings.
 * 
 * @author kjniiran
 *
 */
public interface SettingBusiness extends GenericBusiness<Setting> {

    Setting retrieveByName(String name);
    
    /**
     * Get all settings ordered by name
     * @return all settings ordered by name
     */
    List<Setting> getAllOrderByName();
    
    /**
     * Is hour reporting enabled
     */
    boolean isHourReportingEnabled();
    
    /**
     * Set hour reporting mode
     */
    void setHourReporting(String mode);
    
    /**
     * Is project burndown enabled
     */
    boolean isProjectBurndownEnabled();
    
    /**
     * Set project burndown mode
     */
    void setProjectBurndown(String mode);
    
    /**
     * Set value range low limit for load meter
     * @param value
     */
    void setRangeLow(String value);
    
    /**
     * Get value range low limit for load meter
     * @return the percentage value
     */
    int getRangeLow();
    
    /**
     * Set value range high limit for load meter
     * @param value
     */
    void setRangeHigh(String value);
    
    /**
     * Get value range high limit for load meter
     * @return the percentage value
     */
    int getRangeHigh();
    
    /**
     * Set optimal low value for load meter
     * @param value the percentage value
     */
    void setOptimalLow(String value);
    
    /**
     * Get optimal low value for load meter
     * @return the percentage value
     */
    int getOptimalLow();
    
    /**
     * Set optimal high value for load meter
     * @param value the percentage value
     */
    void setOptimalHigh(String value);
    
    /**
     * Get optimal high value for load meter
     * @return the percentage value
     */
    int getOptimalHigh();
    
    /**
     * Set critical low value for load meter
     * @param value the percentage value
     */
    void setCriticalLow(String value);
    
    /**
     * Get critical low value for load meter
     * @return the percentage value
     */
    int getCriticalLow();
    
}
