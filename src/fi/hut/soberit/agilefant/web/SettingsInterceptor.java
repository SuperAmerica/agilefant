package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import fi.hut.soberit.agilefant.business.SettingBusiness;


@Component("settingsInterceptor")
public class SettingsInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -8858944388573607020L;

    @Autowired
    private SettingBusiness settingBusiness;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        invocation.getStack().set("settings", settingBusiness);
        return invocation.invoke();
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

}
