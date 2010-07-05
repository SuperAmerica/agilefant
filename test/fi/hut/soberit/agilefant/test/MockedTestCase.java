package fi.hut.soberit.agilefant.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.AgilefantUserDetails;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class MockedTestCase {

    public void setUpMocks() {

    }

    protected List<Object> findMockedFields() {
        List<Object> mocks = new ArrayList<Object>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Mock.class)) {
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
        for (Object obj : this.findMockedFields()) {
            EasyMock.verify(obj);
        }
    }

    protected void replayAll() {
        for (Object obj : this.findMockedFields()) {
            EasyMock.replay(obj);
        }
    }
    
    @After
    @SuppressWarnings("serial")
    public void clearLoggedInUser() {
        SecurityUtil.setLoggedUser(null);
        SecurityContextHolder.setContext(new SecurityContext() {
            public void setAuthentication(Authentication arg0) {
            }      
            public Authentication getAuthentication() {
                return null;
            }
        });
    }

    @SuppressWarnings("serial")
    protected void setCurrentUser(User user) {
        SecurityUtil.setLoggedUser(user);
        final AgilefantUserDetails ud = new AgilefantUserDetails(user);
        SecurityContextHolder.setContext(new SecurityContext() {
            public void setAuthentication(Authentication arg0) {
            }      
            public Authentication getAuthentication() {
                return new Authentication() {

                    public String getName() {
                        return null;
                    }

                    public void setAuthenticated(boolean arg0)
                            throws IllegalArgumentException {
                    }

                    public boolean isAuthenticated() {
                        return false;
                    }

                    public Object getPrincipal() {
                        return null;
                    }

                    public Object getDetails() {
                        return ud;
                    }

                    public Object getCredentials() {
                        return null;
                    }

                    public GrantedAuthority[] getAuthorities() {
                        return null;
                    }
                };
            }
        });
    }
}
