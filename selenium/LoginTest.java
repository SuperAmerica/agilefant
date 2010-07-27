import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class LoginTest extends SeleneseTestCase {
    public void setUp() throws Exception {
        setUp("http://localhost:8080/", "*chrome");
    }

    public void testFaultyLogin() throws Exception {
        selenium.open("/agilefant/login.jsp");
        selenium.type("username", "achmed");
        selenium.type("j_password", "abbas");
        selenium.click("//input[@value='Log in']");
        selenium.waitForPageToLoad("30000");
        verifyTrue(selenium
                .isTextPresent("Invalid username or password, please try again."));
    }

    public void testLogin() throws Exception {
        selenium.open("/agilefant/login.jsp");
        selenium.type("username", "admin");
        selenium.type("j_password", "secret");
        selenium.click("_spring_security_remember_me");
        selenium.click("//input[@value='Log in']");
        selenium.waitForPageToLoad("30000");
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
}
