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
        result.addAll(backlogDAO.searchByName(searchTerm));
        result.addAll(storyDAO.searchByName(searchTerm));
        return result;
    }
}
