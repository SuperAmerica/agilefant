package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.model.Team;

@Repository("teamDAO")
public class TeamDAOHibernate extends GenericDAOHibernate<Team> implements
        TeamDAO {

    public TeamDAOHibernate() {
        super(Team.class);
    }

}
