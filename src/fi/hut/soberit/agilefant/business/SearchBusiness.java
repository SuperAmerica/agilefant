package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.NamedObject;
import fi.hut.soberit.agilefant.transfer.SearchResultRow;

public interface SearchBusiness {
    public List<SearchResultRow> searchStoriesAndBacklog(String searchTerm);
    public NamedObject searchByReference(String searchTerm);
    public static final int MAX_RESULTS_PER_TYPE = 51;
}
