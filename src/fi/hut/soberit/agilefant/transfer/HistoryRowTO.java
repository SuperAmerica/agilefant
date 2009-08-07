package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.AgilefantRevisionEntity;

public class HistoryRowTO {
    AgilefantRevisionEntity revision;
    Object model;
    
    public HistoryRowTO(AgilefantRevisionEntity revision, Object model) {
        this.revision = revision;
        this.model = model;
    }

    public AgilefantRevisionEntity getRevision() {
        return revision;
    }

    public Object getModel() {
        return model;
    }
}
