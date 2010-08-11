package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.TimesheetNode;

/**
 * A class representing a backlog item node in the timesheet tree.
 * Fetches the hour entries associated with this backlog item.
 * 
 * @author Pasi Pekkanen, Vesa Pirilä
 *
 */
@XmlRootElement
public class StoryTimesheetNode extends TimesheetNode {
    private Story story;
    private long taskEffortSum;
    
    List<TaskTimesheetNode> childTasks = new ArrayList<TaskTimesheetNode>();
    
    @Override
    public long calculateEffortSum() {
        taskEffortSum  = 0l;
        for(TaskTimesheetNode node : this.childTasks) {
            taskEffortSum += node.calculateEffortSum();
        }
        effortSum = taskEffortSum + this.getOwnEffortSpentSum();
        return effortSum;
    }
    
    public StoryTimesheetNode() {}
    
    public StoryTimesheetNode(Story story) {
        super();
        this.story = story;
    }

    @Override
    public List<? extends TimesheetNode> getChildren() {
        return this.childTasks;
    }

    @Override
    @XmlAttribute(name="storyName")
    public String getName() {
        return this.story.getName();
    }

    @Override
    public boolean getHasChildren() {
        return this.childTasks.size() != 0;
    }
    @Override
    @XmlAttribute(name="storyId")
    public int getId() {
        return story.getId();
    }
    
    public void addChild(TaskTimesheetNode node) {
        this.childTasks.add(node);
    }

    @XmlTransient
    public Story getStory() {
        return this.story;
    }

    public long getTaskEffortSum() {
        return taskEffortSum;
    }
}
