package fi.hut.soberit.agilefant.remote;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.inject.Inject;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Iteration;

@Path("/iteration/{iterationId}")
@Component
@Scope("prototype")
@RolesAllowed("agilefantremote")
public class IterationResource {

    @Autowired
    private IterationBusiness iterationBusiness;

    @Inject
    UriInfo uriInfo;

    @GET
    @Produces("application/xml")
    public JAXBElement<Iteration> get(
            @PathParam("iterationId") Integer iterationId) {
        return new JAXBElement<Iteration>(new QName("iteration"), Iteration.class,
                iterationBusiness.getIterationContents(iterationId));
    }

}
