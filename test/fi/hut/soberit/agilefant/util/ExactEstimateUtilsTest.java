package fi.hut.soberit.agilefant.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExactEstimateUtilsTest {

    private void testConvertFromString(long expected, String input) {
        assertEquals(expected, ExactEstimateUtils.convertFromString(input)
                .getMinorUnits().longValue());
    }
    
    private void testConvertSignedFromString(long expected, String input) {
        assertEquals(expected, ExactEstimateUtils.convertSignedFromString(input)
                .getMinorUnits().longValue());
    }
    @Test
    public void testValidHours() {
        testConvertFromString(60L, "1h");
        testConvertSignedFromString(-60L, "-1h");
    }

    @Test
    public void testValidHoursWithWhitespace() {
        testConvertFromString(60L, "  1h   ");
        testConvertSignedFromString(-60L, "  -1h   ");
    }

    @Test
    public void testValidHoursWithDecimalsAsComma() {
        testConvertFromString(90L, "1,5h");
        testConvertSignedFromString(-90L, "-1,5h");
    }

    @Test
    public void testValidHoursWithDecimalsAsPeriod() {
        testConvertFromString(90L, "1.5h");
        testConvertSignedFromString(-90L, "-1.5h");
    }

    @Test
    public void testValidMinutes() {
        testConvertFromString(15L, "15min");
        testConvertSignedFromString(-15L, "-15min");
    }

    @Test
    public void testValidHoursAndMinutes() {
        testConvertFromString(105L, "1.5h15min");
        testConvertSignedFromString(-105L, "-1.5h15min");
    }

    @Test
    public void testValidHoursAndMinutesWithWhitespace() {
        testConvertFromString(105L, "   1.5h   15min        ");
        testConvertSignedFromString(-105L, "   -1.5h   15min        ");
    }

    @Test
    public void testWithoutUnit() {
        testConvertFromString(60L, "1");
        testConvertSignedFromString(-60L, "-1");
        assertEquals(60L, ExactEstimateUtils.convertFromString("1").getMinorUnits().longValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithoutMinuteUnit() {
        ExactEstimateUtils.convertFromString("1h 15").getMinorUnits();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithoutSignedMinuteUnit() {
        ExactEstimateUtils.convertSignedFromString("-1h 15").getMinorUnits();
    }
}
