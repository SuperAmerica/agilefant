package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;

public interface EstimateHistoryDAO {

	public Collection<EstimateHistoryEvent> getEstimateHistory(Task task);

	public Collection<EstimateHistoryEvent> getEstimateHistory(
			BacklogItem backlogItem);

	public Collection<EstimateHistoryEvent> getEstimateHistory(
			Iteration iteration);

	public Collection<EstimateHistoryEvent> getEstimateHistory(
			Deliverable deliverable);
}
