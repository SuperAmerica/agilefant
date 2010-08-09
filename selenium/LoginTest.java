import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class LoginTest extends SeleneseTestCase {
    public void setUp() throws Exception {
        setUp("http://localhost:8080/", "*chrome");
    }

    private void loginWithCredentials(String username, String password) {
        selenium.open("/agilefant/login.jsp");
        selenium.type("username", username);
        selenium.type("j_password", password);
        selenium.click("//input[@value='Log in']");
        selenium.waitForPageToLoad("30000");
    }
    
    private void loginAsAdmin() {
        loginWithCredentials("admin", "secret");
    }
    
    public void testFaultyLogin() throws Exception {
        loginWithCredentials("achmed", "achmed");
        verifyTrue(selenium
                .isTextPresent("Invalid username or password, please try again."));
    }

    public void testLogin() throws Exception {
        loginAsAdmin();

        for (int second = 0;; second++) {
            if (second >= 60)
                fail("timeout");
            try {
                if (selenium.isElementPresent("bodyWrapper"))
                    break;
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        verifyTrue(selenium.isTextPresent("Agilefant help page"));
    }
    
    public void testCreateProduct() throws Exception {
        loginAsAdmin();
        selenium.click("createNewMenuLink");
        selenium.click("createNewProduct");
        for (int second = 0;; second++) {
            if (second >= 60) fail("timeout");
            try { if (selenium.isElementPresent("editor-2")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }

        selenium.click("editor-2");
        selenium.type("editor-2", "A New Product");
        selenium.click("//button[@type='button']");
        
        selenium.click("link=Products");
        for (int second = 0;; second++) {
                if (second >= 60) fail("timeout");
                try { if (selenium.isElementPresent("link=A New Product")) break; } catch (Exception e) {}
                Thread.sleep(1000);
        }

        selenium.click("link=A New Product");
        selenium.waitForPageToLoad("30000");
        verifyEquals("Product: A New Product", selenium.getText("//div[@id='bodyWrapper']/h2"));
    }
}
