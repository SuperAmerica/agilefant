package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.db.ProductDAO;
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
    private static final String NAME  = "Test Product";
    private static final String NAME2 = "Test Product 2";
    private static final String NAME_SYMBOLS = "test name 2 - non-ascii-symbols äö╚ïâ‼.ê┴åúü";
    private static final String NAME_LONG = "test description 3 - over 256 symbols asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv xzcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf"; 
    private static final String NAME_EMPTY = "";
    private static final String NAME_INJECTION = "<a href=\"http://agilefant.org\">Link fo foo</a>";

    private static final String DESCRIPTION  = "test description 1 - a text with nothing special ";
    private static final String DESCRIPTION2 = "test description 2 - a text with nothing special ";
    private static final String DESCRIPTION_SYMBOLS = "test description 2 - non-ascii-symbols äö╚ïâ‼.ê┴åúü";
    private static final String DESCRIPTION_LONG = "test description 3 - over 256 symbols asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv xzcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv zxcv asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf";
    private static final String DESCRIPTION_EMPTY = "";
    private static final String DESCRIPTION_INJECTION = "<a href=\"http://agilefant.org\">Link fo foo</a>";

//    private static final int PRODUCT_ID  = 123456789;
//    private static final int PRODUCT_ID2 = 987654321;
    private static final int INVALID_PRODUCTID = -1;
    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();
    //private static final User ASSIGNEE = TestUser;
    
    private ProductAction productAction; // The field and setter to be used by Spring 
    private ProductDAO productDAO;
    
    public void setProductDAO(ProductDAO productDAO) {
    	this.productDAO = productDAO;
    }
    
	public void setProductAction(ProductAction productAction){
		this.productAction = productAction;
	}

	/* Checks, if there are any given error countered. */
	@SuppressWarnings("unchecked")  // added for  "Collection<string> errors = ..." line
	private boolean errorFound(String e) {
		Collection<String> errors  = this.productAction.getActionErrors();
		boolean found = false;
		for(String s: errors) {
			if(s.equals(e))
				found = true;
		}
		return found;
	}
	
	/* Methods for setting Product fields. */
	private Product setProductId(int id) {
		Product p = this.productAction.getProduct();
		p.setId(id);
		return p;
	}
/*	private Product setProductName(String name) {
		Product p = this.productAction.getProduct();
		p.setName(name);
		return p;
	}
	private Product setProductFields(String description) {
		Product p = this.productAction.getProduct();
		p.setDescription(description);
		return p;
	}*/
