package fi.hut.soberit.agilefant.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MinorUnitsParserTest {

    /**
     * Set up to hours and minutes
     */
    MinorUnitsParser mup = new MinorUnitsParser("h", "min", 60);
    
    @Test
    /**
     * Should convert 2 -> 2h
     */
    public void testParseLongValue() {
       long actual = mup.convertFromString("2");
       assertEquals(120, actual);
    }
    
    @Test
    /**
     * Should convert 2h -> 2h
     */
    public void testParseHourValue() {
        long actual = mup.convertFromString("2h");
        assertEquals(120, actual); 
    }
    
    @Test
    /**
     * Should convert 2.5 -> 2h 30min
     * Should convert 1,75 -> 1h 45min
     */
    public void testParseHourValueWithDecimals() {
        long actual = mup.convertFromString("2.5");
        assertEquals(150, actual);
        
//        actual = mup.convertFromString("1,75");
//        assertEquals(105, actual);
    }
    
    @Test
    /**
     * Should convert 2.5h -> 2h 30min
     * Should convert 3,5h -> 3h 30min
     */
    public void testParseHourValueWithDecimalsAndMajorUnits() {
        long actual = mup.convertFromString("2.5h");
        assertEquals(150, actual);
        
        actual = mup.convertFromString("3,5h");
        assertEquals(210, actual);
    }
    
    @Test
    /**
     * Should convert 2h 10min -> 2h 10min
     */
    public void testConvertFromWholeString() {
        long actual = mup.convertFromString("2h 10min");
        assertEquals(130, actual);
    }
    
    @Test
    /**
     * Should convert 15min -> 15min
     */
    public void testConvertOnlyMinors() {
        long actual = mup.convertFromString("15min");
        assertEquals(15, actual);
    }
    
}
