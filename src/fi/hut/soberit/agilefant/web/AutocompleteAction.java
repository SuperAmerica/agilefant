package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;

@Component("autocompleteAction")
@Scope("prototype")
public class AutocompleteAction extends ActionSupport {

    private static final long serialVersionUID = 7282682342820966296L;
    
    private List<AutocompleteDataNode> autocompleteData = null;
    
    @Autowired
    private TransferObjectBusiness transferObjectBusiness;
    
    @SuppressWarnings("unchecked")
    public String userTeamData() {
        autocompleteData = new ArrayList<AutocompleteDataNode>();
        autocompleteData.addAll(transferObjectBusiness.constructUserAutocompleteData());
        autocompleteData.addAll(transferObjectBusiness.constructTeamAutocompleteData());
        
        Collections.sort(autocompleteData, new PropertyComparator("name", true, true));
        
        return Action.SUCCESS;
    }
    
    public String backlogData() {
        autocompleteData = transferObjectBusiness.constructBacklogAutocompleteData();
        return Action.SUCCESS;
    }
    
    public String productData() {
        autocompleteData = transferObjectBusiness.constructProductAutocompleteData();
        return Action.SUCCESS;
    }
    
    public String projectData() {
        autocompleteData = transferObjectBusiness.constructProjectAutocompleteData();
        return Action.SUCCESS;
    }

    public String currentIterationData() {
        autocompleteData = transferObjectBusiness.constructCurrentIterationAutocompleteData();
        return Action.SUCCESS;
    }

    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public List<AutocompleteDataNode> getAutocompleteData() {
        return autocompleteData;
    }

}
