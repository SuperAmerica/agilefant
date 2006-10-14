/*
 * Created on 12.10.2006
 */
package fi.hut.soberit.agilefant.web;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class SimpleCounterTest extends TestCase {

	/*
	 * Test method for 'fi.hut.soberit.agilefant.web.SimpleCounter.increaseCount()'
	 */
	public void testIncreaseCount() {
		SimpleCounter sc = new SimpleCounter();
		
		Map map = new HashMap();
		
		sc.setSession(map);
		sc.execute();
		sc.execute();
		
		assertEquals(2, sc.getCounter());
	}

}
