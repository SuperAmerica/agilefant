package fi.hut.soberit.agilefant.web;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.HourEntry;

@Component("hourEntryAction")
@Scope("prototype")
public class HourEntryAction extends ActionSupport implements CRUDAction {
    private static final long serialVersionUID = -3817350069919875136L;

    private int hourEntryId;
    private int parentObjectId;
    private HourEntry hourEntry;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    private Set<Integer> userIds = new HashSet<Integer>();
    
    /**
     * {@inheritDoc}
     */
    public String create() {
        hourEntryId = 0;
        hourEntry = new HourEntry();
        hourEntry.setDate(new DateTime());
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String delete() {
        hourEntry = hourEntryBusiness.retrieve(hourEntryId);
        hourEntryBusiness.delete(hourEntryId);
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String retrieve() {
        hourEntry = hourEntryBusiness.retrieve(hourEntryId);
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc} 
     */
    public String store() {
        hourEntryBusiness.store(hourEntry);
        return Action.SUCCESS;
    }
    
    public String logStoryEffort() {
        this.hourEntryBusiness.logStoryEffort(this.parentObjectId,
                this.hourEntry, this.userIds);
        return Action.SUCCESS;
    }
    
    public String logTaskEffort() {
        this.hourEntryBusiness.logTaskEffort(this.parentObjectId, this.hourEntry, this.userIds);
        return Action.SUCCESS;
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

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public void setParentObjectId(int parentObjectId) {
        this.parentObjectId = parentObjectId;
    }

}
