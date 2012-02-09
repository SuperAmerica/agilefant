package fi.hut.soberit.agilefant.readonly;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;

@Component
@Scope("prototype")
@RolesAllowed("agilefantreadonly")
@Path("/authenticate")
public class AuthenticationResource {
    @Autowired
    private UserBusiness userBusiness;

    @POST
    @Produces( { MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public User authenticate(@FormParam("urlToken") String urlToken) {
        // This function will run if a HTTP POST request for (APPLICATION_XML, TEXT_XML) is made using this resource. 
        
        // TODO @DF: This whole method is not tested or working yet, but I think this is a path that could work. 
        // Maybe something like: 
        // User user = this.userBusiness.generateReadOnlyUser(urlToken); 
        User user = this.userBusiness
                .retrieveByCredentials("admin", "secret"); // This isn't going to work unless admin has role "agilefantreadonly"
        if (user == null) {
            throw new WebApplicationException(Response.status(
                    HttpServletResponse.SC_PRECONDITION_FAILED).entity(
                    "Authentication failed").build());
        } 
        return user;
    }
}
