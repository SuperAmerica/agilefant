package fi.hut.soberit.agilefant.web.context;

import com.sun.jmx.snmp.tasks.Task;

public class ContextLinkGeneratorFactory {
    private static ContextLinkGeneratorFactory instance = new ContextLinkGeneratorFactory();

    public static ContextLinkGeneratorFactory getInstance() {
        return instance;
    }
    
    public <T> ContextLinkGenerator<T> getContextLinkGenerator(T obj) {
        return getContextLinkGenerator(obj.getClass());
    }
    
    public <T> ContextLinkGenerator<T> getContextLinkGenerator(Class cls) {
        if (Task.class.isAssignableFrom(cls)) {
            return (ContextLinkGenerator<T>) new TaskContextLinkGenerator();
        }
        
        return null;
    }
}
