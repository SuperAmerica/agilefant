package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * A spring test case for testing the Backlog business layer.
 * @author rjokelai
 *
 */
public class BacklogBusinessTest extends SpringTestCase {

    private BacklogBusiness backlogBusiness;
    
    private BacklogItemDAO backlogItemDAO;
    
    private BacklogDAO backlogDAO;
        
    /**
     * Test changing the priority of multiple <code>BacklogItem</code>s.
     */
    public void testChangePriorityOfMultipleItems() throws Exception{
        
        /* Generate the test data */
        Backlog backlog = new Project();
        
        backlog = backlogDAO.get(backlogDAO.create(backlog));
        
        
        BacklogItem bli1 = new BacklogItem();
        bli1.setPriority(Priority.BLOCKER);
        bli1.setBacklog(backlog);
        int bli1id = (Integer)backlogItemDAO.create(bli1);
        bli1 = backlogItemDAO.get(bli1id);
        backlog.getBacklogItems().add(bli1);
        
        BacklogItem bli2 = new BacklogItem();
        bli2.setPriority(Priority.MINOR);
        bli2.setBacklog(backlog);
        int bli2id = (Integer)backlogItemDAO.create(bli2);
        bli2 = backlogItemDAO.get(bli2id);
        backlog.getBacklogItems().add(bli2);
        
        BacklogItem bli3 = new BacklogItem();
        bli3.setBacklog(backlog);
        int bli3id = (Integer)backlogItemDAO.create(bli3);
        bli3 = backlogItemDAO.get(bli3id);
        backlog.getBacklogItems().add(bli3);
        
        BacklogItem bli4 = new BacklogItem();
        bli4.setPriority(Priority.MAJOR);
        bli4.setBacklog(backlog);
        int bli4id = (Integer)backlogItemDAO.create(bli4);
        bli4 = backlogItemDAO.get(bli4id);
        backlog.getBacklogItems().add(bli4);
        
        
        /* Test */
        int ids[] = {bli1id, bli2id, bli3id};
        
        backlogBusiness.changePriorityOfMultipleItems(ids, Priority.BLOCKER);
        
        /* Assertions */
        assertEquals("BacklogItem 1 has wrong priority.",
                Priority.BLOCKER, bli1.getPriority());
        assertEquals("BacklogItem 2 has wrong priority.",
                Priority.BLOCKER, bli2.getPriority());
        assertEquals("BacklogItem 3 has wrong priority.",
                Priority.BLOCKER, bli3.getPriority());
        assertEquals("BacklogItem 4 has wrong priority.",
                Priority.MAJOR, bli4.getPriority());
        
    }

    
    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }


    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }


    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }
    
}
