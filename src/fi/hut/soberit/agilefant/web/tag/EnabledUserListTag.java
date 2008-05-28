package fi.hut.soberit.agilefant.web.tag;

import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.UserComparator;

public class EnabledUserListTag extends SpringTagSupport {
    private static final long serialVersionUID = 8356132939350106553L;

    public static final String USER_LIST_KEY = "enabledUserList";

    private UserBusiness userBusiness;

    @Override
    public int doStartTag() throws JspException {
        userBusiness = (UserBusiness) super.getApplicationContext().getBean("userBusiness");

        List<User> list = (List<User>) userBusiness.getEnabledUsers();

        Collections.sort(list, new UserComparator());

        super.getPageContext().setAttribute(EnabledUserListTag.USER_LIST_KEY, list);
        return Tag.EVAL_BODY_INCLUDE;
    }
}
