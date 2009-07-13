package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.Setting;

@Repository("settingDAO")
public class SettingDAOHibernate extends GenericDAOHibernate<Setting> implements SettingDAO {
       
        public SettingDAOHibernate() {
            super(Setting.class);
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public Setting getByName(String name) {
            DetachedCriteria criteria = DetachedCriteria.forClass(this
                    .getPersistentClass());
            criteria.add(Restrictions.eq("name", name));
            return super.getFirst(hibernateTemplate.findByCriteria(criteria));
        }
       
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public List<Setting> getAllOrderByName(){
            final String query = "from Setting s order by s.name asc";
            return (List<Setting>) hibernateTemplate.find(query);
        }
}
