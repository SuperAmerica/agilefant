package fi.hut.soberit.agilefant.readonly;

import java.io.IOException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

import org.hibernate.SessionFactory;
import org.hibernate.Session;

import fi.hut.soberit.agilefant.db.hibernate.IterationDAOHibernate;
import fi.hut.soberit.agilefant.model.Iteration;


public class ReadonlyFilter extends GenericFilterBean {
    
    @Transactional(readOnly = true)
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        // Our passed-in requests and responses are HttpServlet requests and responses.
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest reqt = (HttpServletRequest) request;
        
        // Create a Data Access Object instance and open a Hibernate session.
        IterationDAOHibernate iterationDao = new IterationDAOHibernate();;
        SessionFactory sessionFactory;
        try {
            sessionFactory = (SessionFactory) new InitialContext().lookup("hibernateSessionFactory");
            iterationDao.setSessionFactory(sessionFactory);
        } catch (NamingException e) {
            e.printStackTrace();
            return;
        }
        Session session = sessionFactory.openSession();
        
        // Fetch url token from request.
        String token = getTokenFromUrl(reqt.getRequestURL().toString());
        
        if (iterationDao.isValidReadonlyToken(token)) {
            session.disconnect();
            session.close();
            resp.sendRedirect("/agilefant/ROIteration.action?readonlyToken=" + token);
        } else if (reqt.getRequestURL().toString().contains("ROIteration")) {
            //do nothing
            System.out.println("Here");
        } else {
            // Token is not valid, so redirect to login page.
            resp.sendRedirect("/agilefant/login.jsp");
        }
    }
    
    private String getTokenFromUrl(String url) {
        if(url != null) {
            int tokenStart = url.indexOf("token/");
            String token = url.substring(tokenStart + "token/".length());
            return token;
        }
        else 
            return "";
    }

}
