package fi.hut.soberit.agilefant.web.page;

import java.util.Collection;

/**
 * Common interface for all AEF web pages.
 * 
 * @author jukka
 * 
 */
public interface Backlog {
    public Collection<Backlog> getChildren();

    public Backlog getParent();

    public boolean hasChildren();

    public int getId();

    public String getName();
}
