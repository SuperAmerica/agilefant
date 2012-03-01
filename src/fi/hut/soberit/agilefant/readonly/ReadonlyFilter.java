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

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

import org.hibernate.SessionFactory;
import org.hibernate.Session;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.db.hibernate.IterationDAOHibernate;
import fi.hut.soberit.agilefant.model.Iteration;


public class ReadonlyFilter extends GenericFilterBean {
    
    @Transactional(readOnly = true)
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        // These are HttpServlet requests and responses.
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest reqt = (HttpServletRequest) request;
        
        // Trying to set something up get things from the db. 
        IterationDAOHibernate iterationDao = new IterationDAOHibernate();;
        SessionFactory sessionFactory;
        
        try {
            sessionFactory = (SessionFactory) new InitialContext().lookup("hibernateSessionFactory");
            iterationDao.setSessionFactory(sessionFactory);
        } catch (NamingException e) {
            // TODO Auto-generated catch block
            // Need to add error message here. 
            e.printStackTrace();
            return;
        }
        
        Session session = sessionFactory.openSession();
        
        // Fetch url token from request.
        String urlToken = reqt.getPathInfo().substring(1);
        
        // Make sure url token actually exists.
        Integer iterationId = 9; // TODO @DF Dummy... need to fetch this from our db table once things are actually working.
       
        if (iterationId != null) {
            Iteration i = iterationDao.retrieveDeep(iterationId);
            System.out.print(i.toString());
            
            // Okay, we have successfully pulled iteration data from the db... now what? 
            
            //resp.sendRedirect("http://www.google.ca/" + urlToken);
            
        } else {
            // Token is not valid, so redirect to login page.
            
            resp.sendRedirect("login.jsp");
            
            //chain.doFilter(request, response);
        }
        
        session.close();
    }

}
