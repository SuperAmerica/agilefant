package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.util.EffortHistoryUpdater;

public class DailyWorkAction extends ActionSupport {
	private static final long serialVersionUID = 5732278003634700787L;
	
	private String hello = "Hello A";
	
	public String getHello() {
		return hello;
	}
	
	@Override
	public String execute() throws Exception {
		hello = "Hello 'Fant!";
		// TODO Auto-generated method stub
		return super.execute();
	}
}
