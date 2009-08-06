package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import fi.hut.soberit.agilefant.db.hibernate.AgilefantRevisionListener;

@Entity  
@RevisionEntity(AgilefantRevisionListener.class)
@Table(name="agilefant_revisions")
public class AgilefantRevisionEntity extends DefaultRevisionEntity {
    private String userName = "";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
}
