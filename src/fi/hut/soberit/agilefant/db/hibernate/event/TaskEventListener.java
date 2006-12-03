package fi.hut.soberit.agilefant.db.hibernate.event;

import java.util.Calendar;

import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class TaskEventListener implements PreInsertEventListener{

	private static final long serialVersionUID = -7708348428244828439L;

	public boolean onPreInsert(PreInsertEvent event) {
		if (event.getEntity() instanceof Task){
			Task task = (Task)event.getEntity();
			task.setCreated(Calendar.getInstance().getTime());
		}
		return false;
	}
}
