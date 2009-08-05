package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

import fi.hut.soberit.agilefant.annotations.PrefetchId;

public class PrefetchInterceptorTest {

    PrefetchInterceptor prefetchInterceptor;
    
    ActionInvocation mockInvocation;
    ActionContext mockContext;
    
    Map<String, Object> parameters;
    int validObjectId;
    
    /*
     * Need inline classes to test against annotated fields 
     */
    
    @SuppressWarnings("unused")
    private class okIdAction implements Prefetching {       
        @PrefetchId
        private int testId;
        public int calledWith = 0;
        public void initializePrefetchedData(int objectId) {
            calledWith = objectId;
        }
    }
    
    @SuppressWarnings("unused")
    private class invalidTypeAction implements Prefetching {
        @PrefetchId
        private int invalidTestId;
        public int calledWith = 0;
        public void initializePrefetchedData(int objectId) {
            calledWith = objectId;
        }
    }
    
    @Before
    public void setUp_data() {
        validObjectId = 2003;
        
        parameters = new HashMap<String, Object>();
        parameters.put("testId", new String[] { "2003" });
        parameters.put("invalidTestId", new String[] { "this is bullshit" });
    }
    
    @Before
    public void setUp_dependencies() {
        prefetchInterceptor = new PrefetchInterceptor();
        
        mockInvocation = createStrictMock(ActionInvocation.class);
        
        mockContext = createStrictMock(ActionContext.class);
    }

    @Test
    public void notSingleFieldEditable() throws Exception {
        expect(mockInvocation.getAction()).andReturn(new Object());
        expect(mockInvocation.invoke()).andReturn(Action.SUCCESS);
        replay(mockInvocation, mockContext);
        
        prefetchInterceptor.intercept(mockInvocation);
        
        verify(mockInvocation, mockContext);
    }
    
    @Test
    public void happyCase() throws Exception {
        okIdAction mockAction = new okIdAction();
        expect(mockInvocation.getAction()).andReturn(mockAction).times(2);
        expect(mockInvocation.getInvocationContext()).andReturn(mockContext);
        expect(mockContext.getParameters()).andReturn(parameters);
        
        expect(mockInvocation.invoke()).andReturn(Action.SUCCESS);
        
        replay(mockInvocation, mockContext);
        
        prefetchInterceptor.intercept(mockInvocation);
        assertEquals(validObjectId, mockAction.calledWith);

        
        verify(mockInvocation, mockContext);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void incorrectIdException() throws Exception {
        invalidTypeAction mockAction = new invalidTypeAction();
        expect(mockInvocation.getAction()).andReturn(mockAction).times(2);
        expect(mockInvocation.getInvocationContext()).andReturn(mockContext);
        expect(mockContext.getParameters()).andReturn(parameters);
                                
        replay(mockInvocation, mockContext);
        
        prefetchInterceptor.intercept(mockInvocation);
        assertEquals(0, mockAction.calledWith);
        
        verify(mockInvocation, mockContext);
    }
}
