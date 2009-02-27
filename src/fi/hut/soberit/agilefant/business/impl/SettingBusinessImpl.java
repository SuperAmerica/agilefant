package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.Setting;

/**
 * Business implementation for handling of settings
 * 
 * @author kjniiran
 *
 */

public class SettingBusinessImpl implements SettingBusiness {
    private SettingDAO settingDAO;
    
    public static final String SETTING_NAME_HOUR_REPORTING = "HourReporting";
    public static final String SETTING_VALUE_TRUE = "true";
    public static final String SETTING_NAME_RANGE_LOW = "RangeLow";
    public static final String SETTING_DEFAULT_RANGE_LOW = "0";
    public static final String SETTING_NAME_RANGE_HIGH = "RangeHigh";
    public static final String SETTING_DEFAULT_RANGE_HIGH = "120";
    public static final String SETTING_NAME_OPTIMAL_LOW = "OptimalLow";
    public static final String SETTING_DEFAULT_OPTIMAL_LOW = "70";
    public static final String SETTING_NAME_OPTIMAL_HIGH = "OptimalHigh";
    public static final String SETTING_DEFAULT_OPTIMAL_HIGH = "85";
    public static final String SETTING_NAME_CRITICAL_LOW = "CriticalLow";
    public static final String SETTING_DEFAULT_CRITICAL_LOW = "100";
    public static final String SETTING_NAME_PROJECT_BURNDOWN = "ProjectBurndown";
    
    /**
     * {@inheritDoc}
     */
    public Collection<Setting> getAll() {
        return settingDAO.getAll();
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Setting> getAllOrderByName() {
        return settingDAO.getAllOrderByName();
    }
    
    /**
     * {@inheritDoc}
     */
    public Setting getSetting(int settingID) {
        return settingDAO.get(settingID);
    }
    
    public SettingDAO getSettingDAO() {
        return settingDAO;
    }
    
    public void setSettingDAO(SettingDAO settingDAO) {
        this.settingDAO = settingDAO;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int settingID) {
        settingDAO.remove(settingID);
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(Setting setting) {
        settingDAO.store(setting);
    }
    
    public void setHourReporting(String mode) {
        Setting setting = settingDAO.getSetting(SETTING_NAME_HOUR_REPORTING);
        boolean selection = (mode != null && mode.equals(SETTING_VALUE_TRUE));
        if(setting == null) {
            setting = new Setting();
            setting.setName(SETTING_NAME_HOUR_REPORTING);
            setting.setValue(new Boolean(selection).toString());
            settingDAO.create(setting);
        } else {
            setting.setValue(new Boolean(selection).toString());
            settingDAO.store(setting);
        }
    }
    /**
     * {@inheritDoc}
     */
    public boolean isHourReportingEnabled() {
        Setting setting = settingDAO.getSetting(SETTING_NAME_HOUR_REPORTING);
        
        if (setting == null) {
            return false;
        }
        
        return setting.getValue().equals(SETTING_VALUE_TRUE);        
    }

    public void setProjectBurndown(String mode) {
        Setting setting = settingDAO.getSetting(SETTING_NAME_PROJECT_BURNDOWN);
        boolean selection = (mode != null && mode.equals(SETTING_VALUE_TRUE));
        if(setting == null) {
            setting = new Setting();
            setting.setName(SETTING_NAME_PROJECT_BURNDOWN);
            setting.setValue(new Boolean(selection).toString());
            settingDAO.create(setting);
        } else {
            setting.setValue(new Boolean(selection).toString());
            settingDAO.store(setting);
        }
    }
    /**
     * {@inheritDoc}
     */
    public boolean isProjectBurndownEnabled() {
        Setting setting = settingDAO.getSetting(SETTING_NAME_PROJECT_BURNDOWN);
        
        if (setting == null) {
            return false;
        }
        
        return setting.getValue().equals(SETTING_VALUE_TRUE);        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setRangeLow(String value) {      
        Setting setting = settingDAO.getSetting(SETTING_NAME_RANGE_LOW);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {
                setting.setValue(SETTING_DEFAULT_RANGE_LOW);
                settingDAO.store(setting);
            }
        }
        else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);    
            } catch(NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_RANGE_LOW);               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);                          
            }
            else {               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
        
       
    }
    
    /**
     * {@inheritDoc}
     */
    public int getRangeLow() {
        Setting setting = settingDAO.getSetting(SETTING_NAME_RANGE_LOW);
        
        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_RANGE_LOW) ;
        } else {      
            return Integer.parseInt(setting.getValue());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void setRangeHigh(String value) {
        Setting setting = settingDAO.getSetting(SETTING_NAME_RANGE_HIGH);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {               
                setting.setValue(SETTING_DEFAULT_RANGE_HIGH);
                settingDAO.store(setting);
            }
        }
        else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);    
            } catch(NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_RANGE_HIGH);               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);                          
            }
            else {               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getRangeHigh() {
        Setting setting = settingDAO.getSetting(SETTING_NAME_RANGE_HIGH);
        
        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_RANGE_HIGH) ;
        }       
        return Integer.parseInt(setting.getValue());  
    }
    
    /**
     * {@inheritDoc}
     */
    public void setOptimalLow(String value) {
        Setting setting = settingDAO.getSetting(SETTING_NAME_OPTIMAL_LOW);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {               
                setting.setValue(SETTING_DEFAULT_OPTIMAL_LOW);
                settingDAO.store(setting);
            }
        }
        else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);    
            } catch(NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_OPTIMAL_LOW);               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);                          
            }
            else {               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOptimalLow() {
        Setting setting = settingDAO.getSetting(SETTING_NAME_OPTIMAL_LOW);
        
        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_OPTIMAL_LOW) ;
        }       
        return Integer.parseInt(setting.getValue());
    }
    
    /**
     * {@inheritDoc}
     */
    public void setOptimalHigh(String value) {
        Setting setting = settingDAO.getSetting(SETTING_NAME_OPTIMAL_HIGH);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {                
                setting.setValue(SETTING_DEFAULT_OPTIMAL_HIGH);
                settingDAO.store(setting);
            }
        }
        else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);    
            } catch(NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_OPTIMAL_HIGH);               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);                          
            }
            else {               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOptimalHigh() {
        Setting setting = settingDAO.getSetting(SETTING_NAME_OPTIMAL_HIGH);
        
        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_OPTIMAL_HIGH) ;
        }       
        return Integer.parseInt(setting.getValue());
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCriticalLow(String value) {
        Setting setting = settingDAO.getSetting(SETTING_NAME_CRITICAL_LOW);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {              
                setting.setValue(SETTING_DEFAULT_CRITICAL_LOW);
                settingDAO.store(setting);
            }
        }
        else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);    
            } catch(NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_CRITICAL_LOW);               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);                          
            }
            else {               
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int getCriticalLow() {
        Setting setting = settingDAO.getSetting(SETTING_NAME_CRITICAL_LOW);
        
        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_CRITICAL_LOW) ;
        }       
        return Integer.parseInt(setting.getValue());
    }
    
    public Setting getSetting(String name) {
        return settingDAO.getSetting(name);
    }
}
