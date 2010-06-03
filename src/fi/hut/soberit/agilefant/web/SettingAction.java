package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.SettingBusiness;

@Component("settingAction")
@Scope("prototype")
public class SettingAction extends ActionSupport {
    
    private static final long serialVersionUID = 6404856922329136680L;
    private int rangeLow = -1;
    private int rangeHigh = -1;
    private int optimalLow = -1;
    private int optimalHigh = -1;
    private int criticalLow = -1;
    private boolean hourReportingEnabled = false;
    private boolean dailyWorkEnabled = false;
    private boolean devPortfolioEnabled = false;
    private String storyTreeFieldOrder;
    
    @Autowired
    private SettingBusiness settingBusiness;
    
    public String edit()  {        
        this.criticalLow = this.settingBusiness.getCriticalLow();
        this.optimalHigh = this.settingBusiness.getOptimalHigh();
        this.optimalLow = this.settingBusiness.getOptimalLow();
        this.rangeHigh = this.settingBusiness.getRangeHigh();
        this.rangeLow = this.settingBusiness.getRangeLow();
        this.hourReportingEnabled = this.settingBusiness.isHourReportingEnabled();
        this.devPortfolioEnabled = this.settingBusiness.isDevPortfolio();
        this.dailyWorkEnabled = this.settingBusiness.isDailyWork();
        this.storyTreeFieldOrder = this.settingBusiness.getStoryTreeFieldOrder();
        return Action.SUCCESS;
    }
    
    public String storeSettings() {
        this.initilizeEmptyLoadMeterValues();
        if (!this.validateLoadMeterValues()) {
            return Action.ERROR;
        }
        settingBusiness.setRangeLow(rangeLow);
        settingBusiness.setRangeHigh(rangeHigh);
        settingBusiness.setOptimalLow(optimalLow);
        settingBusiness.setOptimalHigh(optimalHigh);
        settingBusiness.setCriticalLow(criticalLow);
        settingBusiness.setHourReporting(hourReportingEnabled);
        settingBusiness.setDevPortfolio(devPortfolioEnabled);
        settingBusiness.setDailyWork(dailyWorkEnabled);
        settingBusiness.setStoryTreeFieldOrder(storyTreeFieldOrder);
        return Action.SUCCESS;
    }
    
    public void initilizeEmptyLoadMeterValues() {
        if(rangeLow < 0) {
            rangeLow = SettingBusiness.DEFAULT_RANGE_LOW;
        }
        if(rangeHigh < 0) {
            rangeHigh = SettingBusiness.DEFAULT_RANGE_HIGH;
        }
        if(optimalLow < 0) {
            optimalLow = SettingBusiness.DEFAULT_OPTIMAL_LOW;
        }
        if(optimalHigh < 0) {
            optimalHigh = SettingBusiness.DEFAULT_OPTIMAL_HIGH;
        }
        if(criticalLow < 0) {
            criticalLow = SettingBusiness.DEFAULT_CRITICAL_LOW;
        }
    }
    
    public boolean validateLoadMeterValues() {
        if(optimalLow <= rangeLow) {
            this.addActionError(this.getText("settings.rangeLowErr"));
            return false;
        }
        if(optimalHigh <= optimalLow) {
            this.addActionError(this.getText("settings.optimalLowErr"));
            return false;
        }
        if(criticalLow <= optimalHigh) {
            this.addActionError(this.getText("settings.optimalHighErr"));
            return false;
        }
        if(rangeHigh <= criticalLow) {
            this.addActionError(this.getText("settings.criticalErr"));
            return false;
        }
        return true;
    }

    public int getRangeLow() {
        return rangeLow;
    }

    public void setRangeLow(int rangeLow) {
        this.rangeLow = rangeLow;
    }

    public int getRangeHigh() {
        return rangeHigh;
    }

    public void setRangeHigh(int rangeHigh) {
        this.rangeHigh = rangeHigh;
    }

    public int getOptimalLow() {
        return optimalLow;
    }

    public void setOptimalLow(int optimalLow) {
        this.optimalLow = optimalLow;
    }

    public int getOptimalHigh() {
        return optimalHigh;
    }

    public void setOptimalHigh(int optimalHigh) {
        this.optimalHigh = optimalHigh;
    }

    public int getCriticalLow() {
        return criticalLow;
    }

    public void setCriticalLow(int criticalLow) {
        this.criticalLow = criticalLow;
    }

    public boolean isHourReportingEnabled() {
        return hourReportingEnabled;
    }

    public void setHourReportingEnabled(boolean hourReportingEnabled) {
        this.hourReportingEnabled = hourReportingEnabled;
    }

    public boolean isDailyWorkEnabled() {
        return dailyWorkEnabled;
    }

    public void setDailyWorkEnabled(boolean dailyWorkEnabled) {
        this.dailyWorkEnabled = dailyWorkEnabled;
    }

    public boolean isDevPortfolioEnabled() {
        return devPortfolioEnabled;
    }

    public void setDevPortfolioEnabled(boolean devPortfolioEnabled) {
        this.devPortfolioEnabled = devPortfolioEnabled;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

    public String getStoryTreeFieldOrder() {
        return storyTreeFieldOrder;
    }

    public void setStoryTreeFieldOrder(String storyTreeFieldOrder) {
        this.storyTreeFieldOrder = storyTreeFieldOrder;
    }
    
}
