package fi.hut.soberit.agilefant.model;

import java.text.NumberFormat;

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
		
		// without qualifier
		assertEquals(getTime(0, 6, 0), parse("6"));

		// without qualifier, decimal number		
		// first format a decimal string according to current locale
		String decimalNumber = NumberFormat.getNumberInstance().format(3.5);
		// parse the formatted string
		assertEquals(decimalNumber, getTime(0, 3, 30), parse(decimalNumber));
		
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
		
		// don't accept fields without unit symbol with fields with
		// unit symbol
		try {
			parse("76 5m");
			fail();
		} catch (IllegalArgumentException e) {}

		// don't accept fields without unit symbol with fields with
		// unit symbol
		try {
			parse("1d 76");
			fail();
		} catch (IllegalArgumentException e) {}		
		
		// don't accept fields without unit symbol with fields with
		// unit symbol
		try {
			parse("2d 76 30m");
			fail();
		} catch (IllegalArgumentException e) {}		

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
		// first test DHM strings
		/////////////////////////
		
		// Test some normal time
		assertEquals("5d 3h 4min", new AFTime(getTime(5, 3, 4)).toDHMString());
		
		// Days only
		assertEquals("2d", new AFTime(getTime(2, 0, 0)).toDHMString());
		
		// Hours only
		assertEquals("7h", new AFTime(getTime(0, 7, 0)).toDHMString());
		
		// Minutes only
		assertEquals("20min", new AFTime(getTime(0, 0, 20)).toDHMString());
		
		// Days and minutes
		assertEquals("3d 57min", new AFTime(getTime(3, 0, 57)).toDHMString());
		
		// Days and hours
		assertEquals("5d 2h", new AFTime(getTime(5, 2, 0)).toDHMString());

		// then test HM strings
		/////////////////////////
		
		// Test some normal time
		assertEquals("43h 4min", new AFTime(getTime(5, 3, 4)).toHMString());
		
		// Days only
		assertEquals("16h", new AFTime(getTime(2, 0, 0)).toHMString());
		
		// Hours only
		assertEquals("7h", new AFTime(getTime(0, 7, 0)).toHMString());
		
		// Minutes only
		assertEquals("20min", new AFTime(getTime(0, 0, 20)).toHMString());
		
		// Days and minutes
		assertEquals("24h 57min", new AFTime(getTime(3, 0, 57)).toHMString());
		
		// Days and hours
		assertEquals("42h", new AFTime(getTime(5, 2, 0)).toHMString());
						
		// Check that toString rounds values properly
		/////////////////////////
		
		assertEquals("6h 39min", new AFTime(getTime(0, 6, 40) - 30001).toString());
		assertEquals("6h 40min", new AFTime(getTime(0, 6, 40) - 30000).toString());
		assertEquals("6h 40min", new AFTime(getTime(0, 6, 40) + 29999).toString());
		assertEquals("6h 41min", new AFTime(getTime(0, 6, 40) + 30000).toString());
		
		// Zero dates
		// TODO what is the best output format for this? Should we print some qualifier? 
		assertEquals("0", new AFTime(0).toString());
	}
}
