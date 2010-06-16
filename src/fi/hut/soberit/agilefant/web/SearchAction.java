package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.SearchBusiness;
import fi.hut.soberit.agilefant.model.NamedObject;

@Component("searchAction")
@Scope("prototype")
public class SearchAction extends ActionSupport {
    private static final long serialVersionUID = -2601570890557218624L;
    @Autowired
    private SearchBusiness searchBusiness;
    
    private List<NamedObject> results;
    
    private String term;
    
    @Override
    public String execute() {
        results = this.searchBusiness.searchStoriesAndBacklog(term);
        return Action.SUCCESS;
    }

    public List<NamedObject> getResults() {
        return results;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
