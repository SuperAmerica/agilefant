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

import fi.hut.soberit.agilefant.db.hibernate.IterationDAOHibernate;
import fi.hut.soberit.agilefant.model.Iteration;

public class ReadonlyFilter extends GenericFilterBean {

    public static final String LAST_URL_REDIRECT_KEY = ReadonlyFilter.class.getName() + "LAST_URL_REDIRECT_KEY";
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        // These are HttpServlet requests and responses.
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest reqt = (HttpServletRequest) request;
        
        // Fetch url token from request.
        String urlToken = reqt.getPathInfo().substring(1);
        
        // Test url token against db(?) of known read-only urls. 
        // Integer iterationId = fetchIterationIdFromURLToken(urlToken);
        Integer iterationId = 9; // TODO @DF Dummy Need to figure out authentication.
        
        // If url checks out, create iterationTO and supply it somewhere? 
        if (iterationId != null) {
            
            //IterationDAOHibernate iterationDAO = new IterationDAOHibernate();
            //Iteration iteration = iterationDAO.get(9);

            String path = reqt.getPathInfo();
            
            resp.sendRedirect("http://www.google.ca/" + urlToken);
            
        } else {
            
            // Redirect to urltoken failure page? 
            chain.doFilter(request, response);
        }
    }

}
