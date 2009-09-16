package fi.hut.soberit.agilefant.web.context;

public interface ContextLinkGenerator<T> {
    void setObject(T obj);
    T getObject();
    
    String createLink();
}
