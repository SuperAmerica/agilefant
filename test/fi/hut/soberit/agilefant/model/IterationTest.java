package fi.hut.soberit.agilefant.model;

import junit.framework.TestCase;

public class IterationTest extends TestCase {

public void testGetTimeOfDayDate()  {
		
	
		int testHour1 = 6;
		int expectedHour1 = 6;
		int testHour2 = 0;
		int expectedHour2 = 0;
		int testHour3 = 24;
		int expectedHour3 = 0;
		int testHour4 = -15;
		int expectedHour4 = 9;
		int testHour5 = 49;
		int expectedHour5 = 1;
		
		java.util.Calendar cal = java.util.Calendar.getInstance();
		
		cal.setTime(Iteration.getTimeOfDayDate(testHour1));
		int testResult1 = cal.get(java.util.Calendar.HOUR_OF_DAY);
		cal.setTime(Iteration.getTimeOfDayDate(testHour2));
		int testResult2 = cal.get(java.util.Calendar.HOUR_OF_DAY);
		cal.setTime(Iteration.getTimeOfDayDate(testHour3));
		int testResult3 = cal.get(java.util.Calendar.HOUR_OF_DAY);
		cal.setTime(Iteration.getTimeOfDayDate(testHour4));
		int testResult4 = cal.get(java.util.Calendar.HOUR_OF_DAY);
		cal.setTime(Iteration.getTimeOfDayDate(testHour5));
		int testResult5 = cal.get(java.util.Calendar.HOUR_OF_DAY);
		
		assertEquals("" + expectedHour1,
		"" + testResult1
				);
		assertEquals("" + expectedHour2,
				"" + testResult2
				);
		assertEquals("" + expectedHour3,
				"" + testResult3
				);
		assertEquals("" + expectedHour4,
				"" + testResult4
				);
		assertEquals("" + expectedHour5,
				"" + testResult5
				);
		
	}
	
}
