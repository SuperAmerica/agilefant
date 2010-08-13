package fi.hut.soberit.agilefant.model;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlAccessorType( XmlAccessType.NONE )
@XmlTransient   
public class HolidayAnomaly {
    private int id;
    private User user;
    private Date date;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    @ManyToOne
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    
}
