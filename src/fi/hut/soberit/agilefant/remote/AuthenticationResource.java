package fi.hut.soberit.agilefant.remote;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
@RolesAllowed("agilefantremote")
@Path("/authenticate")
public class AuthenticationResource {
    @Autowired
    private UserBusiness userBusiness;

    @POST
    @Produces( { MediaType.APPLICATION_XML, MediaType.TEXT_XML })
    public User authenticate(@FormParam("username") String loginName,
            @FormParam("password") String password) {
        User user = this.userBusiness
                .retrieveByCredentials(loginName, password);
        if (user == null) {
            throw new WebApplicationException(Response.status(
                    HttpServletResponse.SC_PRECONDITION_FAILED).entity(
                    "Authentication failed").build());
        } 
        return user;
    }
}
