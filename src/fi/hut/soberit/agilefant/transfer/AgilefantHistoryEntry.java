package fi.hut.soberit.agilefant.transfer;

import org.hibernate.envers.RevisionType;
import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.AgilefantRevisionEntity;
import fi.hut.soberit.agilefant.model.NamedObject;

public class AgilefantHistoryEntry {
    private NamedObject object;
    private AgilefantRevisionEntity revision;
    private RevisionType revisionType;
    private int objectId;
    
    public AgilefantHistoryEntry(int objectId, RevisionType revisionType, AgilefantRevisionEntity revision) {
        this.objectId = objectId;
        this.revisionType = revisionType;
        this.revision = revision;
    }
    
    public AgilefantHistoryEntry(NamedObject object, AgilefantRevisionEntity revision, RevisionType revisionType) {
        this.object = object;
        this.revision = revision;
        this.revisionType = revisionType;
        this.objectId = object.getId();
    }
    
    public NamedObject getObject() {
        return object;
    }

    public AgilefantRevisionEntity getRevision() {
        return revision;
    }

    public RevisionType getRevisionType() {
        return revisionType;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObject(NamedObject object) {
        this.object = object;
    }
    
    public DateTime getRevisionDate() {
        return new DateTime(this.revision.getRevisionDate());
    }
}