//	private Product setProductFields(int id, String name, String description) {
	private Product setProductFields(String name, String description) {
		Product p = this.productAction.getProduct();
//		p.setId(id);
		p.setName(name);
		p.setDescription(description);
		return p;
	}
	
	/* Method for calling productAction.store that is supposed to work (and is not a target for
	 * testing) Actual testing for method store is done in testStore_XXX -methods */
	private void store() {
		String result = this.productAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
	}

	/* Get all stored Products.
	 * @return all products stored */
	private Collection<Product> getProducts() {
		return this.productDAO.getAll();
	}
	
	/* Get product based on name. */
	private Product getProduct(String name) {
		Product result = null;
		for(Product p: getProducts()) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return result;
	}

	/* Method for calling productAction.create that is supposed to work (and is not a target for 
	 * testing) Actual testing for method create is done in testCreate_XXX -methods */
	private void create() {
		String result = this.productAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
	}
	/* ** Actual test methods ** */
	
	public void testCreate(){
		String result = this.productAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("1 New product had an invalid id", 0, this.productAction.getProductId());
	}
	
	/* Testing store() function.
	 * The rest of the test arsenal is taken within the edit() function tests.
	 * Not reproducing them in store() here, thinking that the basic functionality is the same;
	 * If this is not so, edit must be tested more thoroughly. */
	public void testStore() {
		int n = getProducts().size();

		/* Testing with all fields ok. */
		this.create();
		this.setProductFields(NAME, DESCRIPTION);
		String result = this.productAction.store();
		super.assertEquals("2 store() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("3 The total number of stored products didn't grow up with store().", 
				n+1, getProducts().size());
/*		super.assertNotSame("4 The Stored product should have a proper id number after store()", 
				0, this.productAction.getProduct().getId());*/
		Product storedProduct = this.getProduct(NAME);
		super.assertNotNull("5 Product store(): Product wasn't stored properly (wasn't found)", storedProduct);
		super.assertTrue("6 Product for editing had an invalid name", storedProduct.getName().equals(NAME)); 

		/* Testing with an invalid id.*/
/*		this.create();
		this.setProductFields(INVALID_PRODUCTID, NAME, DESCRIPTION);
		result = this.productAction.store();
	    super.assertFalse("7 Invalid ProductId: store() for a Product with an invalid productId was successful", result.equals(Action.SUCCESS));
		if(result==Action.SUCCESS){
		    n++;
		}else{
		    super.assertEquals("8 The total number of stored products grew with store() though it should have not.", 
				    n+1, getProducts().size());
		}
		super.assertNotSame("9 Invalid ProductId: The Stored product should have a proper id number after store(). Now '-1' is found.", 
				-1, this.productAction.getProduct().getId());
		storedProduct = this.getProduct(INVALID_PRODUCTID);*/

	    /* Testing with no name given */
/*		this.create();
		this.setProductId(12345);
		result = this.productAction.store();
		super.assertFalse("21 Product with no name: store() successed for a product with no name.", result.equals(Action.SUCCESS));
*/
	}
	public void testStore_withoutCreate() {
		String result = this.productAction.store();
		super.assertEquals("22 store() without create() was successful", Action.ERROR, result);
	}
	
/*	public void testEdit() {
		this.create();
		this.setProductFields(NAME, DESCRIPTION);
		this.store();

		//this.productAction.setProduct(null);
		//Product temp = this.getProduct(PRODUCT_ID);
		this.setProductFields(NAME2, DESCRIPTION2);
		String result = this.productAction.edit();
		super.assertEquals("23 edit() was unsuccessful", Action.SUCCESS, result);
		Product fetchedProduct = this.productAction.getProduct();
		super.assertNotNull("24 Product fetched for editing was null", fetchedProduct);
		super.assertTrue("25 Updated product had invalid name", fetchedProduct.getName().equals(NAME2)); 
		super.assertTrue("26 Updated product had invalid description", fetchedProduct.getDescription().equals(DESCRIPTION2)); 
//		super.assertEquals("27 Updated product had invalid id", fetchedProduct.getId(), PRODUCT_ID2); 

		// Testing with special characters in the name.
		this.setProductFields(NAME_SYMBOLS, DESCRIPTION);
		result = this.productAction.edit();
	    super.assertEquals("10 Product with nonASCII name: edit() was unsuccessful for a product with a name with non-ASCII characters.", result, Action.SUCCESS);
	    super.assertEquals("11 Product with nonASCII name: edit() did not get stored right.", this.productAction.getProduct().getName(), NAME_SYMBOLS);

		// Testing with special characters in the description.
		this.setProductFields(NAME, DESCRIPTION_SYMBOLS);
		result = this.productAction.edit();
	    super.assertEquals("12 Product with nonASCII description: edit() was unsuccessful for a product with a dewscription with non-ASCII characters.", result, Action.SUCCESS);

		// Testing with long name.
		this.setProductFields(NAME_LONG, DESCRIPTION);
		result = this.productAction.edit();
	    super.assertEquals("13 Product with nonASCII name: edit() was unsuccessful for a product with a long (>255) name.", result, Action.SUCCESS);
	    String storedName = this.productAction.getProduct().getName();
	    super.assertEquals("14 Product with nonASCII name: edit() did not save the 255 first chars, as it should have.", storedName.substring(0, 255), NAME_LONG);

		// Testing with long description.
		this.setProductFields(NAME, DESCRIPTION_LONG);
		result = this.productAction.edit();
	    super.assertEquals("15 Product with long description: edit() was unsuccessful for a product with a long (>255) description.", result, Action.SUCCESS);
	    super.assertEquals("16 Product with long description: edit() distorted the long description.", 
	    		this.productAction.getProduct().getDescription(), DESCRIPTION_LONG);
	    
		// Testing for html injection in the name.
		this.setProductFields(NAME_INJECTION, DESCRIPTION);
		result = this.productAction.edit();
	    super.assertEquals("17 Product injection: edit() was unsuccessful for a product with a HTML injection in the name.", result, Action.SUCCESS);
	    storedName = this.productAction.getProduct().getName();
	    super.assertFalse("18 Product injection: edit() allows HTML-injection in the name field.", !storedName.equals(NAME_INJECTION) );

		// Testing for html injection in the description.
		this.setProductFields(NAME, DESCRIPTION_INJECTION);
		result = this.productAction.edit();
	    super.assertEquals("19 Product injection: edit() was unsuccessful for a product with a HTML injection in the description.", result, Action.SUCCESS);
		storedName = this.productAction.getProduct().getName();
	    super.assertFalse("20 Product injection: edit() allows HTML-injection in the description field.", !storedName.equals(NAME_INJECTION) );
	}*/

	/*
	public void testEdit_withInvalidId() {
		this.productAction.setProductId(INVALID_PRODUCTID);
		String result = this.productAction.edit();
		assertEquals("Invalid product id didn't result an error.", Action.ERROR, result);
		assertTrue("product.notFound -error not found", 
				errorFound(this.productAction.getText("product.notFound")));
	}
	*/
	
	/* Change the name of previously stored product and update the product.
	 */
	// TODO - should be fixed
