package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
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
import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.TodoMetrics;

public class BusinessThemeDAOHibernate extends
        GenericDAOHibernate<BusinessTheme> implements BusinessThemeDAO {

    public BusinessThemeDAOHibernate() {
        super(BusinessTheme.class);
    }

    public List<BusinessTheme> getSortedBusinessThemesByProductAndActivity(
            Product product, boolean active) {
        return getSortedBusinessThemesByProductAndActivity(product, active,
                false);
    }

    @SuppressWarnings("unchecked")
    public List<BusinessTheme> getSortedBusinessThemesByProductAndActivity(
            Product product, Boolean active, boolean includeGlobal) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        if (includeGlobal) {
            criteria.add(Restrictions.or(Restrictions.eq("product", product),
                    Restrictions.eq("global",true)));
        } else {
            criteria.add(Restrictions.eq("product", product));
        }
        if (active != null) {
            criteria.add(Restrictions.eq("active", active));
        }
        // do not join the product data
        criteria.setFetchMode("product", FetchMode.SELECT);
        criteria.addOrder(Order.asc("name"));

        return (List<BusinessTheme>) super.getHibernateTemplate()
                .findByCriteria(criteria);

    }

    @SuppressWarnings("unchecked")
    public List<BusinessTheme> getSortedGlobalThemes(Boolean active) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        
        criteria.add(Restrictions.eq("global", true));
        criteria.add(Restrictions.eq("active", active));

        return (List<BusinessTheme>) super.getHibernateTemplate()
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

    public void saveOrUpdateBacklogThemeBinding(BacklogThemeBinding binding) {
        super.getHibernateTemplate().saveOrUpdate(binding);
    }

    public void removeBacklogThemeBinding(BacklogThemeBinding binding) {
        super.getHibernateTemplate().delete(binding);
    }

    @SuppressWarnings("unchecked")
    public List<BacklogThemeBinding> getIterationThemesByProject(Project project) {
        DetachedCriteria iterationCriteria = DetachedCriteria
                .forClass(Iteration.class);

        iterationCriteria.setFetchMode("assignments", FetchMode.SELECT);
        iterationCriteria.setFetchMode("backlogItems", FetchMode.SELECT);
        iterationCriteria.setFetchMode("owner", FetchMode.SELECT);
        iterationCriteria.setFetchMode("backlogHistory", FetchMode.SELECT);
        iterationCriteria.setFetchMode("project", FetchMode.SELECT);
        // bindings.setFetchMode("businessTheme", FetchMode.JOIN);

        iterationCriteria.add(Restrictions.eq("project", project));
        List<Object> res = super.getHibernateTemplate().findByCriteria(
                iterationCriteria);
        List<BacklogThemeBinding> ret = new ArrayList<BacklogThemeBinding>();
        for (Object ob : res) {
            System.out.println(ob);
            Iteration iter = (Iteration) ob;
            if (iter.getBusinessThemeBindings() != null) {
                ret.addAll(iter.getBusinessThemeBindings());
            }
        }
        return ret;
    }

    public BacklogThemeBinding getBindingById(int bindingId) {
        return (BacklogThemeBinding) super.getHibernateTemplate().get(
                BacklogThemeBinding.class, bindingId);
    }

    @SuppressWarnings("unchecked")
    public Map<BacklogItem, List<BusinessTheme>> getBacklogItemBusinessThemesByBacklog(
            Backlog backlog) {
        String hql = "from BacklogItem as bli left outer join bli.businessThemes as theme WHERE bli.backlog = ? order by theme.name asc";
        List<Object[]> respBli = this.getHibernateTemplate().find(hql, new Object[] {backlog});
        Map<BacklogItem, List<BusinessTheme>> res = new HashMap<BacklogItem, List<BusinessTheme>>();
        for(Object[] row : respBli) {
           BacklogItem item = (BacklogItem)row[0];
           BusinessTheme theme  = (BusinessTheme)row[1];
           if(res.get(item) == null) {
               res.put(item, new ArrayList<BusinessTheme>());
           }
           res.get(item).add(theme);
        }
        return res;
    }
}
