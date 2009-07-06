package fi.hut.soberit.agilefant.util;

import java.util.Arrays;
import java.util.Collection;

import org.junit.*;
import static org.junit.Assert.*;

public class ListUtilsTest {

    @Test
    public void testRemoveDuplicates() {
        String str1 = "1";
        String str2 = "2";
        String str3 = "3";
        Collection<String> original = Arrays.asList(str1,str1,str1,str2,str3,str3);
        
        Collection<String> result = ListUtils.removeDuplicates(original);
        
        assertTrue("List does not contain the string \"1\"",result.contains(str1));
        assertTrue("List does not contain the string \"2\"",result.contains(str2));
        assertTrue("List does not contain the string \"3\"",result.contains(str3));
        assertEquals("List size is too big",3,result.size());
    }
}
