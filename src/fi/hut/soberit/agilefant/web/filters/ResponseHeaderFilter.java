package fi.hut.soberit.agilefant.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderFilter implements Filter {

    private int expirationTime; 
    
    public void init(FilterConfig filterConfig) throws ServletException {
        expirationTime = Integer.parseInt((filterConfig.getInitParameter("expirationTime")));
    }
    
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        
        HttpServletResponse httpResp = (HttpServletResponse)response;
        
        httpResp.addHeader("Cache-Control", "max-age=" + expirationTime);
        
        filterChain.doFilter(request, response);
    }

    public void destroy() {
    }

}
