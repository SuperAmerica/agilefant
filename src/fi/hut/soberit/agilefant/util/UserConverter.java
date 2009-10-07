package fi.hut.soberit.agilefant.util;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;

import fi.hut.soberit.agilefant.business.UserBusiness;

public class UserConverter extends StrutsTypeConverter {
    @Autowired
    private UserBusiness userBusiness;

    @SuppressWarnings("unchecked")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        int id = Integer.parseInt(values[0]);
        return userBusiness.retrieve(id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String convertToString(Map context, Object o) {
        if (o == null)
            return null;
        return o.toString();
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
}
