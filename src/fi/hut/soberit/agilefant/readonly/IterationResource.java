package fi.hut.soberit.agilefant.readonly;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.transfer.IterationTO;

// TODO @DF ... probably need to change this to something like @Path("/{urlToken") ? This could actually work somehow. 
@Path("/iteration/{iterationId}")
@Component
@Scope("prototype")
@RolesAllowed("agilefantreadonly")
public class IterationResource {

    @Autowired
    private IterationBusiness iterationBusiness;

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public IterationTO get(@PathParam("urlToken") String urlToken) {
        // This function will run if a HTTP GET request for (APPLICATION_XML, TEXT_XML) is made using this resource. 
        
        // TODO @DF this function will need to be coded somewhere, and work :). 
        // int iterationId = getIdFromToken(urlToken);
        int iterationId = 1;
        
        // TODO @DF : This will probably also need to be changed. 
        return iterationBusiness.retrieveIterationOnlyLeafStories(iterationId);
    }

}
