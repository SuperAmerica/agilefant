package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.Setting;

public class SettingDAOHibernate extends GenericDAOHibernate<Setting> implements
        SettingDAO {
    
    public SettingDAOHibernate() {
        super(Setting.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Setting getSetting(String settingName) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Expression.eq("name", settingName));
        return super.getFirst(super.getHibernateTemplate().findByCriteria(
                criteria));
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Setting> getAllOrderByName(){
        final String query = "from Setting s order by s.name asc";
        return (List<Setting>) super.getHibernateTemplate().find(query);
    }
}
