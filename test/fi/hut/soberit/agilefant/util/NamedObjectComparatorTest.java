package fi.hut.soberit.agilefant.util;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.model.NamedObject;
import static org.junit.Assert.*;

public class NamedObjectComparatorTest {

    private NamedObjectComparator comparator;
    private class ObjectWithName implements NamedObject {
        private String name;
        public ObjectWithName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    
    @Before
    public void setUp() {
        comparator = new NamedObjectComparator();
    }
    
    @Test
    public void compare_nulls() {
        assertEquals(0, comparator.compare(null, null));
    }
    
    @Test
    public void compare_oneNull() {
        ObjectWithName withName = new ObjectWithName("aaaaaa");
        assertEquals(-1, comparator.compare(null, withName));
        assertEquals(1, comparator.compare(withName, null));
    }
    
    @Test
    public void compare_equal() {
        ObjectWithName withName = new ObjectWithName("aaaaaa");
        assertEquals(0, comparator.compare(withName, withName));
    }
    
    @Test
    public void compare_notEqual() {
        ObjectWithName nameA = new ObjectWithName("aaaaaa");
        ObjectWithName nameB = new ObjectWithName("bbbbbb");
        assertEquals(-1, comparator.compare(nameA, nameB));
    }
}
