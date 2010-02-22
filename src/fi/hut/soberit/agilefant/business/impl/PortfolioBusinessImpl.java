package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.PortfolioTO;
import fi.hut.soberit.agilefant.transfer.ProjectTO;

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
        List<Project> rankedProjects = new ArrayList<Project>();
        for (Project project : projectDAO.getRankedProjects(startDate, endDate)) {
            ProjectTO to = new ProjectTO(project);
            Set<User> assignees = new HashSet<User>();
            for (Assignment assignment : to.getAssignments()) {
                assignees.add(assignment.getUser());
            }
            to.setAssignees(assignees);
            rankedProjects.add(to);
        }
        portfolioData.setRankedProjects(rankedProjects);
        Set<Project> unrankedProjects = new HashSet<Project>();
        for (Project project : projectDAO.getUnrankedProjects(startDate,
                endDate)) {
            ProjectTO to = new ProjectTO(project);
            Set<User> assignees = new HashSet<User>();
            for (Assignment assignment : to.getAssignments()) {
                assignees.add(assignment.getUser());
            }
            to.setAssignees(assignees);
            unrankedProjects.add(to);
        }
        portfolioData.setUnrankedProjects(unrankedProjects);
        portfolioData.setTimeSpanInDays(Days.daysBetween(startDate, endDate)
                .getDays());
        return portfolioData;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

}
