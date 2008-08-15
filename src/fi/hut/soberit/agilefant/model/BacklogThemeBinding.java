package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

@BatchSize(size=20)
@Table(name = "backlogthemebinding")
@Entity
public class BacklogThemeBinding {
    private int id;
    private boolean relativeBinding = false;
    private Float percentage;
    private AFTime fixedSize;
    private Backlog backlog;
    private BusinessTheme businessTheme;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(nullable = false, columnDefinition = "boolean default 0")
    public boolean isRelativeBinding() {
        return relativeBinding;
    }
    
    public void setRelativeBinding(boolean relativeBinding) {
        this.relativeBinding = relativeBinding;
    }
    @Column(nullable = true)
    public Float getPercentage() {
        return percentage;
    }
    
    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }
    
    @Type(type = "af_time")
    @Column(nullable = true)
    public AFTime getFixedSize() {
        return fixedSize;
    }
    
    public void setFixedSize(AFTime fixedSize) {
        this.fixedSize = fixedSize;
    }
    
    @ManyToOne(targetEntity = fi.hut.soberit.agilefant.model.Backlog.class)
    @Fetch(FetchMode.JOIN)
    public Backlog getBacklog() {
        return backlog;
    }
    
    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }
    
    @ManyToOne(targetEntity = fi.hut.soberit.agilefant.model.BusinessTheme.class)
    @Fetch(FetchMode.JOIN)
    public BusinessTheme getBusinessTheme() {
        return businessTheme;
    }

    public void setBusinessTheme(BusinessTheme businessTheme) {
        this.businessTheme = businessTheme;
    }
    
    @Transient
    public AFTime getBoundEffort()
    {
        if(isRelativeBinding()) {
            if(getBacklog().getBacklogSize() == null || getPercentage() == null) {
                return new AFTime(0);
            }
            return new AFTime(java.lang.Math.round(getPercentage() * (float)(getBacklog().getBacklogSize()*36)));
        } else {
            return getFixedSize();
        }
    }
}
