package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogHistoryEntryDAO;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

@Service("backlogHistoryEntryBusiness")
public class BacklogHistoryEntryBusinessImpl extends
        GenericBusinessImpl<BacklogHistoryEntry> implements
        BacklogHistoryEntryBusiness {

    private BacklogHistoryEntryDAO backlogHistoryEntryDAO;
    private StoryHierarchyDAO storyHierarchyDAO;

    @Autowired
    private BacklogDAO backlogDAO;

    public BacklogHistoryEntryBusinessImpl() {
        super(BacklogHistoryEntry.class);
    }

    @Autowired
    public void setBacklogHistoryEntryDAO(
            BacklogHistoryEntryDAO backlogHistoryEntryDAO) {
        this.backlogHistoryEntryDAO = backlogHistoryEntryDAO;
        this.genericDAO = backlogHistoryEntryDAO;
    }

    @Transactional
    public void updateHistory(int backlogId) {
        Backlog backlog = backlogDAO.get(backlogId);
        Project project = null;
        if (backlog instanceof Iteration) {
            if (backlog.isStandAlone()) {
                DateTime currentTime = new DateTime();
                BacklogHistoryEntry entry = backlogHistoryEntryDAO.retrieveLatest(
                        currentTime, backlog.getId());
                if (entry == null || entry.getTimestamp().isBefore(
                        currentTime.minus(BacklogHistoryEntryBusiness.UPDATE_INTERVAL))) {
                    entry = new BacklogHistoryEntry();
                }
                entry.setTimestamp(new DateTime());
                entry.setDoneSum(storyHierarchyDAO.totalLeafDoneStoryPoints((Project)backlog));
                entry.setEstimateSum(storyHierarchyDAO.totalLeafStoryPoints((Project)backlog));
                entry.setRootSum(storyHierarchyDAO.totalRootStoryPoints((Project)backlog));
                entry.setBacklog(backlog);
                backlogHistoryEntryDAO.store(entry);
                return;
            }
            project = (Project) backlog.getParent();
        } else if (backlog instanceof Product) {
            return;
        } else {
            project = (Project) backlog;
        }
        DateTime currentTime = new DateTime();
        BacklogHistoryEntry entry = backlogHistoryEntryDAO.retrieveLatest(
                currentTime, project.getId());
        // if an existing entry is within the set interval update that entry,
        // else create a new one
        if (entry == null || entry.getTimestamp().isBefore(
                currentTime.minus(BacklogHistoryEntryBusiness.UPDATE_INTERVAL))) {
            entry = new BacklogHistoryEntry();
        }

        entry.setTimestamp(new DateTime());
        entry.setDoneSum(storyHierarchyDAO.totalLeafDoneStoryPoints(project));
        entry.setEstimateSum(storyHierarchyDAO.totalLeafStoryPoints(project));
        entry.setRootSum(storyHierarchyDAO.totalRootStoryPoints(project));
        entry.setBacklog(project);
        backlogHistoryEntryDAO.store(entry);
    }

    @Transactional(readOnly = true)
    public List<BacklogHistoryEntry> retrieveForTimestamps(
            List<DateTime> timestamps, int projectId) {
        List<BacklogHistoryEntry> result = new ArrayList<BacklogHistoryEntry>();
        for (DateTime timestamp : timestamps) {
            BacklogHistoryEntry entry = backlogHistoryEntryDAO.retrieveLatest(
                    timestamp, projectId);
            if (entry == null) {
                entry = new BacklogHistoryEntry();
                entry.setTimestamp(timestamp);
            }
            result.add(entry);
        }
        return result;
    }

    @Autowired
    public void setStoryHierarchyDAO(StoryHierarchyDAO storyHierarchyDAO) {
        this.storyHierarchyDAO = storyHierarchyDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

}
