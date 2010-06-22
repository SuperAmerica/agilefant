package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.SearchBusiness;
import fi.hut.soberit.agilefant.transfer.SearchResultRow;

@Component("searchAction")
@Scope("prototype")
public class SearchAction extends ActionSupport {
    private static final long serialVersionUID = -2601570890557218624L;
    @Autowired
    private SearchBusiness searchBusiness;
    
    private List<SearchResultRow> results;
    
    private String term;
    
    @Override
    public String execute() {
        results = this.searchBusiness.searchStoriesAndBacklog(term);
        return Action.SUCCESS;
    }

    public List<SearchResultRow> getResults() {
        return results;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
