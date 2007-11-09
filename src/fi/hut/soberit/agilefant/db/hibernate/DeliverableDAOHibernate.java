package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Date;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.model.Deliverable;

/**
 * Hibernate implementation of DeliverableDAO interface using
 * GenericDAOHibernate.
 */
public class DeliverableDAOHibernate extends GenericDAOHibernate<Deliverable>
		implements DeliverableDAO {

	public DeliverableDAOHibernate() {
		super(Deliverable.class);
	}

	/** {@inheritDoc} */
	public Collection<Deliverable> getOngoingDeliverables() {
		Date current = Calendar.getInstance().getTime();
		return super
				.getHibernateTemplate()
				.find(
						"from Deliverable d where d.startDate <= ? and d.endDate >= ? order by d.product.name ASC, d.endDate",
						new Object[] { current, current });
	}

	/** {@inheritDoc} */
	public Collection<Deliverable> getAllRankedDeliverables() {
		return super.getHibernateTemplate().find(
				"from Deliverable d where d.rank != 0 order by d.rank ASC");
	}

	/** {@inheritDoc} */
	public Collection<Deliverable> getOngoingRankedDeliverables() {
		Date current = Calendar.getInstance().getTime();
		return super
				.getHibernateTemplate()
				.find(
						"from Deliverable d where d.startDate <= ? and d.endDate >= ? and d.rank != 0 order by d.rank ASC",
						new Object[] { current, current });
	}

	/** {@inheritDoc} */
	public Collection<Deliverable> getOngoingUnrankedDeliverables() {
		Date current = Calendar.getInstance().getTime();
		return super
				.getHibernateTemplate()
				.find(
						"from Deliverable d where d.startDate <= ? and d.endDate >= ? and d.rank = 0 order by d.product.name ASC, d.endDate",
						new Object[] { current, current });
	}

	public Deliverable findFirstLowerRankedOngoingDeliverable(
			Deliverable deliverable) {
		Date current = Calendar.getInstance().getTime();
		List deliverables = getHibernateTemplate()
				.find(
						"from Deliverable d where (d.rank < ?) and (d.rank != 0) and (d.startDate <= ? and d.endDate >= ?) order by d.rank desc limit 1",
						new Object[] { deliverable.getRank(), current, current });
		if (deliverables.size() == 0) {
			return null;
		} else {
			return (Deliverable) deliverables.get(0);
		}
	}

	public Deliverable findFirstUpperRankedOngoingDeliverable(
			Deliverable deliverable) {
		Date current = Calendar.getInstance().getTime();
		List deliverables = getHibernateTemplate()
				.find(
						"from Deliverable d where (d.rank > ?) and (d.rank != 0) and (d.startDate <= ? and d.endDate >= ?) order by d.rank asc limit 1",
						new Object[] { deliverable.getRank(), current, current });
		if (deliverables.size() == 0) {
			return null;
		} else {
			return (Deliverable) deliverables.get(0);
		}
	}

	public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank) {
		List deliverables = null;
		/*
		 * if (lowLimitRank == null) deliverables =
		 * super.getHibernateTemplate().find( "from Deliverable d where d.rank <
		 * ?", upperLimitRank); else if (upperLimitRank == null) deliverables =
		 * super.getHibernateTemplate().find( "from Deliverable d where d.rank >=
		 * ?", lowLimitRank); else if (lowLimitRank != null && upperLimitRank !=
		 * null) deliverables = super.getHibernateTemplate().find( "from
		 * Deliverable d where d.rank >= ? and d.rank < ?", new Object[] {
		 * lowLimitRank, upperLimitRank }); else throw new
		 * IllegalArgumentException("Both limits canot be null.");
		 * 
		 * Iterator it = deliverables.iterator(); while (it.hasNext()) {
		 * Deliverable d = (Deliverable) it.next(); d.setRank(d.getRank() + 1);
		 * store(d); }
		 */

		if (lowLimitRank == null) {
			super
					.getHibernateTemplate()
					.bulkUpdate(
							"update Deliverable d set d.rank = (d.rank + 1) where d.rank < ?",
							upperLimitRank);
		} else if (upperLimitRank == null) {
			super
					.getHibernateTemplate()
					.bulkUpdate(
							"update Deliverable d set d.rank = (d.rank + 1) where d.rank >= ?",
							lowLimitRank);
		} else if (lowLimitRank != null && upperLimitRank != null) {
			super
					.getHibernateTemplate()
					.bulkUpdate(
							"update Deliverable d set d.rank = (d.rank + 1) where d.rank >= ? and d.rank < ?",
							new Object[] { lowLimitRank, upperLimitRank });
		} else
			throw new IllegalArgumentException("Both limits cannot be null.");
	}

	public List<Integer> findBiggestRank() {
		List result = null;
		return result = super.getHibernateTemplate().find(
				"select max(d.rank) from Deliverable d");
	}

}
