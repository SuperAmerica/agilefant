package fi.hut.soberit.agilefant.business.impl;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

@Service("storyBusiness")
@Transactional
public class StoryBusinessImpl extends GenericBusinessImpl<Story> implements
        StoryBusiness {

    private StoryDAO storyDAO;
    @Autowired
    private BacklogDAO backlogDAO;
    @Autowired
    private UserDAO userDAO;

    @Autowired
    public void setStoryDAO(StoryDAO storyDAO) {
        this.genericDAO = storyDAO;
        this.storyDAO = storyDAO;
    }
    
    public List<Story> getStoriesByBacklog(Backlog backlog) {
        return storyDAO.getStoriesByBacklog(backlog);
    }
    

    public Story store(int storyId, int backlogId, Story dataItem, Set<Integer> responsibles) throws ObjectNotFoundException {
        Story item = null; 
        if(storyId > 0) {
            item = storyDAO.get(storyId);
            if(item == null) {
                throw new ObjectNotFoundException("story.notFound");
            }
        }
        Backlog backlog = backlogDAO.get(backlogId);
        if(backlog == null) {
            throw new ObjectNotFoundException("backlog.notFound");
        }
          
        Set<User> responsibleUsers = new HashSet<User>();
        
        for(int userId : responsibles) {
            User responsible = userDAO.get(userId);
            if(responsible != null) {
                responsibleUsers.add(responsible);
            }
        }
        
        return this.store(item, backlog, dataItem, responsibleUsers);
    }

    public Story store(Story storable, Backlog backlog, Story dataItem, Set<User> responsibles) {

        boolean historyUpdated = false;
        
        if(backlog == null) {
            throw new IllegalArgumentException("Backlog must not be null.");
        }
        if(dataItem == null) {
            throw new IllegalArgumentException("No data given.");
        }
        if(storable == null) {
            storable = new Story();
//            storable.setCreatedDate(Calendar.getInstance().getTime());
            try {
                storable.setCreator(SecurityUtil.getLoggedUser()); //may fail if request is multithreaded
            } catch(Exception e) { } //however, saving item should not fail.
        }
        storable.setDescription(dataItem.getDescription());
//        storable.setEffortLeft(dataItem.getEffortLeft());
        storable.setName(dataItem.getName());
//        if(storable.getOriginalEstimate() == null) {
//            if(dataItem.getOriginalEstimate() == null) {
//                storable.setOriginalEstimate(dataItem.getEffortLeft());
//            } else { 
//                storable.setOriginalEstimate(dataItem.getOriginalEstimate());
//            }
//        }
        storable.setPriority(dataItem.getPriority());
        storable.setState(dataItem.getState());
        
//        if(dataItem.getState() == State.DONE) {
//            storable.setEffortLeft(new AFTime(0));
//        } else if(dataItem.getEffortLeft() == null) {
//            storable.setEffortLeft(storable.getOriginalEstimate());
//        }
        
        if(storable.getBacklog() != null && storable.getBacklog() != backlog) {
            this.moveStoryToBacklog(storable, backlog);
            historyUpdated = true;
        } else if(storable.getBacklog() == null) {
            storable.setBacklog(backlog);
        }
        
//        storable.setResponsibles(responsibles);
               
        Story persisted;
        
        if(storable.getId() == 0) {
            int persistedId = (Integer)storyDAO.create(storable);
            persisted = storyDAO.get(persistedId);
        } else {
            storyDAO.store(storable);
            persisted = storable;
        }
//        if(!historyUpdated) {
//            historyBusiness.updateBacklogHistory(backlog.getId());
//        }
        return persisted;
    }
    
    public void moveStoryToBacklog(Story story, Backlog backlog) {

        Backlog oldBacklog = story.getBacklog();
        oldBacklog.getStories().remove(story);
        story.setBacklog(backlog);
        backlog.getStories().add(story);
//        historyBusiness.updateBacklogHistory(oldBacklog.getId());
//        historyBusiness.updateBacklogHistory(backlog.getId());
        
//        if(!backlogBusiness.isUnderSameProduct(oldBacklog, backlog)) {
//            //remove only product themes
//            Collection<BusinessTheme> removeThese = new ArrayList<BusinessTheme>();;
//            for(BusinessTheme theme : story.getBusinessThemes()) {
//                if(!theme.isGlobal()) {
//                    removeThese.add(theme);
//                }
//            }
//            for(BusinessTheme theme : removeThese) {
//                story.getBusinessThemes().remove(theme);
//            }
//        }
    }
    
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
