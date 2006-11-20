package fi.hut.soberit.agilefant.security;

import junit.framework.TestCase;

/**
 * Unit test for the MD5-hashfunction
 * in SecurityUtil. 
 * 
 * @author Turkka Äijälä
 */
public class MD5Test extends TestCase {

	public void testMD5() {
		
		assertEquals(
				"900150983cd24fb0d6963f7d28e17f72",
				SecurityUtil.MD5("abc")
		);
		
		assertEquals(
				"4d68abedac24134afd496ccc0c9188a3",
				SecurityUtil.MD5("dakw 8wda8dwa +43 dwnamd wajm&vvc")
		);
		
		assertEquals(
				"8050d75a0aca4f7fd5387d8f8cfc82b3",
				SecurityUtil.MD5("testing testing testing")
		);
		
	}
}
