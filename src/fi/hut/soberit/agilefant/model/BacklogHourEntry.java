package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.BatchSize;

@Entity
@BatchSize(size=20)
public class BacklogHourEntry extends HourEntry {

        private Backlog backlog;

        @ManyToOne
        @JoinColumn(nullable = true)
        public Backlog getBacklog() {
            return backlog;
        }

        public void setBacklog(Backlog backlog) {
            this.backlog = backlog;
        }
}
