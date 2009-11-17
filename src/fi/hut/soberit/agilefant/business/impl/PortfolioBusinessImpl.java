package fi.hut.soberit.agilefant.business.impl;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.transfer.PortfolioTO;

@Service("portfolioBusiness")
@Transactional
public class PortfolioBusinessImpl implements PortfolioBusiness {

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private SettingBusiness settingBusiness;

    @Transactional(readOnly = true)
    public PortfolioTO getPortfolioData() {
        PortfolioTO portfolioData = new PortfolioTO();
        LocalDate startDate = new LocalDate();
        LocalDate endDate = startDate.plus(settingBusiness
                .getPortfolioTimeSpan());
        portfolioData.setRankedProjects(projectDAO.getRankedProjects(startDate,
                endDate));
        portfolioData.setUnrankedProjects(projectDAO.getUnrankedProjects(
                startDate, endDate));
        return portfolioData;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

}
