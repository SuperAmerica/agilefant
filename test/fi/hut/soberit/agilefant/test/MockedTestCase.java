package fi.hut.soberit.agilefant.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;


public class MockedTestCase {

    public void setUpMocks() {
        
    }
    protected List<Object> findMockedFields() {
        List<Object> mocks = new ArrayList<Object>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(Mock.class)) {
                boolean access = field.isAccessible();
                try {
                    field.setAccessible(true);
                    mocks.add(field.get(this));
                } catch (Exception e) {
                    Assert.fail("Mock test runner failed");
                } finally {
                    field.setAccessible(access);
                }
            }
        }
        return mocks;
    }
    
    protected void verifyAll() {
        for(Object obj : this.findMockedFields()) {
            EasyMock.verify(obj);
        }
    }
    
    protected void replayAll() {
        for(Object obj : this.findMockedFields()) {
            EasyMock.replay(obj);
        }
    }
}
