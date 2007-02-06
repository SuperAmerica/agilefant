package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.util.SpringTestCase;

import java.util.Collection;
import java.util.HashSet;

/**
 * JUnit integration test class for testing class ProductAction
 * 
 * @author smkoski2
 */
public class ProductActionTest extends SpringTestCase {
	private static final String TEST_NAME = "Test Product";
	private static final String TEST_NAME_SYMBOLS = "test name 2 - non-ascii-symbols äö╚ïâ‼.ê┴åúü";
	private static final String TEST_NAME_LONG = "test description 3 - over 256 symbols asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv xzcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf"; 
    private static final String NAME_EMPTY = "";
    private static final String NAME_INJECTION = "<a href=\"http://agilefant.org\">Link fo foo</a>";

	private static final String DESCRIPTION = "test description 1 - a text with no ";
    private static final String DESCRIPTION_SYMBOLS = "test description 2 - non-ascii-symbols äö╚ïâ‼.ê┴åúü";
    private static final String DESCRIPTION_LONG = "test description 3 - over 256 symbols asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv xzcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf";
    private static final String DESCRIPTION_EMPTY = "";
    private static final String DESCRIPTION_INJECTION = "<a href=\"http://agilefant.org\">Link fo foo</a>";

    private static final int productId = 1234567890;
	private static final int INVALID_PRODUCTID = -1;
    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();
//    private static final User ASSIGNEE = TestUser;
    
	private ProductAction productAction; // The field and setter to be used by Spring 

	public void setProductAction(ProductAction productAction){
		this.productAction = productAction;
	}

	/*
	 * Checks, if there are any given error countered. 
	 */
	@SuppressWarnings("unchecked")  // added for  "Collection<string> errors = ..." line
	private boolean errorFound(String e) {
		Collection<String> errors  = productAction.getActionErrors();
		boolean found = false;
		for(String s: errors) {
			if(s.equals(e))
				found = true;
		}
		return found;
	}

	private Product setProductId(int productId) {
		Product p = productAction.getProduct();
		p.setId(productId);
		return p;
	}
	
	/*
	 * Method for calling productAction.create that is supposed to work (and 
	 * is not a target for testing) Actual testing for method create
	 * is done in testCreate_XXX -methods
	 */
	private void create() {
		String result = productAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
	}

	/* Method for calling productAction.store that is supposed to work (and 
	 * is not a target for testing) Actual testing for method store
	 * is done in testStore_XXX -methods
	 */
	private void store() {
		String result = productAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
	}

	/* Get all stored Products.
	 * @return all products stored
	 */
	private Collection<Product> getProducts() {
		return this.productAction.getProductDAO().getAll();
	}
	
	/* Get product based on id.
	 * Check whether the id is unique.
	 */
	private Product getProduct(int id) {
		Product result = null;
		for(Product p: getProducts()) {
			if(p.getId()==productId) {
				if(result == null)
					result = p;
				else
					fail("Multiple Products with same id : " + id);
			}
		}
		return result;
	}

	/*** Actual test methods **/
	
	public void testCreate(){
		String result = productAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("New product had an invalid id", 0, productAction.getProductId());
	}
	
	public void testStore() {
		this.create();
		this.setProductId(productId);
		int n = getProducts().size();
		String result = productAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("The total number of stored productss didn't grow up with store().", 
				n+1, getProducts().size());
		super.assertNotSame("The Stored product should have a proper id number after store()", 
				0, productAction.getProduct().getId());
		Product storedProduct = this.getProduct(productId);
		super.assertNotNull("Product wasn't stored properly (wasn't found)", storedProduct);
		super.assertTrue("Product for editing had an invalid name", storedProduct.getName().equals(TEST_NAME)); 
	}
	
	public void testStore_withoutCreate() {
		String result = productAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);		
	}
	
	public void testEdit() {
		this.create();
		this.setProductId(productId);
		this.store();

		productAction.setProduct(null);
		Product temp = this.getProduct(productId);
		productAction.setProductId(temp.getId());
		String result = productAction.edit();
		super.assertEquals("edit() was unsuccessful", result, Action.SUCCESS);
		Product fetchedProduct = productAction.getProduct();
		super.assertNotNull("Product fetched for editing was null", fetchedProduct);
		super.assertTrue("Updated product had invalid name", fetchedProduct.getName().equals(TEST_NAME)); 
	}
	
	public void testEdit_withInvalidId() {
		productAction.setProductId(INVALID_PRODUCTID);
		String result = productAction.edit();
		assertEquals("Invalid product id didn't result an error.", Action.ERROR, result);
		assertTrue("product.notFound -error not found", 
				errorFound(productAction.getText("product.notFound")));
	}
	
	/*
	 * Change the name of previously stored product and update the product.
	 */
	public void testStore_withUpdate() {
		this.create();
		this.setProductId(productId);
		this.store();
		
		Product storedProduct = this.getProduct(productId);
		productAction.setProductId(storedProduct.getId());
		productAction.setProduct(storedProduct);
		String result = productAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);

		Product updatedProduct = this.getProduct(productId);
		super.assertNotNull("Product wasn't stored properly (wasn't found)", updatedProduct);
		super.assertTrue("Updated product had invalid name", 
						 updatedProduct.getName().equals(TEST_NAME)); 
	}
	

	public void testStore_withDuplicateLProductId() {
		// 1st product
		this.create();
		this.setProductId(productId);
		this.store();

		// create 2nd product with same login name
		this.create();
		this.setProductId(productId);
		String result = productAction.store();
		assertNotSame("Product with duplicate product id was accepted.", Action.SUCCESS, result);	
		assertTrue("product.productIdInUse -error not found", 
				errorFound(productAction.getText("product.productIdInUse")));
	}

	public void testStore_withNegativeProductId() {
		this.create();
		this.setName("TEST_NAME");
		String result = productAction.store();
		assertEquals("Negative Product id accepted", Action.ERROR, result);
		assertTrue("product.noNegativeProductIdAllowed -error not found", 
				errorFound(productAction.getText("product.noNegativeProductIdAllowed")));
	}
	
	public void testStore_withDifferentPasswords() {
		// ...
	}
	
	public void testDelete() {
		this.create();
		this.setProductId(productId);
		String result = productAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		
		int n = getProducts().size();
		Product p = getProduct(productId);
		productAction.setProductId(p.getId());
		productAction.delete();
		super.assertEquals("The number of products didn't decrease with delete().", n-1, getProducts().size());
		
		Product testP = getProduct(productId);
		super.assertNull("The deleted product wasn't properly deleted", testP);
	}
	
	public void testDelete_withInvalidId() {
		productAction.setProductId(INVALID_PRODUCTID);
		try {
			productAction.delete();
			fail("delete() with invalid id " + INVALID_PRODUCTID + " was accepted.");
		}
		catch(IllegalArgumentException iae) {
		}
	}
}
