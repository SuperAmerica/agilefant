package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.Setting;

/**
 * Business implementation for handling of settings
 * 
 * @author kjniiran
 * @author Pasi Pekkanen
 * 
 */
@Service("settingBusiness")
@Transactional
@Scope(value="singleton")
public class SettingBusinessImpl extends GenericBusinessImpl<Setting> implements
        SettingBusiness {
    
    //setting keys
    public static final String SETTING_NAME_HOUR_REPORTING = "HourReporting";
    public static final String SETTING_NAME_RANGE_LOW = "RangeLow";
    public static final String SETTING_NAME_RANGE_HIGH = "RangeHigh";
    public static final String SETTING_NAME_OPTIMAL_LOW = "OptimalLow";
    public static final String SETTING_NAME_OPTIMAL_HIGH = "OptimalHigh";
    public static final String SETTING_NAME_CRITICAL_LOW = "CriticalLow";
    public static final String SETTING_NAME_PORTFOLIO_TIME_SPAN = "PortfolioTimeSpan";

    public SettingBusinessImpl() {
        super(Setting.class);
    }

    @Autowired
    private SettingDAO settingDAO;
    private Map<String,Setting> settingCache = new HashMap<String, Setting>();

    
    public void setSettingDAO(SettingDAO settingDAO) {
        this.genericDAO = settingDAO;
        this.settingDAO = settingDAO;
    }
    
    @PostConstruct
    public void loadSettingCache() {
        this.settingCache.clear();
        Collection<Setting> allSettings = this.settingDAO.getAll();
        for(Setting setting : allSettings) {
            this.settingCache.put(setting.getName(), setting);
        }
    }

    @Transactional(readOnly = true)
    public Setting retrieveByName(String name) {
        return this.settingCache.get(name);
    }
    
    public void storeSetting(String settingName, boolean value) {
        this.storeSetting(settingName, ((Boolean)value).toString());
    }
    
    public void storeSetting(String settingName, int value) {
        this.storeSetting(settingName, ((Integer)value).toString());
    }
    
    public synchronized void storeSetting(String settingName, String value) {
        Setting setting = this.retrieveByName(settingName);
        if (setting == null) {
            setting = new Setting();
            setting.setName(settingName);
            setting.setValue(value);
            this.settingDAO.create(setting);
        } else {
            setting.setValue(value);
            this.settingDAO.store(setting);
        } 
        this.settingCache.put(settingName, setting);
    }
    
    @Transactional(readOnly = true)
    public boolean isHourReportingEnabled() {
        Setting setting = this.retrieveByName(SETTING_NAME_HOUR_REPORTING);

        if (setting == null) {
            return false;
        }

        return setting.getValue().equals("true");
    }

    @Transactional
    public void setHourReporting(boolean mode) {
        this.storeSetting(SETTING_NAME_HOUR_REPORTING, mode);

    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setRangeLow(Integer value) {
        if(value == null) {
            this.storeSetting(SETTING_NAME_RANGE_LOW, DEFAULT_RANGE_LOW);
        } else {
            this.storeSetting(SETTING_NAME_RANGE_LOW, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getRangeLow() {
        Setting setting = this.retrieveByName(SETTING_NAME_RANGE_LOW);

        if (setting == null) {
            return DEFAULT_RANGE_LOW;
        } else {
            return Integer.parseInt(setting.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setRangeHigh(Integer value) {
        if(value == null) {
            this.storeSetting(SETTING_NAME_RANGE_HIGH, DEFAULT_RANGE_HIGH);
        } else {
            this.storeSetting(SETTING_NAME_RANGE_HIGH, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getRangeHigh() {
        Setting setting = this.retrieveByName(SETTING_NAME_RANGE_HIGH);

        if (setting == null) {
            return DEFAULT_RANGE_HIGH;
        }
        return Integer.parseInt(setting.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setOptimalLow(Integer value) {
        if(value == null) {
            this.storeSetting(SETTING_NAME_OPTIMAL_LOW, DEFAULT_OPTIMAL_LOW);
        } else {
            this.storeSetting(SETTING_NAME_OPTIMAL_LOW, value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getOptimalLow() {
        Setting setting = this.retrieveByName(SETTING_NAME_OPTIMAL_LOW);

        if (setting == null) {
            return DEFAULT_OPTIMAL_LOW;
        }
        return Integer.parseInt(setting.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setOptimalHigh(Integer value) {
        if(value == null) {
            this.storeSetting(SETTING_NAME_OPTIMAL_HIGH, DEFAULT_OPTIMAL_HIGH);
        } else {
            this.storeSetting(SETTING_NAME_OPTIMAL_HIGH, value);            
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getOptimalHigh() {
        Setting setting = this.retrieveByName(SETTING_NAME_OPTIMAL_HIGH);

        if (setting == null) {
            return DEFAULT_OPTIMAL_HIGH;
        }
        return Integer.parseInt(setting.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setCriticalLow(Integer value) {
        if(value == null) {
            this.storeSetting(SETTING_NAME_CRITICAL_LOW, value);
        } else {
            this.storeSetting(SETTING_NAME_CRITICAL_LOW, DEFAULT_CRITICAL_LOW);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getCriticalLow() {
        Setting setting = this.retrieveByName(SETTING_NAME_CRITICAL_LOW);

        if (setting == null) {
            return DEFAULT_CRITICAL_LOW;
        }
        return Integer.parseInt(setting.getValue());
    }
    
    
    @Transactional(readOnly = true)
    public Period getPortfolioTimeSpan() {
        Setting setting = this.retrieveByName(SETTING_NAME_PORTFOLIO_TIME_SPAN);
        
        if(setting == null) {
            return DEFAULT_PORTFOLIO_TIME_SPAN; 
        }
        return Period.months(Integer.parseInt(setting.getValue()));
        
    }
    
    @Transactional(readOnly = true)
    public void setPortfolioTimeSpan(Period timeSpan) {
        if( timeSpan == null) {
            this.storeSetting(SETTING_NAME_PORTFOLIO_TIME_SPAN, Integer.toString(DEFAULT_PORTFOLIO_TIME_SPAN.getMonths()));
        } else {
            this.storeSetting(SETTING_NAME_PORTFOLIO_TIME_SPAN, Integer.toString(timeSpan.getMonths()));
        }
    }

}
