package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;

@Component("userTeamAutocomplete")
@Scope("prototype")
public class UserTeamAutcompleteAction extends ActionSupport {

    private static final long serialVersionUID = 7282682342820966296L;
    
    private List<AutocompleteDataNode> userTeamData = null;
    
    @Autowired
    private TransferObjectBusiness transferObjectBusiness;
    
    @Override
    public String execute() {
        userTeamData = new ArrayList<AutocompleteDataNode>();
        userTeamData.addAll(transferObjectBusiness.constructUserAutocompleteData());
        userTeamData.addAll(transferObjectBusiness.constructTeamAutocompleteData());
        return Action.SUCCESS;
    }

    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public List<AutocompleteDataNode> getUserTeamData() {
        return userTeamData;
    }

}
