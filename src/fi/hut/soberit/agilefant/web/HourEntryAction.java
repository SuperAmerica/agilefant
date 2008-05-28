package fi.hut.soberit.agilefant.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;


public class HourEntryAction extends ActionSupport implements CRUDAction {
    private static final long serialVersionUID = -3817350069919875136L;
    private int hourEntryId;
    private HourEntry hourEntry;
    private HourEntryBusiness hourEntryBusiness;
    private HourEntryDAO hourEntryDAO;
    private int[] selectedUserIds;
    private TimesheetLoggable target;
    
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
        HourEntry h = hourEntryDAO.get(hourEntryId);
        if (h == null) {
            super.addActionError(super.getText("hourEntry.notFound"));
            return Action.ERROR;
        }
        hourEntryDAO.remove(hourEntryId);
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String edit() {
        hourEntry = hourEntryDAO.get(hourEntryId);
        if (hourEntry == null) {
            super.addActionError(super.getText("hourEntry.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     * TODO: check that target is valid
     */
    public String store() {
        HourEntry storable = new HourEntry();
        if (hourEntryId > 0) {
            storable = hourEntryDAO.get(hourEntryId);
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
         
        if(hourEntryId == 0) {
            hourEntryBusiness.addHourEntryForMultipleUsers(storable, selectedUserIds);
        }
        hourEntryDAO.store(storable);
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
        storable.setDate(this.hourEntry.getDate());
        storable.setDescription(this.hourEntry.getDescription());
        storable.setTargetId(this.hourEntry.getTargetId());
        storable.setTargetType(this.hourEntry.getTargetType());
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

    public HourEntryDAO getHourEntryDAO() {
        return hourEntryDAO;
    }

    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
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

}
