package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.AssignmentBusiness;
import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.history.BacklogHistoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Schedulable;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
@Service("backlogBusiness")
@Transactional
public class BacklogBusinessImpl extends GenericBusinessImpl<Backlog> implements
        BacklogBusiness {

    private BacklogDAO backlogDAO;
    @Autowired
    private ProductDAO productDAO;
    @Autowired
    private AssignmentBusiness assignmentBusiness;
    @Autowired
    private StoryDAO storyDAO;
    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private BacklogHistoryDAO backlogHistoryDAO;

    public BacklogBusinessImpl() {
        super(Backlog.class);
    }
    
    @Autowired
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.genericDAO = backlogDAO;
        this.backlogDAO = backlogDAO;
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public int getNumberOfChildren(Backlog backlog) {
        return backlogDAO.getNumberOfChildren(backlog);
    }

    @Transactional(readOnly = true)
    public Collection<Backlog> getChildBacklogs(Backlog backlog) {
        Collection<Backlog> childBacklogs = new ArrayList<Backlog>();
        if (backlog == null) {
            childBacklogs.addAll(productDAO.getAll());
        } else {
            childBacklogs.addAll(backlog.getChildren());
        }

        return childBacklogs;
    }

    public Days daysLeftInSchedulableBacklog(Schedulable backlog) {
        DateTime currentTime = new DateTime();
        Interval backlogInterval = new Interval(backlog.getStartDate()
                .toDateMidnight(), backlog.getEndDate().toDateMidnight());
        if (backlog.getEndDate().isBeforeNow()) {
            return Days.days(0);
        }
        Interval tobacklogEnd = new Interval(currentTime.toDateMidnight(),
                backlog.getEndDate().toDateMidnight());
        Interval intersection = tobacklogEnd.overlap(backlogInterval);
        if (backlogInterval.toDurationMillis() == 0) {
            return Days.days(0);
        } 
        return Days.daysIn(intersection);
    }
    
    public float calculateBacklogTimeframePercentageLeft(Schedulable backlog) {
        Interval backlogInterval = new Interval(backlog.getStartDate()
                .toDateMidnight(), backlog.getEndDate().toDateMidnight());
        Days daysLeft = this.daysLeftInSchedulableBacklog(backlog);
        return (float) daysLeft.toStandardDuration().getMillis()
                / (float) backlogInterval.toDurationMillis();
    }

    @Transactional(readOnly = true)
    public int calculateStoryPointSum(int backlogId) {
        return backlogDAO.calculateStoryPointSum(backlogId);
    }

    public int calculateDoneStoryPointSum(int backlogId) {
        return backlogDAO.calculateDoneStoryPointSum(backlogId);
    }
    
    public void addAssignees(int backlogId, Set<Integer> userIds) {
        Backlog backlog = this.retrieve(backlogId);
        this.assignmentBusiness.addMultiple(backlog, userIds,
                SignedExactEstimate.ZERO, 100);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Product getParentProduct(Backlog backlog) {
        Backlog parent = backlog;
        while (!(parent instanceof Product)) {
            parent = parent.getParent();
        }
        return (Product)parent;
    }
    
    @Transactional(readOnly = true)
    public int getStoryPointSumByBacklog(Backlog backlog) {
        return storyDAO.getStoryPointSumByBacklog(backlog.getId());
    }
    
    /**
     * {@inheritDoc}
     */
    public List<Story> retrieveUnexpectedStories(Schedulable backlog) {
        List<AgilefantHistoryEntry> added = this.backlogHistoryDAO
                .retrieveAddedStories((Backlog)backlog);
        List<AgilefantHistoryEntry> deleted = this.backlogHistoryDAO
                .retrieveDeletedStories((Backlog)backlog);

        List<Integer> removedDuringIteration = new ArrayList<Integer>();
        for (AgilefantHistoryEntry entry : deleted) {
            if (entry.getRevisionDate().isAfter(backlog.getStartDate())
                    && entry.getRevisionDate().isBefore(backlog.getEndDate())) {
                removedDuringIteration.add(entry.getObjectId());
            }
        }

        List<Story> unexpectedStories = new ArrayList<Story>();
        for (AgilefantHistoryEntry entry : added) {
            if (entry.getRevisionDate().isAfter(backlog.getStartDate())
                    && entry.getRevisionDate().isBefore(backlog.getEndDate())
                    && !removedDuringIteration.contains(entry.getObjectId())) {
                unexpectedStories.add((Story) entry.getObject());
            }
        }
        
        // update stories to their current revisions if possible (the story has
        // not been deleted)
        List<Story> unexpected = new ArrayList<Story>();
        for (Story story : unexpectedStories) {
            Story current = this.storyBusiness.retrieveIfExists(story.getId());
            if (current != null) {
                unexpected.add(current);
            } else {
                unexpected.add(story);
            }
        }

        return unexpected;
    }
}
