package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.SearchBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.NamedObject;

@Service("searchBusiness")
public class SearchBusinessImpl implements SearchBusiness {

    @Autowired
    private StoryDAO storyDAO;
    @Autowired
    private BacklogDAO backlogDAO;
    
    public List<NamedObject> searchStoriesAndBacklog(String searchTerm) {
        List<NamedObject> result = new ArrayList<NamedObject>();
        NamedObject quickRefMatch = this.searchByReference(searchTerm);
        if(quickRefMatch != null) {
            result.add(quickRefMatch);
        }
        
        result.addAll(backlogDAO.searchByName(searchTerm));
        result.addAll(storyDAO.searchByName(searchTerm));
        return result;
    }

    public NamedObject searchByReference(String searchTerm) {
        String[] matches = searchTerm.split(":");
        int objectId;
        String type;
        if(matches.length != 2) {
            return null;
        }
        type = matches[0];

        try {
            objectId = Integer.parseInt(matches[1]);
        } catch (Exception e) {
            return null;
        }
        if(type.equals("story")) {
            return storyDAO.get(objectId);
        } else if(type.equals("backlog")) {
            return backlogDAO.get(objectId);
        } 
        return null;
    }
}
