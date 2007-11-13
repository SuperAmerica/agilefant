package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.DeliverableBusiness;
import fi.hut.soberit.agilefant.model.Deliverable;

public class DevelopmentPortfolioAction extends ActionSupport {

	private static final long serialVersionUID = -4749839976470627112L;

	private DeliverableBusiness deliverableBusiness;

	private int deliverableId;

	public Collection<Deliverable> getAll() {
		return deliverableBusiness.getAll();
	}

	public Collection<Deliverable> getOngoingRankedDeliverables() {
		return deliverableBusiness.getOngoingRankedDeliverables();
	}

	public Collection<Deliverable> getOngoingUnrankedDeliverables() {
		return deliverableBusiness.getOngoingUnrankedDeliverables();
	}

	public DeliverableBusiness getDeliverableBusiness() {
		return deliverableBusiness;
	}

	public void setDeliverableBusiness(DeliverableBusiness deliverableBusiness) {
		this.deliverableBusiness = deliverableBusiness;
	}

	public String moveDeliverableUp() {
		deliverableBusiness.moveUp(deliverableId);
		return Action.SUCCESS;
	}

	public String moveDeliverableDown() {
		deliverableBusiness.moveDown(deliverableId);
		return Action.SUCCESS;
	}

	public String moveDeliverableTop() {
		deliverableBusiness.moveToTop(deliverableId);
		return Action.SUCCESS;
	}

	public String moveDeliverableBottom() {
		deliverableBusiness.moveToBottom(deliverableId);
		return Action.SUCCESS;
	}

	public int getDeliverableId() {
		return deliverableId;
	}

	public void setDeliverableId(int deliverableId) {
		this.deliverableId = deliverableId;
	}

}
