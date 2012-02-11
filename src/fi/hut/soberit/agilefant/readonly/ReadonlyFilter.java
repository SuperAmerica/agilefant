package fi.hut.soberit.agilefant.readonly;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;


public class ReadonlyFilter extends GenericFilterBean {

    public static final String LAST_URL_REDIRECT_KEY = ReadonlyFilter.class.getName() + "LAST_URL_REDIRECT_KEY";
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
      
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.sendRedirect("http://www.google.ca");

    }

}
