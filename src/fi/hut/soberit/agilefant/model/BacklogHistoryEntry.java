package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "history_backlogs")
@XmlAccessorType( XmlAccessType.NONE )
public class BacklogHistoryEntry {

    private int id;

    private Backlog backlog;

    private long estimateSum;

    private long doneSum;
    
    private long rootSum;

    private DateTime timestamp;

    @Id
    @GeneratedValue
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne
    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    public long getEstimateSum() {
        return estimateSum;
    }

    public void setEstimateSum(long estimateSum) {
        this.estimateSum = estimateSum;
    }

    public long getDoneSum() {
        return doneSum;
    }

    public void setDoneSum(long doneSum) {
        this.doneSum = doneSum;
    }

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Column(nullable=false)
    public long getRootSum() {
        return rootSum;
    }
    
    public void setRootSum(long rootSum) {
        this.rootSum = rootSum;
    }

}
