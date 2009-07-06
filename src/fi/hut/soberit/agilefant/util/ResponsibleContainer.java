package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.User;

public class ResponsibleContainer {
    
    private User user;
    
    private boolean inProject;
    
    public ResponsibleContainer(User user, boolean inProject) {
        this.user = user;
        this.inProject = inProject;
    }

    public User getUser() {
        return user;
    }

    public boolean isInProject() {
        return inProject;
    }

}
