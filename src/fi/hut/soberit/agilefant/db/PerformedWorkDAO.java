package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;

public interface PerformedWorkDAO {
	
	public Collection<PerformedWork> getPerformedWork(Task task);
	public Collection<PerformedWork> getPerformedWork(BacklogItem backlogItem);
	public Collection<PerformedWork> getPerformedWork(Iteration iteration);
	public Collection<PerformedWork> getPerformedWork(Deliverable task);
//	public Collection<PerformedWork> getPerformedWork(Product task);
}
