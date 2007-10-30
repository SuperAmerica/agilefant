package fi.hut.soberit.agilefant.web;

import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

//import fi.hut.soberit.agilefant.model.Portfolio;
//import fi.hut.soberit.agilefant.service.PortfolioManager;

public class DevelopmentPortfolioAction extends ActionSupport {
	
	private static final long serialVersionUID = -4749839976470627112L;
	
	private String hello = "Hello, here project rank tweaking.";

	public String getHello() {
		return hello;
	}
	
	@Override
	public String execute() throws Exception {
		hello = "Hello, here's rank tweaking.";
		return super.execute();
	}

}
