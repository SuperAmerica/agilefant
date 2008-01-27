package fi.hut.soberit.agilefant.business;

import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.State;

/**
 * Business interface for handling functionality related to tasks.
 * 
 * @author Mika Salminen
 * 
 */
public interface TaskBusiness {

    /**
     * Updates multiple tasks' states with one call. Takes Map with elements of
     * form: <code>[task_id => new_status] </code> as parameter.
     * 
     * 
     * @param newStatesMap
     *                <code>Map</code> with elements <code>[task_id => new_status]</code>
     *                defining the new states for tasks.
     * 
     */
    public void updateMultipleTaskStates(Map<Integer, State> newStatesMap)
            throws ObjectNotFoundException;
}
