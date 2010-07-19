package fi.hut.soberit.agilefant.db.history;

import java.util.Set;

import fi.hut.soberit.agilefant.model.Iteration;

public interface IterationHistoryDAO extends GenericHistoryDAO<Iteration> {
    public Set<Integer> retrieveInitialTasks(Iteration iteration);
}
