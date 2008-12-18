package fi.hut.soberit.agilefant.web.tag;

import java.util.Collections;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.UserComparator;

public class UserListTag extends SpringTagSupport {
    private static final long serialVersionUID = 8356132939350106553L;

    public static final String USER_LIST_KEY = "userList";

    private UserDAO userDAO;

    @Override
    public int doStartTag() throws JspException {
        userDAO = (UserDAO) super.getApplicationContext().getBean("userDAO");

        List<User> list = (List<User>) userDAO.getAll();

        Collections.sort(list, new UserComparator());

        super.getPageContext().setAttribute(UserListTag.USER_LIST_KEY, list);
        return Tag.EVAL_BODY_INCLUDE;
    }
}
