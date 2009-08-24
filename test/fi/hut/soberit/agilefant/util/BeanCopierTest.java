package fi.hut.soberit.agilefant.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import fi.hut.soberit.agilefant.model.ExactEstimate;

public class BeanCopierTest {

    @Test
    public void testCopyFields() {
        A source = new A();
        source.setFoo("Hello");
        source.setEe(new ExactEstimate(200L));
        source.setColl(new ArrayList<Integer>(Arrays.asList(123, 222, 433)));
        A dest = new A();
        
        BeanCopier.copy(source, dest);
        
        assertEquals(3, dest.getColl().size());
        source.getColl().add(1986);
        source.setFoo("Bar");
        
        assertEquals("Hello", dest.getFoo());
        assertEquals("Bar", source.getFoo());
        assertEquals(200L, dest.getEe().getMinorUnits().longValue());
        assertEquals(3, dest.getColl().size());
    }
    
    @Test
    public void testCopyFields_toInherited() {
        A source = new A();
        source.setFoo("Hello");
        B dest = new B();
        dest.setBar(123);
        
        BeanCopier.copy(source, dest);
        
        source.setFoo("Bar");
        
        assertEquals("Hello", dest.getFoo());
        assertEquals("Bar", source.getFoo());
        assertEquals(new Integer(123), dest.getBar());
    }
    
    @Test
    public void testCopyFields_nullCollection() {
        A source = new A();
        source.setColl(null);
        A dest = new A();
        dest.setColl(new ArrayList<Integer>());
        
        BeanCopier.copy(source, dest);
        
        assertNull(dest.getColl());
    }
    
    
    class A {
        private String foo;
        private ExactEstimate ee;
        private Collection<Integer> coll;
        
        public String getFoo() {
            return foo;
        }
        
        public void setFoo(String foo) {
            this.foo = foo;
        }

        public void setEe(ExactEstimate ee) {
            this.ee = ee;
        }

        public ExactEstimate getEe() {
            return ee;
        }

        public void setColl(Collection<Integer> coll) {
            this.coll = coll;
        }

        public Collection<Integer> getColl() {
            return coll;
        }

    }
    
    class B extends A {
        private Integer bar;
        
        public Integer getBar() {
            return bar;
        }
        
        public void setBar(Integer bar) {
            this.bar = bar;
        }
    }

}
