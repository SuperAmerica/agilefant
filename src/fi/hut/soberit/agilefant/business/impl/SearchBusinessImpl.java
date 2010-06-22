package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.SearchBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.NamedObject;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.SearchResultRow;

@Service("searchBusiness")
public class SearchBusinessImpl implements SearchBusiness {

    @Autowired
    private StoryDAO storyDAO;
    @Autowired
    private BacklogDAO backlogDAO;

    public List<SearchResultRow> searchStoriesAndBacklog(String searchTerm) {
        List<SearchResultRow> result = new ArrayList<SearchResultRow>();
        NamedObject quickRefMatch = this.searchByReference(searchTerm);
        if (quickRefMatch != null) {
            result.add(new SearchResultRow(quickRefMatch.getName(),
                    quickRefMatch));
        }
        List<Backlog> backlogs = backlogDAO.searchByName(searchTerm);
        for (Backlog bl : backlogs) {
            SearchResultRow item = new SearchResultRow();
            item.setOriginalObject(bl);
            if (bl.getParent() != null) {
                item.setLabel(bl.getParent().getName() + " > " + bl.getName());
            } else {
                item.setLabel(bl.getName());
            }
            result.add(item);
        }
        List<Story> stories = storyDAO.searchByName(searchTerm);
        for (Story story : stories) {
            result.add(new SearchResultRow(story.getBacklog().getName() + " > "
                    + story.getName(), story));
        }
        return result;
    }

    public NamedObject searchByReference(String searchTerm) {
        if (searchTerm == null) {
            return null;
        }

        String[] matches = searchTerm.split(":");
        int objectId;
        String type;
        if (matches.length != 2) {
            return null;
        }
        type = matches[0];

        try {
            objectId = Integer.parseInt(matches[1]);
        } catch (Exception e) {
            return null;
        }
        if (type.equals("story")) {
            return storyDAO.get(objectId);
        } else if (type.equals("backlog")) {
            return backlogDAO.get(objectId);
        }
        return null;
    }
}
