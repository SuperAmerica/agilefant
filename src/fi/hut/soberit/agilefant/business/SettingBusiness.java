package fi.hut.soberit.agilefant.business;


import fi.hut.soberit.agilefant.model.Setting;

/**
 * Business interface for handling functionality related to settings.
 * 
 * @author kjniiran
 *
 */
public interface SettingBusiness extends GenericBusiness<Setting> {
    
    public static final int DEFAULT_RANGE_LOW = 0;
    public static final int DEFAULT_OPTIMAL_LOW = 70;
    public static final int DEFAULT_OPTIMAL_HIGH = 85;
    public static final int DEFAULT_CRITICAL_LOW = 100;
    public static final int DEFAULT_RANGE_HIGH = 120;
    
    /**
     * Is hour reporting enabled
     */
    boolean isHourReportingEnabled();
    
    /**
     * Set hour reporting mode
     */
    void setHourReporting(boolean mode);
    
    /**
     * Set value range low limit for load meter
     * @param value
     */
    void setRangeLow(Integer value);
    
    /**
     * Get value range low limit for load meter
     * @return the percentage value
     */
    int getRangeLow();
    
    /**
     * Set value range high limit for load meter
     * @param value
     */
    void setRangeHigh(Integer value);
    
    /**
     * Get value range high limit for load meter
     * @return the percentage value
     */
    int getRangeHigh();
    
    /**
     * Set optimal low value for load meter
     * @param value the percentage value
     */
    void setOptimalLow(Integer value);
    
    /**
     * Get optimal low value for load meter
     * @return the percentage value
     */
    int getOptimalLow();
    
    /**
     * Set optimal high value for load meter
     * @param value the percentage value
     */
    void setOptimalHigh(Integer value);
    
    /**
     * Get optimal high value for load meter
     * @return the percentage value
     */
    int getOptimalHigh();
    
    /**
     * Set critical low value for load meter
     * @param value the percentage value
     */
    void setCriticalLow(Integer value);
    
    /**
     * Get critical low value for load meter
     * @return the percentage value
     */
    int getCriticalLow();
    
}
