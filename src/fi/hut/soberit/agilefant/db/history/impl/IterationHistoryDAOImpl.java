package fi.hut.soberit.agilefant.db.history.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.history.IterationHistoryDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

@Repository("iterationHistoryDAO")
public class IterationHistoryDAOImpl extends GenericHistoryDAOImpl<Iteration>
        implements IterationHistoryDAO {

    public IterationHistoryDAOImpl() {
        super(Iteration.class);
    }

    @SuppressWarnings("unchecked")
    public Set<Integer> retrieveInitialTasks(Iteration iteration) {
        Set<Integer> result = new HashSet<Integer>();

        // fetch story ids
        AuditQuery storyQuery = this.getAuditReader().createQuery()
                .forRevisionsOfEntity(Story.class, true, false);
        storyQuery.add(AuditEntity.revisionProperty("timestamp").le(
                iteration.getStartDate().getMillis()));
        storyQuery.add(AuditEntity.relatedId("backlog").eq(iteration.getId()));
        storyQuery.addProjection(AuditEntity.property("id").distinct());
        Set<Integer> storyIds = new HashSet<Integer>();

        List<Map<String, Object>> res = storyQuery.getResultList();

        for (Map<String, Object> row : res) {
            storyIds.add((Integer) row.get("id"));
        }

        // fetch task ids
        AuditQuery query = this.getAuditReader().createQuery()
                .forRevisionsOfEntity(Task.class, true, false);
        query.add(AuditEntity.revisionProperty("timestamp").le(
                iteration.getStartDate().getMillis()));
        if (storyIds.isEmpty()) {
            query.add(AuditEntity.relatedId("iteration").eq(iteration.getId()));
        } else {
            query.add(AuditEntity.or(AuditEntity.property("iteration_id").eq(
                    iteration.getId()), AuditEntity.property("story_id").in(
                    storyIds)));
        }
        query.addProjection(AuditEntity.property("id").distinct());
        List<Map<String, Object>> rows = query.getResultList();

        for (Map<String, Object> row : rows) {
            result.add((Integer) row.get("id"));
        }
        return result;
    }

}
