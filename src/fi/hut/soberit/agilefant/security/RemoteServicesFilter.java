package fi.hut.soberit.agilefant.security;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

public class RemoteServicesFilter extends GenericFilterBean {

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
      InitialContext ictx;
      Context environment;
      Boolean remoteServicesEnabled = false;
      try {
          ictx = new InitialContext();
          environment = (Context)ictx.lookup("java:comp/env");
          remoteServicesEnabled = (Boolean)environment.lookup("remoteEnabled");
      } catch (NamingException e) {
          throw new ServletException("Remote services not enabled");
      }
      if (!remoteServicesEnabled) {
          throw new ServletException("Remote services not enabled");
      }
      
      chain.doFilter(request, response);
    }

}
