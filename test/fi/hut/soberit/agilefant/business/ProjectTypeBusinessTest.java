package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.ProjectTypeBusinessImpl;
import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.model.ProjectType;

public class ProjectTypeBusinessTest extends TestCase {

    private ProjectTypeDAO projectTypeDAO;

    private ProjectTypeBusinessImpl projectTypeBusiness;

    public void setUp() {
        projectTypeDAO = createMock(ProjectTypeDAO.class);
        projectTypeBusiness = new ProjectTypeBusinessImpl();
        projectTypeBusiness.setProjectTypeDAO(projectTypeDAO);
    }

    public void testCount() {
        expect(projectTypeDAO.count()).andReturn(10);
        replay(projectTypeDAO);
        assertEquals(10, projectTypeBusiness.count());
        verify(projectTypeDAO);
    }

    public void testGet() {
        ProjectType type = new ProjectType();
        expect(projectTypeDAO.get(10)).andReturn(type);
        replay(projectTypeDAO);
        assertEquals(type, projectTypeBusiness.get(10));
        verify(projectTypeDAO);
    }

    public void testStore() {
        ProjectType type = new ProjectType();
        projectTypeDAO.store(type);
        replay(projectTypeDAO);
        projectTypeBusiness.store(type);
        verify(projectTypeDAO);
    }

    public void testGetAll() {
        List<ProjectType> types = new ArrayList<ProjectType>();
        types.add(new ProjectType());
        types.add(new ProjectType());
        expect(projectTypeDAO.getAll()).andReturn(types);
        replay(projectTypeDAO);
        assertEquals(types, projectTypeBusiness.getAll());
        verify(projectTypeDAO);
    }

}
