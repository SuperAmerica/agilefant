package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.NamedObject;

public interface SearchBusiness {
    public List<NamedObject> searchStoriesAndBacklog(String searchTerm);
    public NamedObject searchByReference(String searchTerm);
    public static final int MAX_RESULTS_PER_TYPE = 51;
}
