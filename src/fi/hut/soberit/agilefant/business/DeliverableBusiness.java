package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Deliverable;

/**
 * Updates projects' ranks.
 * 
 * @author Aleksi Toivonen
 * 
 */

public interface DeliverableBusiness {

	/**
	 * Get all projects.
	 * 
	 * @return all projects.
	 */
	public Collection<Deliverable> getAll();

	/**
	 * Get all ongoing projects that are ranked.
	 * 
	 * @return
	 */
	public Collection<Deliverable> getOngoingRankedDeliverables();

	/**
	 * Get all ongoing projects that are not ranked.
	 * 
	 * @return
	 */
	public Collection<Deliverable> getOngoingUnrankedDeliverables();

	/**
	 * Move project's rank up by one "visible" rank. May jump over many
	 * projects, because projects that are ranked but not ongoing are affected.
	 * 
	 * @param deliverable
	 */
	public void moveUp(int deliverableId);

	/**
	 * Move project's rank down by one "visible" rank. May jump over many
	 * projects, because projects that are ranked but not ongoing are affected.
	 * 
	 * @param deliverable
	 */
	public void moveDown(int deliverableId);

	/**
	 * Sets project's rank to the highest of all ranked projects.
	 * 
	 * @param deliverable
	 */
	public void moveToTop(int deliverableId);

	/**
	 * Sets project's rank to the lowest of all ranked projects.
	 * 
	 * @param deliverable
	 */
	public void moveToBottom(int deliverableId);

}
