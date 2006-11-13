package fi.hut.soberit.agilefant.model;

import junit.framework.TestCase;

import static fi.hut.soberit.agilefant.model.AFTime.*;

/**
 * @author ekantola
 */
public class AFTimeTest extends TestCase {
	private long getTime(int days, int hours, int minutes) {
		return days*WORKDAY_IN_MILLIS + hours*HOUR_IN_MILLIS + minutes*MINUTE_IN_MILLIS;
	}
	
	public void testParse() {
		// Test some normal string
		assertEquals(getTime(2, 4, 15), parse("2d 4h 15m"));
		
		// Days
		assertEquals(getTime(5, 0, 0), parse("5d"));
		
		// Hours
		assertEquals(getTime(0, 4, 0), parse("4h"));
		
		// Hours overflow
		assertEquals(getTime(0, 47, 0), parse("47h"));
		
		// Minutes
		assertEquals(getTime(0, 0, 50), parse("50m"));
		
		// Minutes overflow
		assertEquals(getTime(0, 0, 453), parse("453m"));
		
		// Minutes left out
		assertEquals(getTime(1, 7, 0), parse("1d 7h"));
		
		// Parse zero times properly (note: qualifier can be left out here!)
		assertEquals(0, parse("0"));
		assertEquals(0, parse("0d 0h 0m"));
		assertEquals(0, parse("0d 0m"));
		assertEquals(0, parse("0h"));

		// accept extra use of whitespaces 
		assertEquals(getTime(2, 4, 15), parse("    2d    4h   15m    "));
		
		// Don't accept "almost good" input
		try {
			parse("2d 4h foo 15m");
			fail();
		} catch (IllegalArgumentException e) {
			
		}

		// Don't accept "almost good" input
		try {
			parse("2d 4h 15m foo ");
			fail();
		} catch (IllegalArgumentException e) {
			
		}		
		
		// Don't accept times with same field given multiple times
		try {
			parse("2d 5h 2h");
			fail();
		} catch (IllegalArgumentException e) {
			
		}
		
		// Don't accept empty strings
		try {
			parse("");
			fail();
		} catch (IllegalArgumentException e) {
			
		}
		
		// Don't accept garbage strings
		try {
			parse("roskaa");
			fail();
		} catch (IllegalArgumentException e) {
			
		}
		
		// Don't accept nonzero unqualified numbers
		// TODO is this the right behavior?
		try {
			parse("76");
			fail();
		} catch (IllegalArgumentException e) {
			
		}
		
		// Negative values not allowed
		try {
			parse("-1h");
			fail();
		} catch (IllegalArgumentException e) {
			
		}
		
		// TODO should this be accepted anyway (would equal "3d 15h 25m")?
		try {
			parse("3d 16h -35m");
			fail();
		} catch (IllegalArgumentException e) {
			
		}		
	}
	
	public void testToString() {
		// Test some normal time
		assertEquals("5d 3h 4m", new AFTime(getTime(5, 3, 4)).toString());
		
		// Days only
		assertEquals("2d", new AFTime(getTime(2, 0, 0)).toString());
		
		// Hours only
		assertEquals("7h", new AFTime(getTime(0, 7, 0)).toString());
		
		// Minutes only
		assertEquals("20m", new AFTime(getTime(0, 0, 20)).toString());
		
		// Days and minutes
		assertEquals("3d 57m", new AFTime(getTime(3, 0, 57)).toString());
		
		// Days and hours
		assertEquals("5d 2h", new AFTime(getTime(5, 2, 0)).toString());
		
		// Check that toString rounds values properly

		assertEquals("6h 39m", new AFTime(getTime(0, 6, 40) - 30001).toString());
		assertEquals("6h 40m", new AFTime(getTime(0, 6, 40) - 30000).toString());
		assertEquals("6h 40m", new AFTime(getTime(0, 6, 40) + 29999).toString());
		assertEquals("6h 41m", new AFTime(getTime(0, 6, 40) + 30000).toString());
		
		// Zero dates
		// TODO what is the best output format for this? Should we print some qualifier? 
		assertEquals("0", new AFTime(0).toString());
	}
}
