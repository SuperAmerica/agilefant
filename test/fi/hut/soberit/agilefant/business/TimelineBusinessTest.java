package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.TimelineBusinessImpl;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Status;
import fi.hut.soberit.agilefant.util.CalendarUtils;

public class TimelineBusinessTest extends TestCase {
    
    private TimelineBusinessImpl timelineBusiness = new TimelineBusinessImpl();
    private ProductDAO productDAO;
    
    
    public void testProductToJSON_noSuchProduct() {
        productDAO = createMock(ProductDAO.class);
        timelineBusiness.setProductDAO(productDAO);
        
        expect(productDAO.get(5)).andReturn(null);
        replay(productDAO);
        
        try {
            timelineBusiness.productContentsToJSON(5);
            fail();
        }
        catch (ObjectNotFoundException onfe) {}
        
        verify(productDAO);
    }
    
    /**
     * Test product with no projects.
     */
    public void testProductToJSON_noProjects() {
        productDAO = createMock(ProductDAO.class);
        timelineBusiness.setProductDAO(productDAO);
        
        /* Create the test data */
        Product prod = new Product();
        prod.setId(1);
        prod.setName("Testituote");
        prod.setProjects(new ArrayList<Project>());
        prod.setBacklogItems(new ArrayList<BacklogItem>());
        
        expect(productDAO.get(1)).andReturn(prod);
        replay(productDAO);
        
        String verifiedJSON = "{name:'Testituote',id:1,type:'product',\n" +
                        "contents:[\n]\n}";
        
        System.out.println(verifiedJSON);
        
        try {
            String json = timelineBusiness.productContentsToJSON(1);
            System.out.println("--\n" + json);
            assertEquals(verifiedJSON, json);
        }
        catch (ObjectNotFoundException onfe) {
            fail(onfe.getMessage());
        }
        
        verify(productDAO);
    }
    
    /**
     * Test converting a product to JSON.
     */
    public void testProductToJSON_simpleCase() {
        productDAO = createMock(ProductDAO.class);
        timelineBusiness.setProductDAO(productDAO);
        
        /* Create the test data */
        Product prod = new Product();
        prod.setId(1);
        prod.setName("Testituote");
        prod.setProjects(new ArrayList<Project>());
        prod.setBacklogItems(new ArrayList<BacklogItem>());
        
        Calendar cal = GregorianCalendar.getInstance();
        CalendarUtils.setHoursMinutesAndSeconds(cal, 0, 0, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.YEAR, 2008);
        
        Project proj = new Project();
        proj.setId(2);
        proj.setName("Testiprojekti");
        proj.setStartDate(cal.getTime());
        proj.setStatus(Status.OK);
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.DATE, 31);
        proj.setEndDate(cal.getTime());
        proj.setProduct(prod);
        prod.getProjects().add(proj);
        proj.setIterations(new ArrayList<Iteration>());
        
        Iteration iter = new Iteration();
        iter.setId(3);
        iter.setName("Testi-iteraatio");
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.DATE, 10);
        iter.setStartDate(cal.getTime());
        cal.set(Calendar.DATE, 19);
        iter.setEndDate(cal.getTime());
        iter.setProject(proj);
        proj.getIterations().add(iter);
        
        expect(productDAO.get(1)).andReturn(prod);
        replay(productDAO);
        
        String verifiedJSON = "{name:'Testituote',id:1,type:'product',\n" +
        		"contents:[ \n" +
        		"{name:'Testiprojekti',id:2,type:'project',state:0,startDate:'2008-06-01',endDate:'2008-07-31',contents:[\n" +
        		"{name:'Testi-iteraatio',id:3,type:'iteration',startDate:'2008-07-10',endDate:'2008-07-19'}]\n" +
        		"}\n" +
        		"]\n" +
            		"}";
        
        System.out.println(verifiedJSON);
        
        try {
            String json = timelineBusiness.productContentsToJSON(1);
            System.out.println("--\n" + json);
            assertEquals(verifiedJSON, json);
        }
        catch (ObjectNotFoundException onfe) {
            fail(onfe.getMessage());
        }
        
        verify(productDAO);
    }
    
    public void testProductToJSON_multipleChildren() {
        productDAO = createMock(ProductDAO.class);
        timelineBusiness.setProductDAO(productDAO);
        
        /* Create the test data */
        Product prod = new Product();
        prod.setId(1);
        prod.setName("Testituote");
        prod.setProjects(new ArrayList<Project>());
        prod.setBacklogItems(new ArrayList<BacklogItem>());
        
        Calendar cal = GregorianCalendar.getInstance();
        CalendarUtils.setHoursMinutesAndSeconds(cal, 0, 0, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.YEAR, 2008);
        
        Project proj = new Project();
        proj.setId(2);
        proj.setName("Testiprojekti");
        proj.setStartDate(cal.getTime());
        proj.setStatus(Status.CHALLENGED);
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.DATE, 31);
        proj.setEndDate(cal.getTime());
        proj.setProduct(prod);
        prod.getProjects().add(proj);
        
        expect(productDAO.get(1)).andReturn(prod);
        replay(productDAO);
        
        String verifiedJSON = "{name:'Testituote',id:1,type:'product',\n" +
                        "contents:[ \n" +
                        "{name:'Testiprojekti',id:2,type:'project',state:1,startDate:'2008-06-01',endDate:'2008-07-31',contents:[\n" +
                        "]\n}\n" +
                        "]\n" +
                        "}";
        
        try {
            assertEquals(verifiedJSON, timelineBusiness.productContentsToJSON(1));
        }
        catch (ObjectNotFoundException onfe) {
            fail(onfe.getMessage());
        }
        
        verify(productDAO);
    }

    public void testProductToJSON_complexName() {
        productDAO = createMock(ProductDAO.class);
        timelineBusiness.setProductDAO(productDAO);
        
        /* Create the test data */
        Product prod = new Product();
        prod.setId(1);
        prod.setName("T\ne's\rtituo''te");
        prod.setProjects(new ArrayList<Project>());
        prod.setBacklogItems(new ArrayList<BacklogItem>());
        
        Calendar cal = GregorianCalendar.getInstance();
        CalendarUtils.setHoursMinutesAndSeconds(cal, 0, 0, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.YEAR, 2008);
        
        Project proj = new Project();
        proj.setId(2);
        proj.setName("Testiprojekti");
        proj.setStatus(Status.CRITICAL);
        proj.setStartDate(cal.getTime());
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.DATE, 31);
        proj.setEndDate(cal.getTime());
        proj.setProduct(prod);
        prod.getProjects().add(proj);
        
        expect(productDAO.get(1)).andReturn(prod);
        replay(productDAO);
        
        String verifiedJSON = "{name:'T\\ne\\\'s\\rtituo\\\'\\\'te',id:1,type:'product',\n" +
                        "contents:[ \n" +
                        "{name:'Testiprojekti',id:2,type:'project',state:2,startDate:'2008-06-01',endDate:'2008-07-31',contents:[\n]\n}\n" +
                        "]\n" +
                        "}";
              
        try {
            String json = timelineBusiness.productContentsToJSON(1);
            assertEquals(verifiedJSON, json);
        }
        catch (ObjectNotFoundException onfe) {
            fail(onfe.getMessage());
        }
        
        verify(productDAO);
    }
}
