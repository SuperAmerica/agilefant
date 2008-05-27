package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.SettingBusiness;


public class HourReportingTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729034L;

    private SettingBusiness settingBusiness;

    @Override
    public int doStartTag() throws JspException {
        settingBusiness = (SettingBusiness) super.getApplicationContext().getBean(
                "settingBusiness");
        
        super.getPageContext().setAttribute(super.getId(), 
                settingBusiness.isHourReportingEnabled());
        
        return Tag.EVAL_BODY_INCLUDE;
    }
}
