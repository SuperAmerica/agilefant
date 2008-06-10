package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.BatchSize;

@Entity
@BatchSize(size=20)
public class BacklogItemHourEntry extends HourEntry {
    private BacklogItem backlogItem;

    @ManyToOne
    @JoinColumn(nullable = true)
    public BacklogItem getBacklogItem() {
        return backlogItem;
    }

    public void setBacklogItem(BacklogItem backlogItem) {
        this.backlogItem = backlogItem;
    }
}
