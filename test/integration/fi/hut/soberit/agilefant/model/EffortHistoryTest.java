package fi.hut.soberit.agilefant.model;

import fi.hut.soberit.agilefant.util.TestUtility;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import java.sql.Date;
import java.util.GregorianCalendar;

public class EffortHistoryTest extends SpringTestCase {
	private EffortHistoryDAO effortHistoryDAO;
	private ProductDAO productDAO;
		
	/**
	 * Setter for Spring IoC <br/>
	 * Injected effortHistory item DAO is used for testing 
	 * @param effortHistoryDAO effortHistory item DAO to be set
	 */
	public void setEffortHistoryDAO(EffortHistoryDAO effortHistoryDAO){
		this.effortHistoryDAO = effortHistoryDAO;	
	}
	
	/**
	 * Setter for Spring IoC <br/>
	 * Injected product item DAO is used for testing 
	 * @param productDAO product item DAO to be set
	 */
	public void setProductDAO(ProductDAO productDAO){
		this.productDAO = productDAO;	
	}
	
	/**
	 * Clears the database from test data.
	 * We must clear database manually because of we need transactions to
	 * complete.
	 */
	protected void onTearDownInTransaction() throws Exception {
		super.setComplete();
		TestUtility.clearData(productDAO, effortHistoryDAO);
	}
	
	public void testDAO(){
		Integer id = new Integer(0);
		setComplete();
		
		EffortHistory effortHistory = new EffortHistory();
		Date date = new Date((new GregorianCalendar()).getTimeInMillis());
		Product[] productArray;

		/* Check initial conditions*/
		assertNotNull("EffortHistory DAO not injected", effortHistoryDAO);
				
		/* Test creation */
		TestUtility.createTestProduct(0, productDAO);
		productArray = (Product[]) productDAO.getAll().toArray(new Product[0]);
		
		effortHistory.setBacklog(productArray[0]);
		effortHistory.setDate(date);
		effortHistory.setEffortLeft(600000);
		
		id = (Integer) effortHistoryDAO.create(effortHistory);
		assertTrue("Id wasn't returned", id.intValue() != 0);
		
		effortHistory = effortHistoryDAO.getByDate(date);
		
		assertTrue("Could not retrieve effortHistory object from database",
				effortHistoryDAO.getAll().size() != 0);
		
		assertNotNull("EffortHistory retrieval by current date failed", 
				effortHistory);
		
		assertEquals("Mismatch in effort left value", 
				effortHistory.getEffortLeft(), 600000);
		
		endTransaction();
		startNewTransaction();
		setComplete();
		
		/* Test connection from backlog to effortHistory */
		productArray = (Product[]) productDAO.getAll().toArray(new Product[0]);
		assertNotNull("ProductArray is empty", productArray);
		
		assertFalse("product.effortHistory is empty", 
				productArray[0].getEffortHistory().isEmpty());
		
		/* Test cascade delete */
		productDAO.remove(productArray[0]);
		assertTrue("Cascade delete failed", 
				effortHistoryDAO.getAll().isEmpty());
		
		/* Clear database from effort history testdata */
		for(EffortHistory i : effortHistoryDAO.getAll()){
			effortHistoryDAO.remove(i);
		}
		
		assertTrue("EffortHistory not removed", 
				effortHistoryDAO.getAll().isEmpty());
		
		endTransaction();
		startNewTransaction();

	}
}
