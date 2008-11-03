package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BusinessTheme;

public class BusinessThemeAction extends ActionSupport implements CRUDAction {
    
    private static final long serialVersionUID = -8978527111144555643L;

    private int businessThemeId;  
    
    private int productId;
    
    private BusinessTheme businessTheme;

    private Collection<BusinessTheme> businessThemes = new ArrayList<BusinessTheme>();
    
    private Collection<BusinessTheme> bliBusinessThemes = new ArrayList<BusinessTheme>();
    
    private BusinessThemeBusiness businessThemeBusiness;        
    
    private int backlogItemId;
    
    private int bindingId;
    
    private int backlogId;
    
    private int[] businessThemeIds = new int[1];
    
    private String[] plannedSpendings;
    
    private String jsonData = "";
    

    public String create() {
        businessThemeId = 0;
        businessTheme = new BusinessTheme();
        businessTheme.setActive(true);
        return Action.SUCCESS;
    }
    
    public String list() {
        businessThemes = businessThemeBusiness.getActiveBusinessThemes(productId);
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
        
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        
        try {
            businessThemeBusiness.store(businessThemeId, productId, fillable);
        } catch(ObjectNotFoundException e) {
            super.addActionError(super.getText("businessTheme.notFound"));
            return Action.ERROR;
        } catch (DataIntegrityViolationException nve) {
            super.addActionError(super.getText("businessTheme.duplicateName"));
            return Action.ERROR;
        } catch(Exception e) {
            super.addActionError(super.getText("An error occurred."));
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
                       
       /* try {
            businessTheme.setName(java.net.URLDecoder.decode(businessTheme.getName(), "ISO-8859-1"));
            businessTheme.setDescription(java.net.URLDecoder.decode(businessTheme.getDescription(), "ISO-8859-1"));            
        } catch(Exception e) {}
        */
        this.fillObject(fillable);
        
        if (super.hasActionErrors()) {
            return CRUDAction.AJAX_ERROR;
        } 
        try {
            BusinessTheme theme = businessThemeBusiness.store(businessThemeId, productId, fillable);
            businessThemeId = theme.getId();
        } catch(Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String ajaxActivateBusinessTheme() {
        try {
            businessThemeBusiness.activateBusinessTheme(businessThemeId);
        } catch (ObjectNotFoundException e) {
           return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }
    public String ajaxDeactivateBusinessTheme() {
        try {
            businessThemeBusiness.deactivateBusinessTheme(businessThemeId);
        } catch (ObjectNotFoundException e) {
           return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS; 
    }
    
    public String ajaxDeleteBusinessTheme() {
        try {
            businessThemeBusiness.delete(businessThemeId);
        }
        catch (Exception e) {            
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
        } else if (businessTheme.getName().trim().length() > 20) {
            super.addActionError(super.getText("businessTheme.nameTooLong"));
            return;
        }
        fillable.setName(businessTheme.getName().trim());
        fillable.setDescription(businessTheme.getDescription());
        fillable.setActive(businessTheme.isActive());
    }
    
    public String editBacklogItemBusinessThemes() {
        if (backlogItemId > 0) {
            bliBusinessThemes = businessThemeBusiness.getBacklogItemActiveBusinessThemes(backlogItemId);
        }
        return list();       
    }
    
    public String storeThemeBinding() {
        if(businessThemeIds != null && backlogId > 0 && plannedSpendings != null) {
            businessThemeBusiness.multipleAddOrUpdateThemeToBacklog(businessThemeIds, backlogId, plannedSpendings);
        }
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String storeBacklogItemThemes() {
        if(businessThemeIds != null && backlogItemId > 0) {
            businessThemeBusiness.addMultipleThemesToBacklogItem(businessThemeIds, backlogItemId);
        }
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String removeThemeBinding() {
        if(bindingId > 0) {
            businessThemeBusiness.removeThemeBinding(bindingId);
            return CRUDAction.AJAX_SUCCESS;
        } else {
            return CRUDAction.AJAX_ERROR;
        }
    }
    
    public String themesByProduct() {
        jsonData = businessThemeBusiness.getThemesForProductAsJSON(productId);
        return Action.SUCCESS;
    }
    
    public String activeThemesByBacklog() {
        jsonData = businessThemeBusiness.getActiveThemesForBacklogAsJSON(backlogId);
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Collection<BusinessTheme> getBliBusinessThemes() {
        return bliBusinessThemes;
    }    
    public void setBindingId(int bindingId) {
        this.bindingId = bindingId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }
    
    public int getBacklogId() {
        return this.backlogId;
    }

    public void setPlannedSpendings(String[] plannedSpendings) {
        this.plannedSpendings = plannedSpendings;
    }
    
    public void setBusinessThemeIds(int[] businessThemeIds) {
        this.businessThemeIds = businessThemeIds;
    }

    public String getJsonData() {
        return jsonData;
    }
}
