package fi.hut.soberit.agilefant.service;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.model.Portfolio;

public class PortfolioManagerImpl implements PortfolioManager {

	private DeliverableDAO deliverableDAO;

	public Portfolio getCurrentPortfolio() {
		Portfolio result = new Portfolio();
		result.setDeliverables(deliverableDAO.getOngoingDeliverables());
		return result;
	}

	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
		this.deliverableDAO = deliverableDAO;
	}
}
