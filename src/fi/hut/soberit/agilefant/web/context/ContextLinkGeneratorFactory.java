package fi.hut.soberit.agilefant.web.context;

import fi.hut.soberit.agilefant.model.Task;

public class ContextLinkGeneratorFactory {
    private static ContextLinkGeneratorFactory instance = new ContextLinkGeneratorFactory();

    public static ContextLinkGeneratorFactory getInstance() {
        return instance;
    }
    
    public <T> ContextLinkGenerator<T> getContextLinkGenerator(T obj) {
        return getContextLinkGenerator((Class<? extends T>)obj.getClass());
    }
    
    public <T> ContextLinkGenerator<T> getContextLinkGenerator(Class<? extends T> cls) {
        if (Task.class.isAssignableFrom(cls)) {
            return (ContextLinkGenerator<T>) new TaskContextLinkGenerator();
        }
        
        return null;
    }
}
