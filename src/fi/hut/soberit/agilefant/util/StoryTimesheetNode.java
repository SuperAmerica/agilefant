package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.List;

import fi.hut.soberit.agilefant.model.Story;

/**
 * A class representing a backlog item node in the timesheet tree.
 * Fetches the hour entries associated with this backlog item.
 * 
 * @author Pasi Pekkanen, Vesa Pirilä
 *
 */
public class StoryTimesheetNode extends TimesheetNode {
    private Story story;

    List<TaskTimesheetNode> childTasks = new ArrayList<TaskTimesheetNode>();
    
    public StoryTimesheetNode(Story story) {
        super();
        this.story = story;
    }

    @Override
    public List<? extends TimesheetNode> getChildren() {
        return this.childTasks;
    }

    @Override
    public String getName() {
        return this.story.getName();
    }

    @Override
    public boolean getHasChildren() {
        return this.childTasks.size() != 0;
    }
    @Override
    public int getId() {
        return story.getId();
    }
    
    public void addChild(TaskTimesheetNode node) {
        this.childTasks.add(node);
    }

    public Story getStory() {
        return this.story;
    }
}
