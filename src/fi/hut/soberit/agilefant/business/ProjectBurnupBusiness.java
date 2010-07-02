package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Project;

public interface ProjectBurnupBusiness {

    byte[] getBurnup(Project project);
    
    byte[] getSmallBurnup(Project project);

}
