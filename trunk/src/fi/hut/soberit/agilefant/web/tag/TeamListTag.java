package fi.hut.soberit.agilefant.web.tag;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.model.Team;

public class TeamListTag extends SpringTagSupport {
    private static final long serialVersionUID = 8356132939350106553L;

    public static final String TEAM_LIST_KEY = "teamList";

    private TeamDAO teamDAO;

    @Override
    public int doStartTag() throws JspException {
        teamDAO = (TeamDAO) super.getApplicationContext().getBean("teamDAO");

        List<Team> list = (List<Team>) teamDAO.getAll();

        //Collections.sort(list, new UserComparator());

        super.getPageContext().setAttribute(TeamListTag.TEAM_LIST_KEY, list);
        return Tag.EVAL_BODY_INCLUDE;
    }
}
