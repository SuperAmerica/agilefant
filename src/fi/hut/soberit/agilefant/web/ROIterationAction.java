package fi.hut.soberit.agilefant.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

@Component("ROIterationAction")
@Scope("prototype")
public class ROIterationAction extends ActionSupport implements CRUDAction, Prefetching {

    private static final long serialVersionUID = -3334278151418035144L;
    
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

}