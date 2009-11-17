package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.transfer.PortfolioTO;

@Component("projectPortfolioAction")
@Scope("prototype")
public class ProjectPortfolioAction {

    @Autowired
    private PortfolioBusiness portfolioBusiness;

    private PortfolioTO portfolioData = new PortfolioTO();

    public String retrieve() {
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

}
