package fi.hut.soberit.agilefant.web.context;

public interface ContextViewManager {
	
	public ContextView getParentContext();
	
	public void setParentContext(ContextView context);
	
	public void setParentContext(String contextName, int objectId);
	
	public void resetContext();
}
