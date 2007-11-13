package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.business.DeliverableBusiness;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.model.Deliverable;

public class DeliverableBusinessImpl implements DeliverableBusiness {

	private Deliverable deliverable;

	private DeliverableDAO deliverableDAO;

	/** {@inheritDoc} */
	public Collection<Deliverable> getAll() {
		return deliverableDAO.getAll();
	}

	/** {@inheritDoc} */
	public Collection<Deliverable> getOngoingRankedDeliverables() {
		return deliverableDAO.getOngoingRankedDeliverables();
	}

	/** {@inheritDoc} */
	public Collection<Deliverable> getOngoingUnrankedDeliverables() {
		return deliverableDAO.getOngoingUnrankedDeliverables();
	}

	/** {@inheritDoc} */
	public void moveDown(int deliverableId) {
		Deliverable deliverable = deliverableDAO.get(deliverableId);
		if (deliverable != null) {
			Deliverable upperRankedDeliverable = deliverableDAO
					.findFirstUpperRankedOngoingDeliverable(deliverable);
			if (upperRankedDeliverable != null) {
				int upperRank = upperRankedDeliverable.getRank();
				deliverableDAO.raiseRankBetween(upperRank + 1, null);
				deliverable.setRank(upperRank + 1);
				deliverableDAO.store(deliverable);
			}
		}
	}

	/** {@inheritDoc} */
	public void moveToBottom(int deliverableId) {
		Deliverable deliverable = deliverableDAO.get(deliverableId);
		if (deliverable != null) {
			List result = deliverableDAO.findBiggestRank();
			if (result.size() != 0) {
				int lowestRank = (Integer) (result.get(0));
				if (lowestRank != deliverable.getRank() || lowestRank == 0) {
					deliverable.setRank(lowestRank + 1);
					deliverableDAO.store(deliverable);
				}
			}
		}
	}

	/** {@inheritDoc} */
	public void moveToTop(int deliverableId) {
		Deliverable deliverable = deliverableDAO.get(deliverableId);
		if (deliverable != null && deliverable.getRank() != 1) {
			if (deliverable.getRank() == 0) {
				deliverableDAO.raiseRankBetween(1, null);
			} else {
				deliverableDAO.raiseRankBetween(1, deliverable.getRank());
			}

			deliverable.setRank(1);
			deliverableDAO.store(deliverable);
		}
	}

	/** {@inheritDoc} */
	public void moveUp(int deliverableId) {
		Deliverable deliverable = deliverableDAO.get(deliverableId);
		if (deliverable != null) {
			Deliverable lowerRankedDeliverable = deliverableDAO
					.findFirstLowerRankedOngoingDeliverable(deliverable);
			if (lowerRankedDeliverable != null) {
				int lowerRank = lowerRankedDeliverable.getRank();
				deliverableDAO.raiseRankBetween(lowerRank, deliverable
						.getRank());
				deliverable.setRank(lowerRank);
				deliverableDAO.store(deliverable);
			}
		}
	}

	/** {@inheritDoc} */
	public DeliverableDAO getDeliverableDAO() {
		return deliverableDAO;
	}

	/** {@inheritDoc} */
	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
		this.deliverableDAO = deliverableDAO;
	}

}
