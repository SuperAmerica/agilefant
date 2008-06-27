package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.impl.SettingBusinessImpl;
import fi.hut.soberit.agilefant.model.Setting;

public class SettingAction extends ActionSupport {
    
    private static final long serialVersionUID = 6404856922329136680L;
    private int settingID;
    private String name;
    private String description;
    private String value;
    private String rangeLowValue;
    private String rangeHighValue;
    private String optimalLowValue;
    private String optimalHighValue;
    private String criticalLowValue;
    private Setting setting;
    private Log logger = LogFactory.getLog(getClass());
    private SettingBusiness settingBusiness;
    
    @Override
    public String execute() throws Exception {        
        value = new Boolean(settingBusiness.isHourReportingEnabled()).toString();
        rangeLowValue = new Integer(settingBusiness.getRangeLow()).toString();
        rangeHighValue = new Integer(settingBusiness.getRangeHigh()).toString();
        optimalLowValue = new Integer(settingBusiness.getOptimalLow()).toString();
        optimalHighValue = new Integer(settingBusiness.getOptimalHigh()).toString();
        criticalLowValue = new Integer(settingBusiness.getCriticalLow()).toString();
        return super.execute();
    }
    
    /**
     * {@inheritDoc}
     */
    public String create() {
        settingID = 0;
        setting = new Setting();
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String delete() {
        Setting s = settingBusiness.getSetting(settingID);
        if (s == null) {
            super.addActionError(super.getText("setting.notFound"));
            return Action.ERROR;
        }
        settingBusiness.delete(settingID);
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String edit() {
        if (settingID > 0) {
            setting = settingBusiness.getSetting(settingID);
        } else {
            setting = settingBusiness.getSetting(name);
        }
        if (setting == null) {
            super.addActionError(super.getText("setting.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    
    /**
     * {@inheritDoc}
     */
    public String store() {
        Setting storable = new Setting();
        if (settingID > 0) {
            storable = settingBusiness.getSetting(settingID);
        }else if( name != null ){
            storable = settingBusiness.getSetting(name); 
        }
        if (storable == null) {
            super.addActionError(super.getText("setting.notFound"));
            return Action.ERROR;
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        settingBusiness.store(storable);       
        return Action.SUCCESS;
    }  
    
    public String storeSettings() {
        settingBusiness.setHourReporting(value);
        loadMeterValues();
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        settingBusiness.setRangeLow(rangeLowValue);
        settingBusiness.setRangeHigh(rangeHighValue);
        settingBusiness.setOptimalLow(optimalLowValue);
        settingBusiness.setOptimalHigh(optimalHighValue);
        settingBusiness.setCriticalLow(criticalLowValue);            
        return Action.SUCCESS;
    }
    
    private String loadMeterValues() {
        Integer rangeLowInt = null;
        Integer rangeHighInt = null;
        Integer optimalLowInt = null;
        Integer optimalHighInt = null;
        Integer criticalLowInt = null;
        Integer defaultRangeLowInt = Integer.parseInt(SettingBusinessImpl.SETTING_DEFAULT_RANGE_LOW);
        Integer defaultRangeHighInt = Integer.parseInt(SettingBusinessImpl.SETTING_DEFAULT_RANGE_HIGH);
        Integer defaultOptimalLowInt = Integer.parseInt(SettingBusinessImpl.SETTING_DEFAULT_OPTIMAL_LOW);
        Integer defaultOptimalHighInt = Integer.parseInt(SettingBusinessImpl.SETTING_DEFAULT_OPTIMAL_HIGH);
        Integer defaultCriticalLowInt = Integer.parseInt(SettingBusinessImpl.SETTING_DEFAULT_CRITICAL_LOW);
        if (rangeLowValue.trim().equals("")) {
            rangeLowValue = null;            
        } else {           
            try {
                // Test if value can be parsed to an int.
                rangeLowInt = Integer.parseInt(rangeLowValue);               
            } catch(NumberFormatException nfe) {
                addActionError("Incorrect number format for minimum, enter an integer.");
                return Action.ERROR;
            }
            if (rangeLowInt < 0) {
                addActionError("Enter a positive integer for minimum.");
                return Action.ERROR;
            }           
        }
        if (optimalLowValue.trim().equals("")) {
            optimalLowValue = null;            
        } else {           
            try {
                // Test if value can be parsed to an int.
                optimalLowInt = Integer.parseInt(optimalLowValue);               
            } catch(NumberFormatException nfe) {
                addActionError("Incorrect number format for optimal low, enter an integer.");
                return Action.ERROR;
            }
            if (optimalLowInt < 0) {
                addActionError("Enter a positive integer for optimal low.");
                return Action.ERROR;
            }           
        }
        if (optimalHighValue.trim().equals("")) {
            optimalHighValue = null;            
        } else {           
            try {
                // Test if value can be parsed to an int.
                optimalHighInt = Integer.parseInt(optimalHighValue);               
            } catch(NumberFormatException nfe) {
                addActionError("Incorrect number format for optimal high, enter an integer.");
                return Action.ERROR;
            }
            if (optimalHighInt < 0) {
                addActionError("Enter a positive integer for optimal high.");
                return Action.ERROR;
            }           
        }
        if (criticalLowValue.trim().equals("")) {
            criticalLowValue = null;            
        } else {           
            try {
                // Test if value can be parsed to an int.
                criticalLowInt = Integer.parseInt(criticalLowValue);               
            } catch(NumberFormatException nfe) {
                addActionError("Incorrect number format for critical low, enter an integer.");
                return Action.ERROR;
            }
            if (criticalLowInt < 0) {
                addActionError("Enter a positive integer for critical low.");
                return Action.ERROR;
            }           
        }
        if (rangeHighValue.trim().equals("")) {
            rangeHighValue = null;            
        } else {           
            try {
                // Test if value can be parsed to an int.
                rangeHighInt = Integer.parseInt(rangeHighValue);               
            } catch(NumberFormatException nfe) {
                addActionError("Incorrect number format for maximum, enter an integer.");
                return Action.ERROR;
            }
            if (rangeHighInt < 0) {
                addActionError("Enter a positive integer for maximum.");
                return Action.ERROR;
            }           
        }
        // Check that rangeLowValue < optimalLowValue
        if (rangeLowInt != null && optimalLowInt != null) {
            if (rangeLowInt >= optimalLowInt) {
                addActionError("Minimum may not be greater than or equal to optimal low threshold.");
                return Action.ERROR;
            }
        } else if (rangeLowInt == null && optimalLowInt != null) {
            if (defaultRangeLowInt >= optimalLowInt) {
                addActionError("Minimum may not be greater than or equal to optimal low threshold.");
                return Action.ERROR;
            }
        } else if (rangeLowInt != null && optimalLowInt == null) {
            if (rangeLowInt >= defaultOptimalLowInt) {
                addActionError("Minimum may not be greater than or equal to optimal low threshold.");
                return Action.ERROR;
            }
        }
        // Check that optimalLowValue < optimalHighValue
        if (optimalLowInt != null && optimalHighInt != null) {
            if (optimalLowInt >= optimalHighInt) {
                addActionError("Optimal low may not be greater than or equal to optimal high threshold.");
                return Action.ERROR;
            }
        } else if (optimalLowInt == null && optimalHighInt != null) {
            if (defaultOptimalLowInt >= optimalHighInt) {
                addActionError("Optimal low may not be greater than or equal to optimal high threshold.");
                return Action.ERROR;
            }
        } else if (optimalLowInt != null && optimalHighInt == null) {
            if (optimalLowInt >= defaultOptimalHighInt) {
                addActionError("Optimal low may not be greater than or equal to optimal high threshold.");
                return Action.ERROR;
            }
        }
        // Check that optimalHighValue < criticalLowValue
        if (optimalHighInt != null && criticalLowInt != null) {
            if (optimalHighInt >= criticalLowInt) {
                addActionError("Optimal high may not be greater than or equal to  critical threshold.");
                return Action.ERROR;
            }
        } else if (optimalHighInt == null && criticalLowInt != null) {
            if (defaultOptimalHighInt >= criticalLowInt) {
                addActionError("Optimal high may not be greater than or equal to critical threshold.");
                return Action.ERROR;
            }
        } else if (optimalHighInt != null && criticalLowInt == null) {
            if (optimalHighInt >= defaultCriticalLowInt) {
                addActionError("Optimal high may not be greater than or equal to critical threshold.");
                return Action.ERROR;
            }
        }
        // Check that criticalLowValue < rangeHighValue
        if (criticalLowInt != null && rangeHighInt != null) {
            if (criticalLowInt >= rangeHighInt) {
                addActionError("Critical threshold may not be greater than or equal to maximum.");
                return Action.ERROR;
            }
        } else if (criticalLowInt == null && rangeHighInt != null) {
            if (defaultCriticalLowInt >= rangeHighInt) {
                addActionError("Critical threshold may not be greater than or equal to maximum.");
                return Action.ERROR;
            }
        } else if (criticalLowInt != null && rangeHighInt == null) {
            if (criticalLowInt >= defaultRangeHighInt) {
                addActionError("Critical threshold may not be greater than or equal to maximum.");
                return Action.ERROR;
            }
        }
        return Action.SUCCESS;
    }
    
    /**
     * A helper method for setting all required parameters for storing a setting to the database
     * @param storable the setting to be stored
     */
    protected void fillStorable(Setting storable) {
        storable.setName(name);
        storable.setDescription(description);
        storable.setValue(value);
    }
    
    /**
     * Get all settings
     * @return all settings
     */
    public Collection<Setting> getAllSettings() {
        return settingBusiness.getAll();
    }
    
    /**
     * Get all settings ordered by name
     * @return all settings ordered by name
     */
    public List<Setting> getAllSettingsOrderByName() {
        return settingBusiness.getAllOrderByName();
    }
    
    public void setSetting(Setting setting) {
        this.setting = setting;
    }
    
    public Setting getSetting() {
        return setting;
    }
    
    public void setSettingId(int settingID) {
        this.settingID = settingID;
    }
    
    public int getSettingId(){
        return settingID;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue(){
        return value;
    }

    public SettingBusiness getSettingBusiness() {
        return settingBusiness;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

    public String getRangeLowValue() {
        return rangeLowValue;
    }

    public void setRangeLowValue(String rangeLowValue) {
        this.rangeLowValue = rangeLowValue;
    }

    public String getRangeHighValue() {
        return rangeHighValue;
    }

    public void setRangeHighValue(String rangeHighValue) {
        this.rangeHighValue = rangeHighValue;
    }

    public String getOptimalLowValue() {
        return optimalLowValue;
    }

    public void setOptimalLowValue(String optimalLowValue) {
        this.optimalLowValue = optimalLowValue;
    }

    public String getOptimalHighValue() {
        return optimalHighValue;
    }

    public void setOptimalHighValue(String optimalHighValue) {
        this.optimalHighValue = optimalHighValue;
    }

    public String getCriticalLowValue() {
        return criticalLowValue;
    }

    public void setCriticalLowValue(String criticalLowValue) {
        this.criticalLowValue = criticalLowValue;
    }
}
