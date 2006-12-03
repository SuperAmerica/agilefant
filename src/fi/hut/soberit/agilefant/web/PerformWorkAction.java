package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.WorkType;

public class PerformWorkAction extends ActionSupport {

	private static final long serialVersionUID = 1435602610355587814L;
	private int taskId;
	private int amount; // check unit
	private int workTypeId;
	private TaskDAO taskDAO;
	private WorkTypeDAO workTypeDAO;
	
	public String execute(){
		return Action.SUCCESS;
	}
}
