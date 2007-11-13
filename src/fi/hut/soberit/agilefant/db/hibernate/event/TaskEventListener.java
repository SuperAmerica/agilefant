package fi.hut.soberit.agilefant.db.hibernate.event;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import org.antlr.stringtemplate.StringTemplate;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

/**
 * Hibernate listener used to set Task date when one is created. Also sends
 * email notifications when task is created or updated. Notifications are sent
 * only when properties of task are modified thus triggering update event in
 * Hibernate.
 */

public class TaskEventListener implements PreInsertEventListener,
		PostInsertEventListener, PostUpdateEventListener {

	private static final long serialVersionUID = -7708348428244828439L;

	private SimpleMailMessage createTemplate;

	private SimpleMailMessage updateTemplate;

	private MailSender mailSender;

	public void onPostInsert(PostInsertEvent event) {
		if (event.getEntity() instanceof Task) {
			this.notify((Task) event.getEntity(), createTemplate);
		}
	}

	public void onPostUpdate(PostUpdateEvent event) {
		if (event.getEntity() instanceof Task) {
			this.notify((Task) event.getEntity(), updateTemplate);
		}
	}

	private void notify(Task task, SimpleMailMessage template) {
		if (template == null) {
			return;
		}
		SimpleMailMessage mail = new SimpleMailMessage(template);

		StringTemplate subjectTemplate = new StringTemplate(mail.getSubject());
		subjectTemplate.setAttribute("task", task);
		mail.setSubject(subjectTemplate.toString());

		Collection<String> recipients = new HashSet<String>();

		if (mail.getBcc() != null) {
			for (String bcc : mail.getBcc()) {
				recipients.add(bcc);
			}
		}

		for (User watcher : task.getWatchers().values()) {
			if (hasEmail(watcher)) {
				recipients.add(watcher.getEmail());
			}
		}

		if (hasEmail(task.getCreator())) {
			mail.setFrom(task.getCreator().getEmail());
			recipients.add(task.getCreator().getEmail());
		}

		if (task.getAssignee() != null && this.hasEmail(task.getAssignee())) {
			recipients.add(task.getAssignee().getEmail());
		}

		String[] bcc = recipients.toArray(new String[recipients.size()]);

		mail.setBcc(bcc);

		StringTemplate textTemplate = new StringTemplate(mail.getText());
		textTemplate.setAttribute("task", task);
		mail.setText(textTemplate.toString());

		// if (mailSender != null && recipients.size() > 0){
		// mailSender.send(mail);
		// }
	}

	public boolean onPreInsert(PreInsertEvent event) {
		if (event.getEntity() instanceof Task) {
			Task task = (Task) event.getEntity();
			task.setCreated(Calendar.getInstance().getTime());
		}
		return false;
	}

	private boolean hasEmail(User user) {
		return (user.getEmail() != null && user.getEmail().length() > 0);
	}

	public void setCreateTemplate(SimpleMailMessage createTemplate) {
		this.createTemplate = createTemplate;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setUpdateTemplate(SimpleMailMessage updateTemplate) {
		this.updateTemplate = updateTemplate;
	}
}
