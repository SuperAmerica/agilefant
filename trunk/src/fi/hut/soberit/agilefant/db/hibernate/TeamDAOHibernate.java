package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.model.Team;

public class TeamDAOHibernate extends GenericDAOHibernate<Team> implements
        TeamDAO {
    
    public TeamDAOHibernate() {
        super(Team.class);
    }
}
