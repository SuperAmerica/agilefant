package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import fi.hut.soberit.agilefant.db.history.impl.AgilefantRevisionListener;

@Entity  
@RevisionEntity(AgilefantRevisionListener.class)
@Table(name="agilefant_revisions")
@XmlTransient
@XmlAccessorType( XmlAccessType.NONE )
public class AgilefantRevisionEntity extends DefaultRevisionEntity {
    
    private static final long serialVersionUID = 5256226401100437772L;
    
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
