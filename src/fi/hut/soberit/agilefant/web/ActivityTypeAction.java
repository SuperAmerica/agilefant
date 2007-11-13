package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Deliverable;

public class ActivityTypeAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = 1342432127514974396L;

    private int activityTypeId;

    private ActivityType activityType;

    private ActivityTypeDAO activityTypeDAO;

    private DeliverableDAO deliverableDAO;

    private Collection<ActivityType> activityTypes;

    public String getAll() {
        activityTypes = activityTypeDAO.getAll();
        return Action.SUCCESS;
    }

    public String create() {
        activityTypeId = 0;
        activityType = new ActivityType();
        return Action.SUCCESS;
    }

    public String edit() {
        activityType = activityTypeDAO.get(activityTypeId);
        if (activityType == null) {
            super.addActionError(super.getText("activityType.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public String store() {
        if (activityType == null) {
            super.addActionError(super.getText("activityType.missingForm"));
        }
        ActivityType fillable = new ActivityType();
        if (activityTypeId > 0) {
            fillable = activityTypeDAO.get(activityTypeId);
            if (fillable == null) {
                super.addActionError(super.getText("activityType.notFound"));
                return Action.ERROR;
            }
        }
        this.fillObject(fillable);
        activityTypeDAO.store(fillable);
        return Action.SUCCESS;
    }

    public String delete() {
        activityType = activityTypeDAO.get(activityTypeId);
        if (activityType == null) {
            super.addActionError(super.getText("activityType.notFound"));
            return Action.ERROR;
        }
        for (Deliverable d : deliverableDAO.getAll()) {
            if (d.getActivityType().getId() == activityTypeId) {
                super.addActionError(super
                        .getText("activityType.deliverablesLinked"));
                return Action.ERROR;
            }
        }
        activityTypeDAO.remove(activityType);
        return Action.SUCCESS;
    }

    protected void fillObject(ActivityType fillable) {
        fillable.setName(activityType.getName());
        fillable.setDescription(activityType.getDescription());
    }

    public int getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public Collection<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypeDAO(ActivityTypeDAO activityTypeDAO) {
        this.activityTypeDAO = activityTypeDAO;
    }

    public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
        this.deliverableDAO = deliverableDAO;
    }

}