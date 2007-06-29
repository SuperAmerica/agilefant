package fi.hut.soberit.agilefant.web;

import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.model.Portfolio;
import fi.hut.soberit.agilefant.service.PortfolioManager;

public class PortfolioAction extends ActionSupport {
	
	private static final long serialVersionUID = -4749839976470627112L;
	private PortfolioManager portfolioManager;
	private Portfolio portfolio;
	private Date startDate;
	private Date endDate;
	
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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
}
