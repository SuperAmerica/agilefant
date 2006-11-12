package fi.hut.soberit.agilefant.model;

import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.NoSuchElementException;

/**
 * Wrapper class around java.sql.Time for representing times in the
 * "JIRA notation".
 * <p>
 * db.hibernate.TimeUserTime.java enables saving these times into db.
 * package-info.java defines a annotation shorthand for defining fields of this type. 
 * You should always define hibernate getters like this:
 * <p><code>
 * &#064;Type(type="af_time")<br>
 * public AFTime getTesttime() { ...
 * </code><p>
 * Forgetting &#064;Type makes bad things happen. 
 * 
 * @author ekantola
 * @author Turkka Äijälä
 *
 */
public class AFTime extends java.sql.Time {
	private static final long serialVersionUID = 2737253352614021649L;

	public static long SECOND_IN_MILLIS = 1000;
	public static long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;
	public static long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
	public static long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;
	
	public AFTime(long time) {
		super(time);
	}
	
	public AFTime(String time) {
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

		// use scanner
		Scanner scanner = new Scanner(s);
		
		try {
			
			final int Days = 0;
			final int Hours = 1;
			final int Minutes = 2; 
			
			// received fields
			long fields[] = {0, 0, 0};
			// which fields were read
			boolean hasFields[] = {false, false, false};
			// millisecond contribution of each field
			final long contributions[] = {DAY_IN_MILLIS, HOUR_IN_MILLIS, MINUTE_IN_MILLIS};
			
			while(true) {
				
				// try skipping the white spaces
				try {
					scanner.next(scanner.delimiter());
				} catch(NoSuchElementException e) {}
				
				// try reading next int and character - pair
				try {
					scanner.next("(\\d+)(\\p{Alpha})");
				} catch(NoSuchElementException e) {
					// no more elements
					break;					
				}
				
				// get the regexp result
			    MatchResult result = scanner.match();
			    
			    // get the integer and character from groups 1 and 2
			    long value = Long.parseLong(result.group(1));
			    char type = result.group(2).charAt(0);
			    
			    // interperent the value according to the letter
			    switch(type) {
			    	case 'd':
			    	case 'D':
			    		// did we read days already?
			    		if(hasFields[Days])
			    			throw new IllegalArgumentException("days defined multiple times");
			    		
			    		// mark the field
			    		fields[Days] = value;
			    		
			    		// mark that we got this field
			    		hasFields[Days] = true;
			    	break;

			    	case 'h':
			    	case 'H':
			    		if(hasFields[Hours])
			    			throw new IllegalArgumentException("hours defined multiple times");
			    		fields[Hours] = value;
			    		hasFields[Hours] = true;
			    	break;
			    	
			    	case 'm':
			    	case 'M':
			    		if(hasFields[Minutes])
			    			throw new IllegalArgumentException("minutes defined multiple times");
			    		fields[Minutes] = value;
			    		hasFields[Minutes] = true;
			    	break;
			    	
			    	// unknown field
			    	default:
			    		throw new IllegalArgumentException("unknown field type '" + type + "'");
			    }
			}

			// because we're not accepting empty input,
			// we're checking here if we managed to parse anything  
			boolean allFalse = true;
			
		    long time = 0;
		    for(int i = 0; i < fields.length; i++) {
		    	
		    	// did we got this field?
		    	if(hasFields[i])
		    		// mark the flag that we got something  
		    		allFalse = false;
		    	
		    	time += fields[i] * contributions[i];
		    }
		    
		    if(allFalse) {
		    	// If we got here, we didn't understood any of the input.
		    	// We should understand bare zeroes however. Let's try parsing those here. 
		    	
		    	String findResult = scanner.findInLine("0+");
		    	if( findResult != null && !scanner.hasNext()) {
		    		// we understood the zero, return it here
		    		return 0;
		    	}
		    	
		    	throw new IllegalArgumentException("invalid input");
		    }
		    
		    // check if there's more input
		    if(scanner.hasNext())
		    	throw new IllegalArgumentException("invalid input");
		    
		    return time;
		
		} catch(IllegalStateException e) {
			throw new IllegalArgumentException("parse error", e);
		} catch(IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("parse error", e);
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException("parse error", e);
		} finally {
			scanner.close();		    	    
		}		
	}
	
	public String toString() {
		long time = this.getTime();
		
		if(time == 0)
			return "0";
		
		// divided time into elements 
		
		long days = time / DAY_IN_MILLIS;
		time %= DAY_IN_MILLIS;
		
		long hours = time / HOUR_IN_MILLIS;
		time %= HOUR_IN_MILLIS;
		
		long minutes = time / MINUTE_IN_MILLIS;
		time %= MINUTE_IN_MILLIS;
		
		if(time >= 30000)
			minutes++;
		
		// form string
		
		boolean hadPrevious = false;
		String result = "";
		
		if(days != 0) {
			result += days + "d";
			hadPrevious = true;
		}
		
		if(hours != 0) {
			if(hadPrevious) result += " ";
			result += hours + "h";
			hadPrevious = true;
		}
		
		if(minutes != 0) {
			if(hadPrevious) result += " ";
			result += minutes + "m";
		}
		
		return result;
	}		
}
