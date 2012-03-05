package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.Product;

@Component("accessAction")
@Scope("prototype")
public class AccessAction extends ActionSupport implements CRUDAction, Prefetching {

    private static final long serialVersionUID = -3334278151418035144L;

    /**
     * Create a new team to product relationship.
     */
    public String create() {
        
        return Action.SUCCESS;
    }

    /**
     * Delete an existing team to product relationship.
     */
    public String delete() {

        return Action.SUCCESS;
    }

    /**
     * Edit a team to product relationship.
     */
    public String retrieve() {
        
        return Action.SUCCESS;
    }
    
    public String retrieveAll() {

        return Action.SUCCESS;
    }

    /**
     * Store the team to product relationship.
     */
    public String store() {

        return Action.SUCCESS;
    }



    public void initializePrefetchedData(int objectId) {

    }
}
