package fi.hut.soberit.agilefant.web;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.User;

public class AjaxExceptionHandlerTest {
    
    private AjaxExceptionHandler ajaxExceptionHandler;
    
    @SuppressWarnings("serial")
    @Before
    public void setUp_handler() {
        ajaxExceptionHandler = new AjaxExceptionHandler() {
            @Override
            public String getText(String aTextName) {
                return "msg_" + aTextName;
            }
        };
    }
    
    @Test
    public void handle_genericException() {
        ajaxExceptionHandler.setException(new Exception("Some exception"));
        
        assertEquals(AjaxExceptionHandler.genericExceptionResult,
                ajaxExceptionHandler.handle());
        
        assertEquals("Some exception", ajaxExceptionHandler.getErrorMessage());
    }
    
    @Test
    public void handle_objectNotFound() {
        ajaxExceptionHandler.setException(new ObjectNotFoundException("Not found!", User.class));
        
        assertEquals(AjaxExceptionHandler.objectNotFoundResult,
                ajaxExceptionHandler.handle());
        
        assertEquals("msg_user.notFound", ajaxExceptionHandler.getErrorMessage());
    }
    
    @Test
    public void handleObjectNotFoundException_withI18nKey() {
        ObjectNotFoundException onfe
            = new ObjectNotFoundException("Text not found", "text.notFound");
        
        assertEquals(AjaxExceptionHandler.objectNotFoundResult,
                ajaxExceptionHandler.handleObjectNotFoundException(onfe));
        
        assertEquals("msg_text.notFound", ajaxExceptionHandler.getErrorMessage());
    }
    
    @Test
    public void handleObjectNotFoundException_withoutI18nKey() {
        ObjectNotFoundException onfe
            = new ObjectNotFoundException("Text not found");
        
        assertEquals(AjaxExceptionHandler.objectNotFoundResult, 
                ajaxExceptionHandler.handleObjectNotFoundException(onfe));
        assertEquals("msg_unknown.notFound", ajaxExceptionHandler.getErrorMessage());
    }
    
}
