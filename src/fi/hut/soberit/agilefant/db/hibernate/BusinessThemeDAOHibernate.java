package fi.hut.soberit.agilefant.db.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.State;

public class BusinessThemeDAOHibernate extends GenericDAOHibernate<BusinessTheme> implements BusinessThemeDAO {

    public BusinessThemeDAOHibernate() {
        super(BusinessTheme.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<BusinessTheme> getSortedBusinessThemesByProductAndActivity(Product product, boolean active) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("product", product));
        criteria.add(Restrictions.eq("active", active));
        criteria.addOrder(Order.asc("name"));

        return (List<BusinessTheme>)super.getHibernateTemplate()
                            .findByCriteria(criteria);
       
    }
    public Map<Integer, Integer> numberOfBacklogItemsByProduct(Product product,
            State backlogItemState) {
        DetachedCriteria crit = DetachedCriteria.forClass(BacklogItem.class);
        ProjectionList proj = Projections.projectionList();
        proj.add(Projections.groupProperty("id"));
        proj.add(Projections.property("id"));
        proj.add(Projections.count("id"));
        crit.createCriteria("businessThemes").add(
                Restrictions.eq("product", product)).setProjection(proj);
        if (backlogItemState != null) {
            crit.add(Restrictions.eq("state", backlogItemState));
        }
        HashMap<Integer, Integer> res = new HashMap<Integer, Integer>();
        for (Object item : super.getHibernateTemplate().findByCriteria(crit)) {
            Object[] row = (Object[]) item;
            res.put((Integer) row[1], (Integer) row[2]);
        }

        return res;
    }

    @SuppressWarnings("unchecked")
    public List getThemesByBacklog(Backlog backlog) {
        DetachedCriteria crit = DetachedCriteria.forClass(BacklogItem.class);
        crit.createAlias("businessThemes","themes");
        ProjectionList p = Projections.projectionList();
        p.add(Projections.property("id"),"backlogItemId");
        p.add(Projections.property("themes.id"),"themeId");
        p.add(Projections.property("themes.name"),"themeName");
        p.add(Projections.property("themes.description"),"themeDescription");
        crit.setProjection(p);
        crit.setFetchMode("themes", FetchMode.JOIN);
        crit.setFetchMode("iterationGoal", FetchMode.SELECT);
        crit.setFetchMode("responsibles", FetchMode.SELECT);
        crit.setFetchMode("tasks", FetchMode.SELECT);
        crit.add(Restrictions.eq("backlog", backlog));
        crit.add(Restrictions.eq("themes.active", true));
        crit.addOrder(Order.asc("themes.name"));
        return super.getHibernateTemplate().findByCriteria(crit);
    }
    
}
