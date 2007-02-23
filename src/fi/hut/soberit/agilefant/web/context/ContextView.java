package fi.hut.soberit.agilefant.web.context;

import java.io.Serializable;

public class ContextView implements Serializable{
	
	private String contextName;
	private int contextObject;
	
	public ContextView(String name, int objectId){
		if (name == null){
			throw new NullPointerException("Context name is null");
		}
		this.contextName = name;
		this.contextObject = objectId;
	}

	public String getContextName() {
		return contextName;
	}

	public int getContextObject() {
		return contextObject;
	}
}
