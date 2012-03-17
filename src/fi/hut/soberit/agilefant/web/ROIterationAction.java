package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.AssignmentTO;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;

@Component("ROIterationAction")
@Scope("prototype")
public class ROIterationAction extends ActionSupport implements CRUDAction, Prefetching {

    private static final long serialVersionUID = -3334278151418035144L;
    
    private String readonlyToken;
    
    private Iteration iteration;
    
    private IterationMetrics iterationMetrics;
    
    @Autowired
    private IterationBusiness iterationBusiness;
    
    public String fetchROIterationData() {
        iteration = iterationBusiness.retreiveIterationByReadonlyToken(readonlyToken);
        
        if(iteration != null) {
            iterationBusiness.retrieve(iteration.getId());
        } else {
        	//TODO FINNUCKS: This doesn't redirect to the login
            return Action.LOGIN;
        }
        
        iteration = iterationBusiness.getIterationContents(iteration.getId());
        return Action.SUCCESS;
    }
    
    @Override
    public String execute() {
        return Action.SUCCESS;
    }

    /**
     * Create a new read only iteration object
     */
    public String create() {
        
        return Action.SUCCESS;
    }

    /**
     * Delete an existing read only iteration object
     */
    public String delete() {

        return Action.SUCCESS;
    }

    /**
     * Edit a read only iteration object (Doesn't do anything)
     */
    public String retrieve() {
        
        return Action.SUCCESS;
    }
    
    public String retrieveAll() {

        return Action.SUCCESS;
    }

    /**
     * Store the read only iteration object (Doesn't do anything)
     */
    public String store() {

        return Action.SUCCESS;
    }

    public void initializePrefetchedData(int objectId) {

    }
    
    public void setReadonlyToken(String readonlyToken) {
        this.readonlyToken = readonlyToken;
    }
    
    public String getReadonlyToken() {
        return readonlyToken;
    }
    
    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }
    
    public void setIterationBusiness(IterationBusiness iterationBusiness) {
        this.iterationBusiness = iterationBusiness;
    }

    public IterationMetrics getIterationMetrics() {
        return iterationMetrics;
    }

}