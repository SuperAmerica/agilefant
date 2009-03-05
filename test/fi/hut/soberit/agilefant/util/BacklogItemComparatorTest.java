package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.State;

import junit.framework.TestCase;

public class BacklogItemComparatorTest extends TestCase {
    public void testCompare_DoneAndNotDone() {
        List<BacklogItem> items = new ArrayList<BacklogItem>();
        BacklogItem item1 = new BacklogItem();
        BacklogItem item2 = new BacklogItem();
        items.add(item1);
        items.add(item2);
        item1.setState(State.DONE);
        item2.setState(State.PENDING);
        
        Collections.sort(items, new BacklogItemComparator(new BacklogItemPriorityComparator()));
        assertEquals(item1, items.get(1));
        assertEquals(item2, items.get(0));
    }
    
    public void testCompare_NotDoneDifferentPrio() {
        List<BacklogItem> items = new ArrayList<BacklogItem>();
        BacklogItem item1 = new BacklogItem();
        BacklogItem item2 = new BacklogItem();
        items.add(item1);
        items.add(item2);
        item1.setState(State.PENDING);
        item2.setState(State.PENDING);
        item1.setPriority(Priority.MINOR);
        item2.setPriority(Priority.MAJOR);
        
        Collections.sort(items, new BacklogItemComparator(new BacklogItemPriorityComparator()));
        assertEquals(item1, items.get(1));
        assertEquals(item2, items.get(0));
    }
    
    public void testCompare_DoneOneUndefined() {
        List<BacklogItem> items = new ArrayList<BacklogItem>();
        BacklogItem item1 = new BacklogItem();
        BacklogItem item2 = new BacklogItem();
        items.add(item1);
        items.add(item2);
        item1.setState(State.DONE);
        item2.setState(State.DONE);
        item1.setPriority(Priority.MINOR);
        item2.setPriority(Priority.UNDEFINED);
        
        Collections.sort(items, new BacklogItemComparator(new BacklogItemPriorityComparator()));
        assertEquals(item1, items.get(1));
        assertEquals(item2, items.get(0));
    }
    
    public void testCompare_NotDoneOneUndefined() {
        List<BacklogItem> items = new ArrayList<BacklogItem>();
        BacklogItem item1 = new BacklogItem();
        BacklogItem item2 = new BacklogItem();
        items.add(item1);
        items.add(item2);
        item1.setState(State.PENDING);
        item2.setState(State.PENDING);
        item1.setPriority(Priority.MINOR);
        item2.setPriority(Priority.UNDEFINED);
        
        Collections.sort(items, new BacklogItemComparator(new BacklogItemPriorityComparator()));
        assertEquals(item1, items.get(1));
        assertEquals(item2, items.get(0));
    }
    
    public void testCompare_Multiple1() {
        List<BacklogItem> items = new ArrayList<BacklogItem>();
        BacklogItem item1 = new BacklogItem();
        BacklogItem item2 = new BacklogItem();
        BacklogItem item3 = new BacklogItem();
        BacklogItem item4 = new BacklogItem();
        BacklogItem item5 = new BacklogItem();
        
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        
        item1.setState(State.DONE);
        item2.setState(State.STARTED);
        item3.setState(State.IMPLEMENTED);
        item4.setState(State.DONE);
        item5.setState(State.NOT_STARTED);
        
        item1.setPriority(Priority.MAJOR);
        item2.setPriority(Priority.BLOCKER);
        item3.setPriority(Priority.UNDEFINED);
        item4.setPriority(Priority.MINOR);
        item5.setPriority(Priority.MINOR);
        
        Collections.sort(items, new BacklogItemComparator(new BacklogItemPriorityComparator()));
        assertEquals(item1, items.get(3));
        assertEquals(item2, items.get(1));
        assertEquals(item3, items.get(0));
        assertEquals(item4, items.get(4));
        assertEquals(item5, items.get(2));
    }
    
    public void testCompare_Multiple2() {
        List<BacklogItem> items = new ArrayList<BacklogItem>();
        BacklogItem item1 = new BacklogItem();
        BacklogItem item2 = new BacklogItem();
        BacklogItem item3 = new BacklogItem();
        BacklogItem item4 = new BacklogItem();
        BacklogItem item5 = new BacklogItem();
        
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);
        item1.setState(State.DONE);
        item2.setState(State.DONE);
        item3.setState(State.DONE);
        item4.setState(State.PENDING);
        item5.setState(State.PENDING);
        item1.setPriority(Priority.MINOR);
        item2.setPriority(Priority.UNDEFINED);
        item3.setPriority(Priority.MAJOR);
        item4.setPriority(Priority.UNDEFINED);
        item5.setPriority(Priority.BLOCKER);
        
        Collections.sort(items, new BacklogItemComparator(new BacklogItemPriorityComparator()));
        assertEquals(item1, items.get(4));
        assertEquals(item2, items.get(2));
        assertEquals(item3, items.get(3));
        assertEquals(item4, items.get(0));
        assertEquals(item5, items.get(1));
    }

}
