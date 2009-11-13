package fi.hut.soberit.agilefant.web;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.model.Project;

@Component("projectPortfolioAction")
@Scope("prototype")
public class ProjectPortfolioAction {

    @Autowired
    private PortfolioBusiness portfolioBusiness;
    
    private List<Project> projects = Collections.emptyList();

    public String retrieve() {
        return Action.SUCCESS;
    }
    
    public String portfolioData() {
        projects = portfolioBusiness.getPortfolioData();
        return Action.SUCCESS;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setPortfolioBusiness(PortfolioBusiness portfolioBusiness) {
        this.portfolioBusiness = portfolioBusiness;
    }
    
}
