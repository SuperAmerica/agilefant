package fi.hut.soberit.agilefant.remote;

import java.util.Collection;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.transfer.BacklogInfoCollectionTO;
import fi.hut.soberit.agilefant.transfer.BacklogInfoTO;
import fi.hut.soberit.agilefant.transfer.ProjectTO;

@Path("/product")
@Component
@Scope("prototype")
@RolesAllowed("agilefantremote")
public class ProductResource {

    @Autowired
    private ProductBusiness productBusiness;

    @GET
    @Path("/{productId}")
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public Product get(@PathParam("productId") Integer productId) {
        return productBusiness.retrieve(productId);
    }
    
    @GET
    @Path("/{productId}/projects")
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public BacklogInfoCollectionTO getProjects(@PathParam("productId") Integer productId) {
        BacklogInfoCollectionTO coll = new BacklogInfoCollectionTO();
        for (ProjectTO p : productBusiness.retrieveProjects(productBusiness.retrieve(productId))) {
            coll.getBacklogs().add(new BacklogInfoTO(p));
        }
        return coll;
    }
    
    @GET
    @Path("/list")
    @Produces({MediaType.APPLICATION_XML,MediaType.TEXT_XML})
    public BacklogInfoCollectionTO getAll() {
        Collection<Product> products = productBusiness.retrieveAllOrderByName();
        BacklogInfoCollectionTO coll = new BacklogInfoCollectionTO(); 
        for (Product prod : products) {
            coll.getBacklogs().add(new BacklogInfoTO(prod));
        }
        return coll;
    }

    
}
