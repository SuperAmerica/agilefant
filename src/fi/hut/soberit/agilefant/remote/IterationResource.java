package fi.hut.soberit.agilefant.remote;

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

@Path("/iteration/{iterationId}")
@Component
@Scope("prototype")
@RolesAllowed("agilefantremote")
public class IterationResource {

    @Autowired
    private IterationBusiness iterationBusiness;

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public IterationTO get(@PathParam("iterationId") Integer iterationId) {
        return iterationBusiness.retrieveIterationOnlyLeafStories(iterationId);
    }

}
