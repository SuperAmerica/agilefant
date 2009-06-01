package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.Setting;

/**
 * Business implementation for handling of settings
 * 
 * @author kjniiran
 * 
 */
@Service("settingBusiness")
@Transactional
public class SettingBusinessImpl extends GenericBusinessImpl<Setting> implements
        SettingBusiness {

    private SettingDAO settingDAO;

    @Autowired
    public void setSettingDAO(SettingDAO settingDAO) {
        this.genericDAO = settingDAO;
        this.settingDAO = settingDAO;
    }

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

    @Transactional(readOnly = true)
    public Setting retrieveByName(String name) {
        return settingDAO.getByName(name);
    }
    
    @Transactional(readOnly = true)
    public boolean isHourReportingEnabled() {
        Setting setting = settingDAO.getByName(SETTING_NAME_HOUR_REPORTING);

        if (setting == null) {
            return false;
        }

        return setting.getValue().equals(SETTING_VALUE_TRUE);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public List<Setting> getAllOrderByName() {
        return (List<Setting>) settingDAO.getAllOrderByName();
    }

    @Transactional
    public void setHourReporting(String mode) {
        Setting setting = settingDAO.getByName(SETTING_NAME_HOUR_REPORTING);
        boolean selection = (mode != null && mode.equals(SETTING_VALUE_TRUE));
        if (setting == null) {
            setting = new Setting();
            setting.setName(SETTING_NAME_HOUR_REPORTING);
            setting.setValue(new Boolean(selection).toString());
            settingDAO.create(setting);
        } else {
            setting.setValue(new Boolean(selection).toString());
            settingDAO.store(setting);
        }
    }

    @Transactional
    public void setProjectBurndown(String mode) {
        Setting setting = settingDAO.getByName(SETTING_NAME_PROJECT_BURNDOWN);
        boolean selection = (mode != null && mode.equals(SETTING_VALUE_TRUE));
        if (setting == null) {
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
    @Transactional(readOnly = true)
    public boolean isProjectBurndownEnabled() {
        Setting setting = settingDAO.getByName(SETTING_NAME_PROJECT_BURNDOWN);

        if (setting == null) {
            return false;
        }

        return setting.getValue().equals(SETTING_VALUE_TRUE);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setRangeLow(String value) {
        Setting setting = settingDAO.getByName(SETTING_NAME_RANGE_LOW);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {
                setting.setValue(SETTING_DEFAULT_RANGE_LOW);
                settingDAO.store(setting);
            }
        } else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_RANGE_LOW);
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            } else {
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getRangeLow() {
        Setting setting = settingDAO.getByName(SETTING_NAME_RANGE_LOW);

        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_RANGE_LOW);
        } else {
            return Integer.parseInt(setting.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setRangeHigh(String value) {
        Setting setting = settingDAO.getByName(SETTING_NAME_RANGE_HIGH);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {
                setting.setValue(SETTING_DEFAULT_RANGE_HIGH);
                settingDAO.store(setting);
            }
        } else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_RANGE_HIGH);
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            } else {
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getRangeHigh() {
        Setting setting = settingDAO.getByName(SETTING_NAME_RANGE_HIGH);

        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_RANGE_HIGH);
        }
        return Integer.parseInt(setting.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setOptimalLow(String value) {
        Setting setting = settingDAO.getByName(SETTING_NAME_OPTIMAL_LOW);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {
                setting.setValue(SETTING_DEFAULT_OPTIMAL_LOW);
                settingDAO.store(setting);
            }
        } else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_OPTIMAL_LOW);
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            } else {
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getOptimalLow() {
        Setting setting = settingDAO.getByName(SETTING_NAME_OPTIMAL_LOW);

        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_OPTIMAL_LOW);
        }
        return Integer.parseInt(setting.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setOptimalHigh(String value) {
        Setting setting = settingDAO.getByName(SETTING_NAME_OPTIMAL_HIGH);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {
                setting.setValue(SETTING_DEFAULT_OPTIMAL_HIGH);
                settingDAO.store(setting);
            }
        } else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_OPTIMAL_HIGH);
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            } else {
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getOptimalHigh() {
        Setting setting = settingDAO.getByName(SETTING_NAME_OPTIMAL_HIGH);

        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_OPTIMAL_HIGH);
        }
        return Integer.parseInt(setting.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void setCriticalLow(String value) {
        Setting setting = settingDAO.getByName(SETTING_NAME_CRITICAL_LOW);
        // if value is null, restore default value.
        if (value == null) {
            if (setting != null) {
                setting.setValue(SETTING_DEFAULT_CRITICAL_LOW);
                settingDAO.store(setting);
            }
        } else {
            Integer intValue = null;
            try {
                intValue = Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                return;
            }
            if (setting == null) {
                setting = new Setting();
                setting.setName(SETTING_NAME_CRITICAL_LOW);
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            } else {
                setting.setValue(intValue.toString());
                settingDAO.store(setting);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    public int getCriticalLow() {
        Setting setting = settingDAO.getByName(SETTING_NAME_CRITICAL_LOW);

        if (setting == null) {
            return Integer.parseInt(SETTING_DEFAULT_CRITICAL_LOW);
        }
        return Integer.parseInt(setting.getValue());
    }

}
