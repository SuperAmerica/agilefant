package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.NamedObject;

public interface SearchBusiness {
    List<NamedObject> searchStoriesAndBacklog(String searchTerm);
    public static final int MAX_RESULTS_PER_TYPE = 51;
}
