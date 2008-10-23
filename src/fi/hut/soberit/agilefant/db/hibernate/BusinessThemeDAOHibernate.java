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
        crit.addOrder(Order.asc("themes.name"));
        return super.getHibernateTemplate().findByCriteria(crit);
    }
    
    public void saveOrUpdateBacklogThemeBinding(BacklogThemeBinding binding) {
        super.getHibernateTemplate().saveOrUpdate(binding);
    }
    public void removeBacklogThemeBinding(BacklogThemeBinding binding) {
        super.getHibernateTemplate().delete(binding);
    }
    @SuppressWarnings("unchecked")
    public List<BacklogThemeBinding> getIterationThemesByProject(Project project) {
        DetachedCriteria iterationCriteria = DetachedCriteria.forClass(Iteration.class);
        //DetachedCriteria bindings = iterationCriteria.createAlias("businessThemeBindings", "themeBindings");      
        //iterationCriteria.setFetchMode("businessThemeBindings", FetchMode.JOIN);
        iterationCriteria.setFetchMode("assignments", FetchMode.SELECT);
        iterationCriteria.setFetchMode("backlogItems", FetchMode.SELECT);
        iterationCriteria.setFetchMode("owner", FetchMode.SELECT);
        iterationCriteria.setFetchMode("backlogHistory", FetchMode.SELECT);
        iterationCriteria.setFetchMode("project", FetchMode.SELECT);
        //bindings.setFetchMode("businessTheme", FetchMode.JOIN);
        
        iterationCriteria.add(Restrictions.eq("project", project));
        List<Object> res = super.getHibernateTemplate().findByCriteria(iterationCriteria);
        List<BacklogThemeBinding> ret = new ArrayList<BacklogThemeBinding>();
        for(Object ob : res) {
            System.out.println(ob);
            Iteration iter = (Iteration)ob;
            if(iter.getBusinessThemeBindings() != null) {
                ret.addAll(iter.getBusinessThemeBindings());
            }
        }
        return ret;
    }
    
    public BacklogThemeBinding getBindingById(int bindingId) {
        return (BacklogThemeBinding)super.getHibernateTemplate().get(BacklogThemeBinding.class, bindingId);
    }
}
