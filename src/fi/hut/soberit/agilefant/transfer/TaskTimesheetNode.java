package fi.hut.soberit.agilefant.transfer;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.util.TimesheetNode;

@XmlType
@XmlAccessorType( XmlAccessType.NONE )
public class TaskTimesheetNode extends TimesheetNode {
    Task task;


    public TaskTimesheetNode() { }
    
    public TaskTimesheetNode(Task task) {
        super();
        this.task = task;
    }
    @Override
    public List<? extends TimesheetNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    @XmlAttribute(name="taskName")
    public String getName() {
        return this.task.getName();
    }

    @Override
    public boolean getHasChildren() {
        return false;
    }
    @Override
    @XmlAttribute(name = "taskId")
    public int getId() {
        return task.getId();
    }
    
    public Task getTask() {
        return this.task;
    }
    
    @XmlElementWrapper(name="hourEntries")
    @XmlElement(name="hourEntry", type=TaskHourEntry.class)
    @Override
    public List<HourEntry> getHourEntries() {
        return super.getHourEntries();
    }

}
