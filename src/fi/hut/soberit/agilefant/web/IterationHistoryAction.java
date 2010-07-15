package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;

@Component("iterationHistoryAction")
@Scope("prototype")
public class IterationHistoryAction extends ActionSupport {
    private static final long serialVersionUID = -5381754026574773970L;
    
    @Autowired
    private IterationBusiness iterationBusiness;
    
    private List<AgilefantHistoryEntry> storyHistory;
    private int iterationId;

    
    @Override
    public String execute() {
        Iteration iteration = this.iterationBusiness.retrieve(iterationId);
        storyHistory = this.iterationBusiness.retrieveChangesInIterationStories(iteration);
        return SUCCESS;
    }
    
    public List<AgilefantHistoryEntry> getStoryHistory() {
        return storyHistory;
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }
    

}
