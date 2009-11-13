package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Project;

@Service("portfolioBusiness")
@Transactional
public class PortfolioBusinessImpl implements PortfolioBusiness {

    @Autowired
    private ProjectDAO projectDAO;

    @Transactional(readOnly = true)
    public List<Project> getPortfolioData() {
        return projectDAO.getActiveProjectsSortedByRank();
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

}
