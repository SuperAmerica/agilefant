package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.model.Portfolio;
import fi.hut.soberit.agilefant.service.PortfolioManager;

public class PortfolioAction extends ActionSupport {
	
	private PortfolioManager portfolioManager;
	private Portfolio portfolio;
		
	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void setPortfolioManager(PortfolioManager portfolioManager) {
		this.portfolioManager = portfolioManager;
	}

	public String execute(){
		portfolio = portfolioManager.getCurrentPortfolio();
		return Action.SUCCESS;
	}
}
