package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.JSONBusinessImpl;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;


public class JSONBusinessTest extends TestCase {
    
    private JSONBusinessImpl jsonBusiness = new JSONBusinessImpl();
    
    private UserBusiness userBusiness;
    private BacklogBusiness backlogBusiness;
    private BacklogItemBusiness backlogItemBusiness;
    private TeamBusiness teamBusiness;
    
    public void testGetUserChooserJSON() {
        userBusiness = createMock(UserBusiness.class);
        backlogBusiness = createMock(BacklogBusiness.class);
        backlogItemBusiness = createMock(BacklogItemBusiness.class);
        teamBusiness = createMock(TeamBusiness.class);
        jsonBusiness.setBacklogBusiness(backlogBusiness);
        jsonBusiness.setBacklogItemBusiness(backlogItemBusiness);
        jsonBusiness.setUserBusiness(userBusiness);
        jsonBusiness.setTeamBusiness(teamBusiness);
        
        String userJson = "users:[{\"enabled\":true,\"fullName\":\"Pertti Pasianssi\"," +
                        "\"id\":15,\"initials\":\"Pera\"}," +
                        "{\"enabled\":true,\"fullName\":\"Timo Testikäyttäjä\"," +
        		"\"id\":10,\"initials\":\"Timo\"}]";
        String teamJson = "teams:[{\"class\":\"fi.hut.soberit.agilefant.model.Team\"," +
        		"\"description\":\"\",\"id\":3," +
        		"\"name\":\"ATMAN\",\"users\":" +
        		"[{\"id\":10}]}]";
        String assJson = "assignments:[10]";
        String respJson = "responsibles:[10,15]";
        String overJson = "overheads:{\"10\":3600}";
        String backlogJson = "backlog:{\"defaultOverhead\":1000,\"id\":200}";
        
        // Create the test data
        Project proj = new Project();
        proj.setId(200);
        proj.setAssignments(new ArrayList<Assignment>());
        proj.setDefaultOverhead(new AFTime(1000));
        
        User user1 = new User();
        user1.setId(10);
        user1.setFullName("Timo Testikäyttäjä");
        user1.setInitials("Timo");
        user1.setEnabled(true);
        
        User user2 = new User();
        user2.setId(15);
        user2.setFullName("Pertti Pasianssi");
        user2.setInitials("Pera");
        user2.setEnabled(true);
        
        Assignment ass = new Assignment();
        ass.setId(123);
        ass.setBacklog(proj);
        ass.setUser(user1);
        ass.setDeltaOverhead(new AFTime(3600));
        proj.getAssignments().add(ass);
        
        BacklogItem bli = new BacklogItem();
        bli.setId(2000);
        bli.setResponsibles(new ArrayList<User>());
        bli.getResponsibles().add(user1);
        bli.getResponsibles().add(user2);
        
        Team team = new Team();
        team.setId(3);
        team.setName("ATMAN");
        team.setDescription("");
        team.setUsers(new ArrayList<User>());
        team.getUsers().add(user1);
        
        List<User> userList = new ArrayList<User>();
        userList.add(user1);
        userList.add(user2);
        
        List<Team> teamList = new ArrayList<Team>();
        teamList.add(team);
        
        try {
            expect(backlogItemBusiness.getBacklogItem(2000)).andReturn(bli);
            expect(backlogBusiness.getBacklog(200)).andReturn(proj);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        expect(userBusiness.getAllUsers()).andReturn(userList);
        expect(teamBusiness.getAllTeams()).andReturn(teamList);
        
        replay(backlogItemBusiness);
        replay(backlogBusiness);
        replay(teamBusiness);
        replay(userBusiness);
        
        String realJson = jsonBusiness.getUserChooserJSON(2000, 200);
        String verifiedJson = "{" + userJson + "," + teamJson + "," + assJson + "," + respJson + "," 
            + overJson + "," + backlogJson + "}";
        
        System.out.println(verifiedJson + "\n\n" + realJson);
        
        assertEquals(verifiedJson, realJson);
        
        verify(backlogItemBusiness);
        verify(backlogBusiness);
        verify(teamBusiness);
        verify(userBusiness);
    }
}
