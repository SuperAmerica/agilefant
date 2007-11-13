package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Deliverable;

/**
 * Interface for a DAO of a Deliverable.
 * 
 * @see GenericDAO
 */
public interface DeliverableDAO extends GenericDAO<Deliverable> {

	/**
	 * Get all currently ongoing deliverables.
	 */
	public Collection<Deliverable> getOngoingDeliverables();

	/**
	 * Get all deliverables that have rank.
	 */
	public Collection<Deliverable> getAllRankedDeliverables();

	/**
	 * Get all currently ongoing deliverables that have rank by rank order.
	 */
	public Collection<Deliverable> getOngoingRankedDeliverables();

	/**
	 * Get all Currently ongoing deliverables that are unranked.
	 */
	public Collection<Deliverable> getOngoingUnrankedDeliverables();

	/**
	 * Finds the next deliverable that is ongoing and has lower rank number than
	 * the deliverable given as parameter.
	 * 
	 * @param deliverable
	 * @return
	 */
	public Deliverable findFirstLowerRankedOngoingDeliverable(
			Deliverable deliverable);

	/**
	 * Finds the next deliverable that is ongoing and has upper rank number than
	 * the deliverable given as parameter.
	 * 
	 * @param deliverable
	 * @return
	 */
	public Deliverable findFirstUpperRankedOngoingDeliverable(
			Deliverable deliverable);

	/**
	 * Raises the rank of all Deliverables between the set rank limits, by one.
	 * If low limit is null, then all ranks lower than the upper limit are
	 * affected. If upper limit is null, then all ranks higher than the low
	 * limit are affected. Low limit rank is included in the raised ranks, but
	 * upper limit rank is not. The range of raised ranks is
	 * [lowLimitRank,upperLimitRank[
	 * 
	 * @param lowLimitRank
	 * @param upperLimitRank
	 */
	public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank);

	/**
	 * Finds the biggest Rank among all Deliverables.
	 * 
	 * @return
	 */
	public List<Integer> findBiggestRank();
}
