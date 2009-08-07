package fi.hut.soberit.agilefant.db.history.impl;

import org.hibernate.envers.RevisionListener;

import fi.hut.soberit.agilefant.model.AgilefantRevisionEntity;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class AgilefantRevisionListener implements RevisionListener {

    public void newRevision(Object revisionEntity) {
        AgilefantRevisionEntity entity = (AgilefantRevisionEntity)revisionEntity;
        User user = SecurityUtil.getLoggedUser();
        if(user != null) {
            entity.setUserName(user.getFullName());
            entity.setUserId(user.getId());
        }
    }
}
