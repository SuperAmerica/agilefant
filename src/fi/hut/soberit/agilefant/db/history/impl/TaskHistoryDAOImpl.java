/**
 * Implementation of TaskHistoryDAO interface
 * 
 * @author arberborix
 * 
 */
package fi.hut.soberit.agilefant.db.history.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.history.TaskHistoryDAO;
import fi.hut.soberit.agilefant.model.AgilefantRevisionEntity;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;

@Repository("taskHistoryDAO")
public class TaskHistoryDAOImpl extends GenericHistoryDAOImpl<Iteration> 
                                implements TaskHistoryDAO {

    private Task task = new Task();
    
    public TaskHistoryDAOImpl() {
        super(Iteration.class);
    }

    /**
     * Retrieves all records in table 'tasks_AUD' for the given iteration.
     * 
     * @author aborici
     */
    @SuppressWarnings("unchecked")
    public List<AgilefantHistoryEntry> retrieveAllTaskRevisions(
            Iteration iteration) {
        
        List<AgilefantHistoryEntry> result = new ArrayList<AgilefantHistoryEntry>();
        
        // generate query on tasks_AUD:
        AuditQuery query = this.getAuditReader().createQuery()
                .forRevisionsOfEntity(Task.class, false, true);
        query.add(AuditEntity.property("iteration_id").eq(iteration.getId()));
   
      //row[0]
        query.addProjection(AuditEntity.revisionType());
      //row[1]
        query.addProjection(AuditEntity.property("name")); // tasks by name revisions
        
        // the following come from 'agilefant_revisions' table:
      //row[2]
        query.addProjection(AuditEntity.revisionNumber());
      //row[3]
        query.addProjection(AuditEntity.revisionProperty("timestamp"));
      //row[4]
        query.addProjection(AuditEntity.revisionProperty("userId"));
      //row[5]
        query.addProjection(AuditEntity.revisionProperty("userName"));

      //row[6]
        query.addProjection(AuditEntity.property("story_id"));
      
        List<Object[]> data = query.getResultList();

        // return result set:
        for (Object[] row : data) {
            // construct task:
            task.setName(row[1].toString());
            
            Story story = null;
            for(Story st : iteration.getAssignedStories()){
                if (row[6] == null) continue;
                if (st.getId() == ((Story) row[6]).getId()) {
                    story = st;
                    break;
                }
                    
            }
            
            task.setStory(story);
           
            
            AgilefantRevisionEntity rev = new AgilefantRevisionEntity();
            rev.setId((Integer)row[2]);
            rev.setTimestamp((Long)row[3]);
            rev.setUserId((Integer)row[4]);
            rev.setUserName((String)row[5]);
            
            result.add(new AgilefantHistoryEntry(task, rev, (RevisionType) row[0]));
        }
        
        return result;
    }

}
