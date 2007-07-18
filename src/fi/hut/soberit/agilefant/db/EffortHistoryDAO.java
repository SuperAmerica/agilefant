package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.EffortHistory;
import java.sql.Date;

/**
 * Interface for a DAO of a EffortHistory data model
 * 
 * @see GenericDAO
 */
public interface EffortHistoryDAO extends GenericDAO<EffortHistory>{
	public EffortHistory getByDate(Date date);
}
