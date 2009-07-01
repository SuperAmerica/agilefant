package fi.hut.soberit.agilefant.web;

import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

public class SingleFieldEditInterceptorTest {

    SingleFieldEditInterceptor singleFieldEditInterceptor;
    
    SingleFieldEditable mockAction;
    ActionInvocation mockInvocation;
    ActionContext mockContext;
    
    Map<String, Object> parameters;
    int validObjectId;
    
    @Before
    public void setUp_data() {
        validObjectId = 2003;
        
        parameters = new HashMap<String, Object>();
        parameters.put("testId", new String[] { "2003" });
        parameters.put("invalidTestId", new String[] { "this is bullshit" });
    }
    
    @Before
    public void setUp_dependencies() {
        singleFieldEditInterceptor = new SingleFieldEditInterceptor();
        
        mockInvocation = createStrictMock(ActionInvocation.class);
        mockAction = createStrictMock(SingleFieldEditable.class);
        
        mockContext = createStrictMock(ActionContext.class);
    }

    @Test
    public void notSingleFieldEditable() throws Exception {
        expect(mockInvocation.getAction()).andReturn(new Object());
        expect(mockInvocation.invoke()).andReturn(Action.SUCCESS);
        replay(mockInvocation, mockAction, mockContext);
        
        singleFieldEditInterceptor.intercept(mockInvocation);
        
        verify(mockInvocation, mockAction, mockContext);
    }
    
    @Test
    public void happyCase() throws Exception {
        expect(mockInvocation.getAction()).andReturn(mockAction).times(2);
        expect(mockInvocation.getInvocationContext()).andReturn(mockContext);
        expect(mockContext.getParameters()).andReturn(parameters);
        
        expect(mockAction.getIdFieldName()).andReturn("testId");
        mockAction.initializeDataForEditing(validObjectId);
        
        expect(mockInvocation.invoke()).andReturn(Action.SUCCESS);
        
        replay(mockInvocation, mockAction, mockContext);
        
        singleFieldEditInterceptor.intercept(mockInvocation);
        
        verify(mockInvocation, mockAction, mockContext);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void incorrectIdException() throws Exception {
        expect(mockInvocation.getAction()).andReturn(mockAction).times(2);
        expect(mockInvocation.getInvocationContext()).andReturn(mockContext);
        expect(mockContext.getParameters()).andReturn(parameters);
        
        expect(mockAction.getIdFieldName()).andReturn("invalidTestId");
                        
        replay(mockInvocation, mockAction, mockContext);
        
        singleFieldEditInterceptor.intercept(mockInvocation);
        
        verify(mockInvocation, mockAction, mockContext);
    }
}
