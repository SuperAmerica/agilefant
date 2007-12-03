package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

/**
 * Hibernate implementation of UserDAO interface using GenericDAOHibernate.
 */
public class UserDAOHibernate extends GenericDAOHibernate<User> implements
        UserDAO {

    public UserDAOHibernate() {
        super(User.class);
    }

    /** {@inheritDoc} */
    public User getUser(String loginName) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Expression.eq("loginName", loginName));
        return super.getFirst(super.getHibernateTemplate().findByCriteria(
                criteria));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Task> getUnfinishedTasks(User user) {
        return (Collection<Task>) super
                .getHibernateTemplate()
                .findByNamedParam(
                        "from Task t where t.assignee.id = :id and t.status != 4",
                        "id", new Integer(user.getId()));
    }

    private String instanceOf(String item, String clazz) {
        String clazzTempInstance = "temp_" + clazz.toLowerCase();
        return "(" + item + ".id in (select " + clazzTempInstance + ".id from "
                + clazz + " " + clazzTempInstance + ")) ";
    }

    private String dateIntersects(String dateContainer) {
        return "((" + dateContainer + ".startDate <= :end and " + dateContainer
                + ".endDate >= :start) " + "or (" + dateContainer
                + ".startDate is null and " + dateContainer
                + ".endDate is null)) ";
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Task> getUnfinishedTasksByTime(User user, Date start,
            Date end) {

        final String names[] = { "id", "start", "end" };
        Object values[] = { user.getId(), start, end };

        return (Collection<Task>) super
                .getHibernateTemplate()
                .findByNamedParam(

                        "select distinct t from Task t, Project d, Iteration i where "
                                + "t.assignee.id = :id and t.status != 4 and "
                                + "( "
                                + instanceOf("t.backlogItem.backlog",
                                        "Project")
                                + "and d.id = t.backlogItem.backlog.id and "
                                + dateIntersects("d")
                                + " ) "
                                + "or "
                                + "( "
                                + instanceOf("t.backlogItem.backlog",
                                        "Iteration")
                                + "and i.id = t.backlogItem.backlog.id and "
                                + dateIntersects("i")
                                + " )"
                                + "or "
                                + instanceOf("t.backlogItem.backlog", "Product"),

                        names, values);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<BacklogItem> getBacklogItemsByTime(User user, Date start,
            Date end) {

        final String names[] = { "id", "start", "end" };
        Object values[] = { user.getId(), start, end };

        return (Collection<BacklogItem>) super.getHibernateTemplate()
                .findByNamedParam(

                        "select distinct bli from BacklogItem bli, Project d, Iteration i where "
                                + "bli.assignee.id = :id and " + "( "
                                + instanceOf("bli.backlog", "Project")
                                + "and d.id = bli.backlog.id and "
                                + dateIntersects("d") + " ) " + "or " + "( "
                                + instanceOf("bli.backlog", "Iteration")
                                + "and i.id = bli.backlog.id and "
                                + dateIntersects("i") + " )" + "or "
                                + instanceOf("bli.backlog", "Product"), names,
                        values);

    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<BacklogItem> getBacklogItemsInProgress(User user) {
        HibernateTemplate ht = super.getHibernateTemplate();

        DetachedCriteria crit = DetachedCriteria.forClass(BacklogItem.class);
        crit.add(Restrictions.eq("assignee", user));

        return ht.findByCriteria(crit);
    }
}