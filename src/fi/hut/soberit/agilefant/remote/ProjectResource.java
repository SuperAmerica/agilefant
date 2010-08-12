package fi.hut.soberit.agilefant.remote;

import java.util.Collection;

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

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.transfer.BacklogInfoCollectionTO;
import fi.hut.soberit.agilefant.transfer.BacklogInfoTO;
import fi.hut.soberit.agilefant.transfer.IterationTO;

@Path("/project")
@Component
@Scope("prototype")
@RolesAllowed("agilefantremote")
public class ProjectResource {

    @Autowired
    private ProjectBusiness projectBusiness;

    @Inject
    UriInfo uriInfo;

    @GET
    @Path("/{projectId}")
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public Project get(@PathParam("projectId") Integer projectId) {
        return projectBusiness.retrieve(projectId);
    }
    
    @GET
    @Path("/{projectId}/iterations")
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public BacklogInfoCollectionTO getProjects(@PathParam("projectId") Integer projectId) {
        BacklogInfoCollectionTO coll = new BacklogInfoCollectionTO();
        for (IterationTO iter : projectBusiness.retrieveProjectIterations(projectId)) {
            coll.getBacklogs().add(new BacklogInfoTO(iter));
        }
        return coll;
    }
    
    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public BacklogInfoCollectionTO getAll() {
        Collection<Project> projects = projectBusiness.retrieveAll();
        BacklogInfoCollectionTO coll = new BacklogInfoCollectionTO(); 
        for (Project prod : projects) {
            coll.getBacklogs().add(new BacklogInfoTO(prod));
        }
        return coll;
    }
    
}
