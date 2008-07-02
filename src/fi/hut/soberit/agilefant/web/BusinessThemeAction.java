package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;

public class BusinessThemeAction extends ActionSupport implements CRUDAction {
    
    private static final long serialVersionUID = -8978527111144555643L;

    private int businessThemeId;
    
    private int backlogItemId;

    private BusinessTheme businessTheme;

    private BusinessThemeDAO businessThemeDAO;
    
    private BacklogItemDAO backlogItemDAO;

    private Collection<BusinessTheme> businessThemes;
    
    private BusinessThemeBusiness businessThemeBusiness;
    
    public String create() {
        businessThemeId = 0;
        businessTheme = new BusinessTheme();
        return Action.SUCCESS;
    }
    
    public String edit() {
        businessTheme = businessThemeDAO.get(businessThemeId);
        if (businessTheme == null) {
            super.addActionError(super.getText("businessTheme.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    
    public String store() {
        if (businessTheme == null) {
            super.addActionError(super.getText("businessTheme.missingForm"));
        }
        BusinessTheme fillable = new BusinessTheme();
        if (businessThemeId > 0) {
            fillable = businessThemeDAO.get(businessThemeId);
            if (fillable == null) {
                super.addActionError(super.getText("businessTheme.notFound"));
                return Action.ERROR;
            }
        }
        this.fillObject(fillable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        try {
            businessThemeDAO.store(fillable);
        } catch (DataIntegrityViolationException dve) {
            super.addActionError(super.getText("businessTheme.duplicateName"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    
    public String delete() {
        businessTheme = businessThemeDAO.get(businessThemeId);
        if (businessTheme == null) {
            super.addActionError(super.getText("businessTheme.notFound"));
            return Action.ERROR;
        }
        /*
        for (BacklogItem bli : backlogItemDAO.getAll()) {
            // käy vielä läpi bli:n kaikki teemat...
            if (bli.getTheme().getId() == themeId) {
                super.addActionError(super
                        .getText("theme.backlogItemsLinked"));
                return Action.ERROR;
            }
        }
        */
        try {
            businessThemeBusiness.delete(businessThemeId);
        }
        catch (ObjectNotFoundException e) {
            super.addActionError(e.getMessage());
            return Action.ERROR;
        }
        
        return Action.SUCCESS;
    }

    public String editBacklogItemBusinessThemes() {
        
        BacklogItem bli;
        if (backlogItemId > 0) {
            bli = backlogItemDAO.get(backlogItemId);
            if (bli == null) {
                super.addActionError(super.getText("backlogItem.notFound"));
                return Action.ERROR;
            } else {
                businessThemes = bli.getBusinessThemes();
            }
        }                
                
        return Action.SUCCESS;
    }
    
    public String storeBacklogItemBusinessThemes() {
        
        BacklogItem bli;
        if (backlogItemId > 0) {
            bli = backlogItemDAO.get(backlogItemId);
        
            if (bli == null) {
                super.addActionError(super.getText("backlogItem.notFound"));
                return Action.ERROR;
            } else {
                bli.setBusinessThemes(businessThemes);
                backlogItemDAO.store(bli);
                
                // testausta
                System.out.println("*** tallennettiin itemin " + bli.getName() + " teemat. ***");
            }
        }                
                
        return Action.SUCCESS;
             
    }
    
    protected void fillObject(BusinessTheme fillable) {
        if (businessTheme.getName() == null || 
                businessTheme.getName().trim().equals("")) {
            super.addActionError(super.getText("businessTheme.nameEmpty"));
            return;
        }
        fillable.setName(businessTheme.getName());
        fillable.setDescription(businessTheme.getDescription());
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

    public Collection<BusinessTheme> getBusinessThemes() {
        return businessThemes;
    }

    public void setBusinessThemes(Collection<BusinessTheme> businessThemes) {
        this.businessThemes = businessThemes;
    }

    public BusinessThemeBusiness getBusinessThemeBusiness() {
        return businessThemeBusiness;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness businessThemeBusiness) {
        this.businessThemeBusiness = businessThemeBusiness;
    }

    public void setBusinessThemeDAO(BusinessThemeDAO businessThemeDAO) {
        this.businessThemeDAO = businessThemeDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }
    
}
