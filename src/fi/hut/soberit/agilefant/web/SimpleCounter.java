package fi.hut.soberit.agilefant.web;

import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

public class SimpleCounter extends ActionSupport implements SessionAware {
	
	private static final Logger log = Logger.getLogger(SimpleCounter.class);
	public static final String COUNTER_PARAM = "simpleCounter";
	
	private Map<String, Integer> sessionMap;
	private int counter;
	
	public int getCounter(){
		return counter;
	}
	
	@SuppressWarnings("unchecked")
	public void setSession(Map sessionMap) {
		this.sessionMap = sessionMap;
	}
	
	public String resetCounter(){
		sessionMap.remove(SimpleCounter.COUNTER_PARAM);
		log.info("Counter reseted");
		this.counter = 0;
		return Action.SUCCESS;
	}

	public String execute(){
		increaseCount();
		return Action.SUCCESS;
	}
	
	protected void increaseCount(){
		log.info("Increasing counter");
		Integer count = (Integer)sessionMap.get(SimpleCounter.COUNTER_PARAM);
		if (count == null){
			count = new Integer(0);
		}
		counter = count.intValue();
		counter++;
		count = new Integer(counter);
		sessionMap.put(SimpleCounter.COUNTER_PARAM, count);
		log.info("Counter increased. New value is " + counter); 
	}
}
