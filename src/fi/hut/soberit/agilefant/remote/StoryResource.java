package fi.hut.soberit.agilefant.remote;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.spi.inject.Inject;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Story;

@Path("/story/{storyId}")
@Component
@Scope("prototype")
@RolesAllowed("agilefantremote")
public class StoryResource {

    @Autowired
    private StoryBusiness storyBusiness;

    @Inject
    UriInfo uriInfo;

    @GET
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML,MediaType.APPLICATION_JSON})
    public Story get(@PathParam("storyId") Integer storyId) {
        return storyBusiness.retrieve(storyId);
    }

}
