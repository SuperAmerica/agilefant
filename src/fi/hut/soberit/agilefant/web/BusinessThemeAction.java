package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BusinessTheme;

public class BusinessThemeAction extends ActionSupport implements CRUDAction {
    
    private static final long serialVersionUID = -8978527111144555643L;

    private int businessThemeId;

    private BusinessTheme businessTheme;

    private Collection<BusinessTheme> businessThemes = new ArrayList<BusinessTheme>();
    
    private BusinessThemeBusiness businessThemeBusiness;
    
    private int backlogItemId;
    
    public String create() {
        businessThemeId = 0;
        businessTheme = new BusinessTheme();
        return Action.SUCCESS;
    }
    
    public String list() {
        businessThemes = businessThemeBusiness.getAll();
        return Action.SUCCESS;
    }
    
    public String edit() {
        businessTheme = businessThemeBusiness.getBusinessTheme(businessThemeId);
        if (businessTheme == null) {
            super.addActionError(super.getText("businessTheme.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
  
    public String store() {
        if (businessTheme == null) {
            super.addActionError(super.getText("businessTheme.missingForm"));
            return Action.ERROR;
        }
        BusinessTheme fillable = new BusinessTheme();
        this.fillObject(fillable);
        try {
            businessThemeBusiness.store(businessThemeId, fillable);
        } catch(ObjectNotFoundException e) {
            super.addActionError(super.getText("businessTheme.notFound"));
            return Action.ERROR;
        } catch(Exception e) {
            super.addActionError(super.getText(e.getMessage()));
            return Action.ERROR;
        }
        
        return Action.SUCCESS;
    }
    
    public String ajaxStoreBusinessTheme() {
        if (businessTheme == null) {
            super.addActionError(super.getText("businessTheme.missingForm"));
            return CRUDAction.AJAX_ERROR;
        }
        BusinessTheme fillable = new BusinessTheme();
        this.fillObject(fillable);
        try {
            fillable.setName(java.net.URLDecoder.decode(fillable.getName(), "ISO-8859-1"));
            fillable.setDescription(java.net.URLDecoder.decode(fillable.getDescription(), "ISO-8859-1"));
        } catch(Exception e) {}
        try {
            BusinessTheme theme = businessThemeBusiness.store(businessThemeId, fillable);
            businessThemeId = theme.getId();
        } catch(Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }
    public String delete() {
        try {
            businessThemeBusiness.delete(businessThemeId);
        }
        catch (Exception e) {
            super.addActionError(e.getMessage());
            return Action.ERROR;
        }
        
        return Action.SUCCESS;
    }
    
    protected void fillObject(BusinessTheme fillable) {
        if (businessTheme.getName() == null || 
                businessTheme.getName().trim().equals("")) {
            super.addActionError(super.getText("businessTheme.nameEmpty"));
            return;
        } else if (businessTheme.getName().length() > 20) {
            super.addActionError(super.getText("businessTheme.nameTooLong"));
            return;
        }
        fillable.setName(businessTheme.getName().trim());
        fillable.setDescription(businessTheme.getDescription());
    }
    
    public String editBacklogItemBusinessThemes() {
        businessThemes = businessThemeBusiness.getAll();
        return Action.SUCCESS;
    }
    public int getBusinessThemeId() {
        return businessThemeId;
    }

    public void setBusinessThemeId(int businessThemeId) {
        this.businessThemeId = businessThemeId;
    }

    public BusinessTheme getBusinessTheme() {
        return businessTheme;
    }

    public void setBusinessTheme(BusinessTheme businessTheme) {
        this.businessTheme = businessTheme;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness businessThemeBusiness) {
        this.businessThemeBusiness = businessThemeBusiness;
    }

    public Collection<BusinessTheme> getBusinessThemes() {
        return businessThemes;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }
    
}
