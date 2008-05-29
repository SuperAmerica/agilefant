package fi.hut.soberit.agilefant.web;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import java.text.ParseException;


public class HourEntryAction extends ActionSupport implements CRUDAction {
    private static final long serialVersionUID = -3817350069919875136L;
    private int hourEntryId;
    private HourEntry hourEntry;
    private HourEntryBusiness hourEntryBusiness;
    private BacklogItemDAO backlogItemDAO;
    private int[] selectedUserIds;
    private TimesheetLoggable target;
    private Date date;
    private int backlogId;
    private int backlogItemId;
    
    private Log logger = LogFactory.getLog(getClass());

    /**
     * {@inheritDoc}
     */
    public String create() {
        hourEntryId = 0;
        hourEntry = new HourEntry();
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String delete() {
        HourEntry h = hourEntryBusiness.getId(hourEntryId);
        if (h == null) {
            super.addActionError(super.getText("hourEntry.notFound"));
            return Action.ERROR;
        }
        hourEntryBusiness.remove(hourEntryId);
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String edit() {
        hourEntry = hourEntryBusiness.getId(hourEntryId);
        if (hourEntry == null) {
            super.addActionError(super.getText("hourEntry.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    private TimesheetLoggable getParent() {
        TimesheetLoggable parent = null;
        if(backlogItemId > 0) {
            parent = backlogItemDAO.get(backlogItemId);
        }
        return parent;
    }
    /**
     * {@inheritDoc}
     * TODO: check that target is valid
     */
    public String store() {
        HourEntry storable = new HourEntry();
        if (hourEntryId > 0) {
            storable = hourEntryBusiness.getId(hourEntryId);
            if (storable == null) {
                super.addActionError(super.getText("hourEntry.notFound"));
                return Action.ERROR;
            }
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        if (selectedUserIds == null) {
            selectedUserIds = new int[1];
            selectedUserIds[0] = storable.getUser().getId();
        }
        
        
        //Existing entries cannot be "shared"
        TimesheetLoggable parent = getParent();
        
        if(hourEntryId == 0) {
            hourEntryBusiness.addHourEntryForMultipleUsers(parent,storable, selectedUserIds);
        }
        hourEntryBusiness.store(parent,storable);
        return determinateReturnPage();
    }
    
    protected String determinateReturnPage() {
        if(this.target instanceof BacklogItem) {
            return "backlogItem";
        } else if(this.target instanceof Iteration) {
            return "iteration";
        } else {
            return Action.SUCCESS;
        }
    }
    protected void fillStorable(HourEntry storable) {
        storable.setDate(this.date);
        storable.setDescription(this.hourEntry.getDescription());
        storable.setTimeSpent(this.hourEntry.getTimeSpent());
        storable.setUser(this.hourEntry.getUser());
    }

    public int getHourEntryId() {
        return hourEntryId;
    }

    public void setHourEntryId(int hourEntryId) {
        this.hourEntryId = hourEntryId;
    }

    public HourEntry getHourEntry() {
        return hourEntry;
    }

    public void setHourEntry(HourEntry hourEntry) {
        this.hourEntry = hourEntry;
    }

    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public int[] getSelectedUserIds() {
        return selectedUserIds;
    }

    public void setSelectedUserIds(int[] selectedUserIds) {
        this.selectedUserIds = selectedUserIds;
    }

    public TimesheetLoggable getTarget() {
        return target;
    }

    public void setTarget(TimesheetLoggable parent) {
        this.target = parent;
    }

    public Date getDate() {
        return hourEntry.getDate();
    }

    public void setDate(String date) {
        try {
            this.date = hourEntryBusiness.formatDate(date);
        } catch(ParseException e) {
            //TODO: How to handle?
        }
    }

    public int getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

}
