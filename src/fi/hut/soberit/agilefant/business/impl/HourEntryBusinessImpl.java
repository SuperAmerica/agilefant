package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.StoryHourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;

@Service("hourEntryBusiness")
@Transactional
public class HourEntryBusinessImpl extends GenericBusinessImpl<HourEntry>
        implements HourEntryBusiness {

    private HourEntryDAO hourEntryDAO;

    @Autowired
    private StoryHourEntryDAO storyHourEntryDAO;
    @Autowired
    private BacklogHourEntryDAO backlogHourEntryDAO;

    @Autowired
    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.genericDAO = hourEntryDAO;
        this.hourEntryDAO = hourEntryDAO;
    }

    public HourEntry store(TimesheetLoggable parent, HourEntry hourEntry)
            throws IllegalArgumentException {
        HourEntry storable = null;

        if (parent == null) {
            throw new IllegalArgumentException("Unknown parent type.");
        }
        if (hourEntry == null) {
            throw new IllegalArgumentException("No data given.");
        }
        if (parent instanceof Story) {
            if ((storable = storyHourEntryDAO.get(hourEntry.getId())) == null) {
                storable = new StoryHourEntry();
            }
            ((StoryHourEntry) storable).setStory((Story) parent);
        } else if (parent instanceof Backlog) {
            if ((storable = backlogHourEntryDAO.get(hourEntry.getId())) == null) {
                storable = new BacklogHourEntry();
            }
            ((BacklogHourEntry) storable).setBacklog((Backlog) parent);
        } else {
            throw new IllegalArgumentException("Unknown parent type.");
        }
        storable.setDate(hourEntry.getDate());
        storable.setUser(hourEntry.getUser());
        storable.setDescription(hourEntry.getDescription());
        storable.setMinutesSpent(hourEntry.getMinutesSpent());
        storable.setDate(hourEntry.getDate());
        if (parent instanceof Story) {
            storyHourEntryDAO.store((StoryHourEntry) storable);
        } else {
            backlogHourEntryDAO.store((BacklogHourEntry) storable);
        }
        return storable;
    }

}
