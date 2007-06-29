package fi.hut.soberit.agilefant.web.page;

import java.util.Collection;



/**
 * Common interface for all AEF web pages. 
 * 
 * @author jukka
 *
 */
public interface PageItem {
	public Collection<PageItem> getChildren();
	public PageItem getParent();
	public boolean hasChildren();
	public int getId();
	public String getName();
}
