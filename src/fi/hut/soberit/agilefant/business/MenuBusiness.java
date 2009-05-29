package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.util.MenuData;
import fi.hut.soberit.agilefant.model.Backlog;

/**
 * Business interface for handling functionality related to the lefthand menu.
 * 
 * @author rjokelai
 * 
 */
public interface MenuBusiness {

    /**
     * Get the children of the selected pageitem.
     * <p>
     * To get the children of the "root" element, i.e. all product backlogs, use
     * null as parameter.
     * 
     * @param backlog
     *            The parent of wanted pageitems
     * @return a menudata object
     */
    public MenuData getSubMenuData(Backlog backlog);

}
