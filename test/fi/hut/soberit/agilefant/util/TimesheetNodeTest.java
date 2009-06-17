package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import static org.junit.Assert.*;


import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.HourEntry;

public class TimesheetNodeTest extends TestCase {

    class TimesheetStaticTestNode extends TimesheetNode {
        
        @Override
        public ExactEstimate getEffortSum() {
            return new ExactEstimate(450);
        }
        @Override
        public List<? extends TimesheetNode> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean hasChildren() {
            return false;
        }
        
    }
    
    class TimesheetTestNode extends TimesheetNode {
        private List<TimesheetNode> children = new ArrayList<TimesheetNode>();
        public TimesheetTestNode() {
            children.add(new TimesheetStaticTestNode());
            children.add(new TimesheetStaticTestNode());
            children.add(new TimesheetStaticTestNode());
            HourEntry he = new HourEntry();
            he.setMinutesSpent(6500);
            this.hourEntries.add(he);
        }
        @Override
        public List<? extends TimesheetNode> getChildren() {
            return this.children;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public boolean hasChildren() {
            return true;
        }   
    }
    
    public void testCalculateEffortSum() {
        TimesheetTestNode testable = new TimesheetTestNode();
        ExactEstimate sum = testable.getEffortSum();
        long minorSum = sum.getMinorUnits().longValue();
        assertEquals(7850L, minorSum);
    }
}
