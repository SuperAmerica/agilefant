package fi.hut.soberit.agilefant.db.hibernate.event;

import java.util.Calendar;

import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;

import fi.hut.soberit.agilefant.model.TaskEvent;

public class TaskEventEventListener implements PreInsertEventListener {

	private static final long serialVersionUID = -4249542180184709345L;

	public boolean onPreInsert(PreInsertEvent event) {
		if (event.getEntity() instanceof TaskEvent){
			TaskEvent taskEvent = (TaskEvent)event.getEntity();
			taskEvent.setCreated(Calendar.getInstance().getTime());			
		}
		return false;
	}
}
