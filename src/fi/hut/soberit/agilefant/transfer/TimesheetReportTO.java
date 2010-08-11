package fi.hut.soberit.agilefant.transfer;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="timesheetReport")
@XmlAccessorType( XmlAccessType.NONE )
public class TimesheetReportTO {

    private List<BacklogTimesheetNode> productNodes;

    private long totalEffortSum;
    
    @XmlElementWrapper(name="products")
    @XmlElement(name="productNode")
    public List<BacklogTimesheetNode> getProductNodes() {
        return productNodes;
    }
    public void setProductNodes(List<BacklogTimesheetNode> productNodes) {
        this.productNodes = productNodes;
    }
    @XmlAttribute
    public long getTotalEffortSum() {
        return totalEffortSum;
    }
    public void setTotalEffortSum(long totalEffortSum) {
        this.totalEffortSum = totalEffortSum;
    }
}
