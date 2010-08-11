package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


import fi.hut.soberit.agilefant.model.HourEntry;

/**
 * TimesheetNode is the superclass for nodes in the tree that represents a time sheet.
 * In addition to holding child nodes, it calculates the cumulative spent effort
 * in each branch.
 *   
 * @author Pasi Pekkanen, Vesa Pirilä
 *
 */

@XmlType
@XmlAccessorType( XmlAccessType.NONE )
public abstract class TimesheetNode {
    List<HourEntry> hourEntries = new ArrayList<HourEntry>();
    protected long effortSum = 0;
    
    public TimesheetNode() {
        
    }
    
    public long calculateEffortSum() {
        effortSum = 0l;
        for(HourEntry entry : this.hourEntries) {
            effortSum += entry.getMinutesSpent();
        }
        for(TimesheetNode node : this.getChildren()) {
            effortSum += node.calculateEffortSum();
        }
        return effortSum;
    }
    @XmlAttribute(name="effortSum",required=true)
    public long getEffortSum() {
        return this.effortSum;
    }
    public abstract boolean getHasChildren();
    
    public abstract List<? extends TimesheetNode> getChildren();
    public abstract String getName();
    public abstract int getId();
    
    public void addHourEntry(HourEntry entry) {
        this.hourEntries.add(entry);
    }
    @XmlElementWrapper(name="hourEntries")
    @XmlElement(name="hourEntry")
    public List<HourEntry> getHourEntries() {
        return this.hourEntries;
    }
    @XmlTransient
    public long getOwnEffortSpentSum() {
        long sum = 0;
        for(HourEntry entry : this.hourEntries) {
            sum += entry.getMinutesSpent();
        }
        return sum;
    }
}