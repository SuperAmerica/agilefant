package fi.hut.soberit.agilefant.model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Wrapper class around java.sql.Time for representing times in the
 * "JIRA notation".
 * 
 * @author ekantola
 */
public class Time extends java.sql.Time {
	private static final long serialVersionUID = 2737253352614021649L;

	public static long SECOND_IN_MILLIS = 1000;
	public static long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;
	public static long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
	public static long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;
	
	public Time(long time) {
		super(time);
	}
	
	public Time(String time) {
		super(parse(time));
	}
	
	/**
     * Attempts to interpret the string <tt>s</tt> as a representation 
     * of a time. If the attempt is successful, the time 
     * indicated is returned represented as milliseconds. 
     * If the attempt fails, an <tt>IllegalArgumentException</tt> is thrown.
     * <p>
     * Parse accepts the "JIRA notation": "Dd Hh Mm", where D, H and M represent
     * the numeric values of days, hours and minutes. For example, "2d 15h 30m"
     * means "two days, 15 hours and 30 minutes". Some, but not all, of the
     * days, hours, or minutes can be left out, in which case they are
     * interpreted as zero-valued.
	 * 
	 * @param s string to be parsed as a time
	 * @return the time represented by the string argument in milliseconds
	 * @throws IllegalArgumentException if illegal input is given
	 */
	public static long parse(String s) throws IllegalArgumentException {
		throw new NotImplementedException();
	}
	
	public String toString() {
		throw new NotImplementedException();
	}
}
