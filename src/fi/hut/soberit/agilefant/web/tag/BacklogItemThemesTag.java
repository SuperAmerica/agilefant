package fi.hut.soberit.agilefant.web.tag;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;

public class BacklogItemThemesTag extends SpringTagSupport {
        
    private static final long serialVersionUID = -7678732585895121348L;
    private int backlogItemId;
    private String positionId;
    private String actualPositionId;
    private boolean editBacklogItemPage;
    private BacklogItemDAO backlogItemDAO;
    private BusinessThemeBusiness businessThemeBusiness;
    
    @Override
    public int doStartTag() throws JspException {
        backlogItemDAO = (BacklogItemDAO) super.getApplicationContext()
            .getBean("backlogItemDAO");
        businessThemeBusiness = (BusinessThemeBusiness) super.getApplicationContext()
            .getBean("businessThemeBusiness");

        BacklogItem bli = backlogItemDAO.get(backlogItemId);
        if (positionId != null) {
            actualPositionId = positionId;            
        } else {
            actualPositionId = "" + bli.getId();
        }
        // System.out.println("postion: " + actualPositionId);
       
        String printString = "<a href=\"#\" id=\"" + actualPositionId + "\" " +
        		"onclick=\"openThemeBusinessModal('" + actualPositionId + "', 'editBacklogItemBusinessThemes.action'," + bli.getId() + ", 0," + bli.getProduct().getId() + "); return false;\">" +
                                "<img src=\"static/img/add_theme.png\" alt=\"Edit themes\" title=\"Edit themes\" />";
        
        List<BusinessTheme> themes = businessThemeBusiness.getBacklogItemActiveBusinessThemes(backlogItemId);
        // add the "none" text only in edit bli page
        if (editBacklogItemPage && themes.size() == 0) {
            printString += " none";                        
        }
        printString += "</a>";
        for (BusinessTheme theme : themes) {
            printString += "<span class=\"businessTheme\" title=\"" + theme.getDescription() + "\">" +
                            "<a href=\"#\" id=\"" + bli.getId() + "\" onclick=\"openThemeBusinessModal('" + actualPositionId + "', 'editBacklogItemBusinessThemes.action', " + bli.getId() + "," +  theme.getId() + ", " + bli.getProduct().getId() + "); return false;\">" +
                            theme.getName() +
                            "</a></span>";
        }                
        
        try {
            super.getPageContext().getOut().print(printString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness businessThemeBusiness) {
        this.businessThemeBusiness = businessThemeBusiness;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public void setEditBacklogItemPage(boolean editBacklogItemPage) {
        this.editBacklogItemPage = editBacklogItemPage;
    }

}
