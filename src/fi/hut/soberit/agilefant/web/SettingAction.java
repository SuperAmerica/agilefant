package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.model.Setting;

public class SettingAction extends ActionSupport implements CRUDAction {
    
    private static final long serialVersionUID = 6404856922329136680L;
    private int settingID;
    private String name;
    private String description;
    private String value;
    private Setting setting;
    private Log logger = LogFactory.getLog(getClass());
    private SettingBusiness settingBusiness;
    
    
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
        setting = settingBusiness.getSetting(settingID);
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
            if (storable == null) {
                super.addActionError(super.getText("setting.notFound"));
                return Action.ERROR;
            }
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        settingBusiness.store(storable);       
        return Action.SUCCESS;
    }
    
    /**
     * A helper method for setting all required parameters for storing a setting to the database
     * @param storable the setting to be stored
     */
    protected void fillStorable(Setting storable) {
        storable.setName(this.setting.getName());
        storable.setDescription(this.setting.getDescription());
        storable.setValue(this.setting.getValue());
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
    
    public void setSettingName(String name){
        this.name = name;
    }
    
    public String getSettingName(){
        return name;
    }
    
    public void setSettingDescription(String description) {
        this.description = description;
    }

    public String getSettingDescription() {
        return description;
    }
    
    public void setSettingValue(String value) {
        this.value = value;
    }
    
    public String getSettingValue(){
        return value;
    }
}