/*	public void testStore_withUpdate() {
		this.create();
		this.setProductId(PRODUCT_ID);
		this.store();
		
		Product storedProduct = this.getProduct(PRODUCT_ID);
		this.setProductId(storedProduct.getId());
		this.productAction.setProduct(storedProduct);
		String result = this.productAction.store();
		super.assertEquals("28 store() was unsuccessful", result, Action.SUCCESS);

		Product updatedProduct = this.getProduct(PRODUCT_ID);
		super.assertNotNull("29 Product wasn't stored properly (wasn't found)", updatedProduct);
		super.assertEquals("30 Updated product had invalid name", updatedProduct.getName(), NAME); 
	}*/
	
/*	public void testStore_withDuplicateProductId() {
		// 1st product
		this.create();
		this.setProductId(PRODUCT_ID);
		this.store();

		// create 2nd product with same name
		this.create();
		this.setProductId(PRODUCT_ID);
		String result = this.productAction.store();
		assertNotSame("31 Product with duplicate product id was accepted.", Action.SUCCESS, result);	
		assertTrue("32 Product.productIdInUse -error not found", 
				errorFound(this.productAction.getText("product.productIdInUse")));
	}*/

/*	public void testStore_withNegativeProductId() {
		this.create();
		this.setName(NAME);
		this.setProductId(INVALID_PRODUCTID);
		String result = this.productAction.store();
		assertEquals("33 Negative Product id accepted", Action.ERROR, result);
		assertTrue("34 product.noNegativeProductIdAllowed -error not found", 
				errorFound(this.productAction.getText("product.noNegativeProductIdAllowed")));
	}*/
	
/*	public void testStore_withDifferentPasswords() {
		// TODO ...
	}*/
	
	// TODO - should be fixed
/*	public void testDelete() {
		this.create();
		this.setProductId(PRODUCT_ID);
		String result = this.productAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		
		int n = getProducts().size();
		Product p = getProduct(PRODUCT_ID);
		this.setProductId(p.getId());
		this.productAction.delete();
		super.assertEquals("The number of products didn't decrease with delete().", n-1, getProducts().size());
		
		Product testP = getProduct(PRODUCT_ID);
		super.assertNull("The deleted product wasn't properly deleted", testP);
	}*/
	
/*	public void testDelete_withInvalidId() {
		this.productAction.setProductId(INVALID_PRODUCTID);
		try {
			String result = this.productAction.delete();
			fail("delete() with invalid id " + INVALID_PRODUCTID + " was accepted.");
		}
		catch(IllegalArgumentException iae) {
		}
	}*/
}
