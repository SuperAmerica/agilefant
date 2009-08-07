package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import fi.hut.soberit.agilefant.db.history.impl.AgilefantRevisionListener;

@Entity  
@RevisionEntity(AgilefantRevisionListener.class)
@Table(name="agilefant_revisions")
public class AgilefantRevisionEntity extends DefaultRevisionEntity {
    private String userName = "";
    private int userId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
}
