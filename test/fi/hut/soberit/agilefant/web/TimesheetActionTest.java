package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.BacklogTimesheetNode;


public class TimesheetActionTest {
    
    private Set<Integer> userIds;
    private DateTime startDate;
    private DateTime endDate;
    private TimesheetAction timesheetAction;
    private TimesheetBusiness timesheetBusiness;
    
    @Before
    public void setUp() {
        this.userIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        this.startDate = new DateTime(2009,1,1,1,1,0,0);
        this.endDate = new DateTime(2009,5,1,1,1,0,0);
        timesheetAction = new TimesheetAction();
        timesheetBusiness = createMock(TimesheetBusiness.class);
        timesheetAction.setTimesheetBusiness(timesheetBusiness);
    }
    
    @Test
    public void testGenerateTree_noBacklogsSelected() {
        Set<Integer> productIds = Collections.emptySet();
        timesheetAction.setUserIds(userIds);
        timesheetAction.setStartDate(new DateTime(2009,1,1,1,1,0,0));
        timesheetAction.setEndDate(new DateTime(2009,5,1,1,1,0,0));
        timesheetAction.setProductIds(productIds);
        assertEquals(Action.ERROR, timesheetAction.generateTree());
    }
    
    @Test
    public void testGenerateTree() {
        Set<Integer> productIds = new HashSet<Integer>(Arrays.asList(4,5));
        timesheetAction.setUserIds(userIds);
        timesheetAction.setStartDate(new DateTime(2009,1,1,1,1,0,0));
        timesheetAction.setEndDate(new DateTime(2009,5,1,1,1,0,0));
        timesheetAction.setProductIds(productIds);
        
        List<BacklogTimesheetNode> rootNodes = Collections.emptyList();
        expect(timesheetBusiness.getRootNodes(productIds, startDate, endDate, userIds)).andReturn(rootNodes);
        expect(timesheetBusiness.getRootNodeSum(rootNodes)).andReturn(500L);
        replay(timesheetBusiness);
        assertEquals(Action.SUCCESS, timesheetAction.generateTree());
        assertEquals(rootNodes, timesheetAction.getProducts());
        assertEquals(500L, timesheetAction.getEffortSum());
        verify(timesheetBusiness);
    }
    
    @Test
    public void testGenerateTree_emptyDates() {
        Set<Integer> productIds = new HashSet<Integer>(Arrays.asList(4,5));
        timesheetAction.setUserIds(userIds);
        timesheetAction.setStartDate(null);
        timesheetAction.setEndDate(null);
        timesheetAction.setProductIds(productIds);
        
        List<BacklogTimesheetNode> rootNodes = Collections.emptyList();
        expect(timesheetBusiness.getRootNodes(productIds, null, null, userIds)).andReturn(rootNodes);
        expect(timesheetBusiness.getRootNodeSum(rootNodes)).andReturn(500L);
        replay(timesheetBusiness);
        assertEquals(Action.SUCCESS, timesheetAction.generateTree());
        assertEquals(rootNodes, timesheetAction.getProducts());
        assertEquals(500L, timesheetAction.getEffortSum());
        verify(timesheetBusiness);
    }
    
    @Test
    public void testGetSelectedBacklogs_selectedProjects() {
        Set<Integer> projectIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        Set<Integer> productIds = new HashSet<Integer>(Arrays.asList(4,5));
        timesheetAction.setProductIds(productIds);
        timesheetAction.setOnlyOngoing(false);
        timesheetAction.setProjectIds(projectIds);
        Set<Integer> backlogIds = timesheetAction.getSelectedBacklogs();
        assertEquals(projectIds, backlogIds);
    }
    @Test
    public void testGetSelectedBacklogs_selectedOngoinProjects() {
        Set<Integer> projectIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        Set<Integer> productIds = new HashSet<Integer>(Arrays.asList(4,5));
        timesheetAction.setProductIds(productIds);
        timesheetAction.setOnlyOngoing(true);
        timesheetAction.setProjectIds(projectIds);
        Set<Integer> backlogIds = timesheetAction.getSelectedBacklogs();
        assertEquals(projectIds, backlogIds);
    }
    
    @Test
    public void testGetSelectedBacklogs_selectedProducts() {
        Set<Integer> productIds = new HashSet<Integer>(Arrays.asList(4,5));
        timesheetAction.setProductIds(productIds);
        timesheetAction.setOnlyOngoing(true);
        Set<Integer> backlogIds = timesheetAction.getSelectedBacklogs();
        assertEquals(productIds, backlogIds);
    }
    
    @Test
    public void testGetSelectedBacklogs_selectedIterations() {
        Set<Integer> iterationIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        Set<Integer> projectIds = new HashSet<Integer>(Arrays.asList(4,5));
        timesheetAction.setProjectIds(projectIds);
        timesheetAction.setOnlyOngoing(false);
        timesheetAction.setIterationIds(iterationIds);
        Set<Integer> backlogIds = timesheetAction.getSelectedBacklogs();
        assertEquals(iterationIds, backlogIds);
    }
    
    @Test
    public void testGetSelectedBacklogs_selectedOngoingIterations() {
        Set<Integer> iterationIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        Set<Integer> projectIds = new HashSet<Integer>(Arrays.asList(4,5));
        timesheetAction.setProjectIds(projectIds);
        timesheetAction.setOnlyOngoing(true);
        timesheetAction.setIterationIds(iterationIds);
        Set<Integer> backlogIds = timesheetAction.getSelectedBacklogs();
        assertEquals(iterationIds, backlogIds);
    }
    @Test
    public void getSelectedUsers_noUsers() {
        timesheetAction.setUserIds(new HashSet<Integer>());
        assertNotNull(timesheetAction.getSelectedUsers());
        assertEquals(0, timesheetAction.getSelectedUsers().size());
    }
    @Test
    public void getSelectedUsers() {
        Set<Integer> userIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        User user1 = new User();
        User user2 = new User();
        UserBusiness userBusiness = createMock(UserBusiness.class);
        timesheetAction.setUserBusiness(userBusiness);
        timesheetAction.setUserIds(userIds);
        expect(userBusiness.retrieve(1)).andReturn(user1);
        expect(userBusiness.retrieve(2)).andReturn(user2);
        expect(userBusiness.retrieve(3)).andReturn(null);
        replay(userBusiness);
        List<User> actual = timesheetAction.getSelectedUsers();
        assertEquals(2, actual.size());
        verify(userBusiness);
        
    }
}
