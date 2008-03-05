package fi.hut.soberit.agilefant.model;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * Wrapper class around java.sql.Time for representing times in the "JIRA
 * notation".
 * <p>
 * Accepts following notations:
 * <p>
 * AFTime mytime = new AFTime("2d 4h 15m");<br>
 * AFTime mytime = new AFTime("2h 15m"); (elements can be left out)<br>
 * AFTime mytime = new AFTime("0");<br>
 * AFTime mytime = new AFTime("2,5"); (or 2.5, depending on system settings)<br>
 * <p>
 * TimeUserType enables saving these time-objects into db. package-info.java
 * defines a annotation shorthand for defining fields of this type. You should
 * always define AFTime-Hibernate getters like this:
 * <p>
 * <code>
 * &#064;Type(type="af_time")<br>
 * public AFTime getTesttime() { ...
 * </code>
 * <p>
 * Forgetting &#064;Type makes bad things happen.
 * 
 * @see fi.hut.soberit.agilefant.db.hibernate.TimeUserType
 * @author ekantola
 * @author Turkka Äijälä
 */
public class AFTime extends java.sql.Time {
    private static final long serialVersionUID = 2737253352614021649L;

    public final static long SECOND_IN_MILLIS = 1;

    public final static long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;

    public final static long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;

    /**
     * Workday is not a fixed-length concept, don't use this
     */
    @Deprecated
    public static long WORKDAY_IN_MILLIS = 8 * HOUR_IN_MILLIS;

    public static long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

    public static long WORKDAY_IN_HOURS = WORKDAY_IN_MILLIS / HOUR_IN_MILLIS;

    // constants for array indices
    private static final int Days = 0;

    private static final int Hours = 1;

    private static final int Minutes = 2;

    public AFTime(long time) {
        super(time);
    }

    public AFTime(String time) {
        super(parse(time));
    }

    public AFTime(String time, boolean allowNegatives) {
        super(parse(time, allowNegatives));
    }

    
    public static long parse(String s) throws IllegalArgumentException {
        return parse(s, true);
    }

    
    /**
     * Attempts to interpret the string <tt>s</tt> as a representation of a
     * time. If the attempt is successful, the time indicated is returned
     * represented as milliseconds. If the attempt fails, an
     * <tt>IllegalArgumentException</tt> is thrown.
     * <p>
     * Parse accepts the "JIRA notation": "Dd Hh Mm", where D, H and M represent
     * the numeric values of days, hours and minutes. For example, "2d 15h 30m"
     * means "two days, 15 hours and 30 minutes". Some, but not all, of the
     * days, hours, or minutes can be left out, in which case they are
     * interpreted as zero-valued.
     * 
     * @param s
     *                string to be parsed as a time
     * @return the time represented by the string argument in milliseconds
     * @throws IllegalArgumentException
     *                 if illegal input is given
     */
    public static long parse(String s, boolean allowNegatives) throws IllegalArgumentException {
        
        
        boolean negative = false;
        
        // use scanner
        Scanner scanner = new Scanner(s);

        try {

            // received fields
            long fields[] = { 0, 0, 0 };
            // which fields were read
            boolean hasFields[] = { false, false, false };
            // millisecond contribution of each field
            final long contributions[] = { WORKDAY_IN_MILLIS, HOUR_IN_MILLIS,
                    MINUTE_IN_MILLIS };

            while (true) {

                // try skipping the white spaces
                try {
                    scanner.next(scanner.delimiter());
                } catch (NoSuchElementException e) {
                }

                // try reading next int and character - pair
                try {
                    scanner.next("(-?)(\\d+)(\\p{Alpha}|min)");
                } catch (NoSuchElementException e) {
                    // no more elements
                    break;
                }

                // get the regexp result
                MatchResult result = scanner.match();

                // set negative to true if one of the time digits is negative
                if(result.group(1).equals("-") && allowNegatives)
                    negative = true;
                
                // get the integer and character from groups 2 and 3
                long value = Long.parseLong(result.group(2));
                char type;

                // set value to negative if one of the time digits has been negative
                if(negative)
                    value = -1*value;
                
                // allow min instead of just m
                if (result.group(3).contains("min")) {
                    type = 'm';
                } else {
                    type = result.group(3).charAt(0);
                }

                // interperent the value according to the letter
                switch (type) {
                case 'd':
                case 'D':
                    // did we read days already?
                    if (hasFields[Days])
                        throw new IllegalArgumentException(
                                "days defined multiple times");

                    // mark the field
                    fields[Days] = value;

                    // mark that we got this field
                    hasFields[Days] = true;
                    break;

                case 'h':
                case 'H':
                    if (hasFields[Hours])
                        throw new IllegalArgumentException(
                                "hours defined multiple times");
                    fields[Hours] = value;
                    hasFields[Hours] = true;
                    break;

                case 'm':
                case 'M':
                    if (hasFields[Minutes])
                        throw new IllegalArgumentException(
                                "minutes defined multiple times");
                    fields[Minutes] = value;
                    hasFields[Minutes] = true;
                    break;

                // unknown field
                default:
                    throw new IllegalArgumentException("unknown field type '"
                            + type + "'");
                }
            }

            // because we're not accepting empty input,
            // we're checking here if we managed to parse anything
            boolean allFalse = true;

            long time = 0;
            for (int i = 0; i < fields.length; i++) {

                // did we get this field?
                if (hasFields[i])
                    // mark the flag that we got something
                    allFalse = false;

                time += fields[i] * contributions[i];
            }

            if (allFalse) {
                Scanner sc = new Scanner(s);
                
                try {
                    sc.next("^\\s*(-?)(\\d+)[,|\\.]?(\\d*)\\s*$");
                }
                catch (NoSuchElementException e) {
                    throw new IllegalArgumentException("invalid input");
                }
                
                MatchResult dotResult = sc.match();
                
                if (dotResult.group(1).equals("-") && allowNegatives) {
                    negative = true;
                }
                
                double hours = Double.parseDouble(dotResult.group(2) + "." + dotResult.group(3));
                
                if (negative) {
                    return -((long) (hours * (double) HOUR_IN_MILLIS));
                }
                else {
                    return (long) (hours * (double) HOUR_IN_MILLIS);
                }

            }

            // check if there's more input
            if (scanner.hasNext())
                throw new IllegalArgumentException("invalid input");

            return time;

        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("parse error", e);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("parse error", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("parse error", e);
        } finally {
            scanner.close();
        }
    }

    /*
     * Get the time divided up to days, hour, minutes. @return array with an
     * element for days, hours, minutes, correspondingly
     */
    private long[] divideToElements() {
        // array elements for each element
        long[] elem = { 0, 0, 0 };

        // get the time in milliseconds
        long time = getTime();

        if (time == 0)
            return elem;

        // get amount of days
        elem[Days] = time / WORKDAY_IN_MILLIS;
        // calculate remaining milliseconds
        time %= WORKDAY_IN_MILLIS;

        // similarly
        elem[Hours] = time / HOUR_IN_MILLIS;
        time %= HOUR_IN_MILLIS;

        elem[Minutes] = time / MINUTE_IN_MILLIS;
        time %= MINUTE_IN_MILLIS;

        // rounding minutes properly, as defined by the unit test
        if (time >= 30)
            elem[Minutes]++;

        return elem;
    }

    /**
     * Build a DHM-string out of an element array. Includes only nonzero
     * elements.
     * 
     * @param time
     *                array of time elements
     */
    private String buildElementString(long[] time) {

        assert time.length == 3;

        // a flag to track when we should
        // put space between elements
        boolean hadPrevious = false;
        boolean negative = false;
        
        // string to build the result in
        String result = "";

        
        // days
        if (time[Days] != 0) {
            if (time[Days] < 0) {
                negative = true;
            }
            result += Math.abs(time[Days]) + "d";
            hadPrevious = true;
        }

        // hours
        if (time[Hours] != 0) {
            if (time[Hours] < 0) {
                negative = true;
            }
            if (hadPrevious)
                result += " ";
            result += Math.abs(time[Hours]) + "h";
            hadPrevious = true;
        }

        // minutes
        if (time[Minutes] != 0) {
            if (time[Minutes] < 0) {
                negative = true;
            }
            if (hadPrevious)
                result += " ";
            result += Math.abs(time[Minutes]) + "min";
        }

        // check the emptyness once more here,
        // since all the fields might've been 0
        // if time was less than half a minute
        if (result.length() == 0)
            result = "0";

        if (negative) {
            return "-" + result;
        }
        else {
            return result;
        }
    }

    /**
     * Get a "full" string representation, with days, hours and minutes all
     * expressed.
     * <p>
     * Gets you a dhm-string, eg. "5d 3h 4m".
     * 
     * @return a dhm-string, eg. "5d 3h 4m".
     * @see toString
     */
    public String toDHMString() {

        if (getTime() == 0)
            return "0";

        // get day, hour, minute elements
        long[] time = divideToElements();

        return buildElementString(time);
    }

    /**
     * Get a "partial" string representation, with days included in hours.
     * <p>
     * Gets you an hm-string, eg. "43h 4m", instead of "5d 3h 4m".
     * 
     * @return a hm-string, eg. "43h 4m".
     * @see toFullString
     */

    public String toHMString() {

        if (getTime() == 0)
            return "0h";

        // get days, hours, minutes
        long[] time = divideToElements();

        // form the string

        // fix up the hours so that days are included in them
        time[Hours] += time[Days] * WORKDAY_IN_HOURS;
        time[Days] = 0;

        return buildElementString(time);
    }

    /**
     * Functionally same as "toHMString".
     */
    public String toString() {
        // just call toHMString.
        return toHMString();
    }

    /**
     * Sums another AFTime to this
     * 
     * @param time
     *                Time to add
     */
    public void add(AFTime time) {
        super.setTime(this.getTime() + time.getTime());
    }
}