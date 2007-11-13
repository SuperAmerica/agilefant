package fi.hut.soberit.agilefant.util;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.web.UserAction;
import fit.Fixture;

/**
 * This class has something to do with Fitnesse.
 * 
 * @author agilefant
 * 
 */
public class SpringDemoActions extends Fixture {
    private String userFullName;

    private String userLoginName;

    private String userPassword1;

    private String userPassword2;

    private String userResult;

    private FitnesseSpringHelper helper;

    private UserAction userAction;

    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }

    public UserAction getUserAction() {
        return this.userAction;
    }

    public SpringDemoActions() {
        super();
        // Super ugly hack to get instance of the abstract class.
        helper = new FitnesseSpringHelper() {
        };
    }

    public void setUp() throws Exception {
        helper.ownSetUp();
        helper.enableSpring(this);
    }

    public void tearDown() throws Exception {
        helper.ownTearDown();
    }

    public void setComplete() {
        helper.setComplete();
    }

    public void usersName(String userFullName) { // "users full name"
        this.userFullName = userFullName;
    }

    public void usersPassword1(String userPassword) {
        this.userPassword1 = userPassword;
    }

    public void usersPassword2(String userPassword) {
        this.userPassword2 = userPassword;
    }

    public void usersLoginName(String loginName) { // "users login name"
        this.userLoginName = loginName;
    }

    public boolean userCreationSuccesfull() {
        return this.userResult != null
                && this.userResult.equals(Action.SUCCESS);
    }

    public void createUser() {
        this.userAction.create();
        this.userAction.setPassword1(this.userPassword1);
        this.userAction.setPassword2(this.userPassword2);
        this.userAction.getUser().setLoginName(this.userLoginName);
        this.userAction.getUser().setFullName(this.userFullName);
        try {
            this.userResult = this.userAction.store();
        } catch (Exception e) {
            this.userResult = null;
        }
    }
}
