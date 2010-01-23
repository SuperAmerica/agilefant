package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.LabelBusinessImpl;
import fi.hut.soberit.agilefant.db.LabelDAO;
import fi.hut.soberit.agilefant.model.Label;

public class LabelBusinessTest {
    
    private LabelBusinessImpl labelBusiness = new LabelBusinessImpl();
    private LabelDAO labelDAO;
    
    @Before
    public void setUp() {
        labelDAO = createMock(LabelDAO.class);
        labelBusiness.setLabelDAO(labelDAO);
    }
    
    @Test
    public void testLookUpLabelsLike() {
        List<Label> list = new LinkedList<Label>();
        expect(labelDAO.lookupLabelsLike("Notfound")).andReturn(list);
        replay(labelDAO);
        labelBusiness.lookupLabelsLike("Notfound");
        verify(labelDAO);
    }

}
