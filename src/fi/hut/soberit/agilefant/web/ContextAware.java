package fi.hut.soberit.agilefant.web;

/**
 * 
 */
public interface ContextAware {
    public static final String CONTEXT_OBJECT_ID_AFFIX = "contextViewId_";
    public static final String CONTEXT_NAME = "contextView_lastContext";
    
    public String getContextName();
    public int getContextObjectId();
}
