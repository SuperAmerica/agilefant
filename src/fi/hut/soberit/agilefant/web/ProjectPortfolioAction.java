package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.transfer.PortfolioTO;

@Component("projectPortfolioAction")
@Scope("prototype")
public class ProjectPortfolioAction implements ContextAware{

    @Autowired
    private PortfolioBusiness portfolioBusiness;

    @Autowired
    private WidgetCollectionBusiness widgetCollectionBusiness;
    
    private List<WidgetCollection> allCollections;
    
    private PortfolioTO portfolioData = new PortfolioTO();

    public String retrieve() {
        allCollections = widgetCollectionBusiness.getAllCollections();
        return Action.SUCCESS;
    }

    public String portfolioData() {
        portfolioData = portfolioBusiness.getPortfolioData();
        return Action.SUCCESS;
    }

    public PortfolioTO getPortfolioData() {
        return portfolioData;
    }

    public void setPortfolioBusiness(PortfolioBusiness portfolioBusiness) {
        this.portfolioBusiness = portfolioBusiness;
    }

    public String getContextName() {
        return "portfolio";
    }

    public int getContextObjectId() {
        return 0;
    }

    public List<WidgetCollection> getAllCollections() {
        return allCollections;
    }

}
