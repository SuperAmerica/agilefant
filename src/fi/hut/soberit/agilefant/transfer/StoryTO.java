package fi.hut.soberit.agilefant.transfer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.BeanCopier;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import flexjson.JSON;

@XmlType
@XmlAccessorType( XmlAccessType.NONE )
public class StoryTO extends Story {

    // Additional fields
    private StoryMetrics metrics;
    // Context-specific rank
    private Integer rank;

    public StoryTO() {}
    
    public StoryTO(Story story) {
        BeanCopier.copy(story, this);
    }

    public void setMetrics(StoryMetrics metrics) {
        this.metrics = metrics;
    }

    public StoryMetrics getMetrics() {
        return metrics;
    }

    @JSON
    @XmlAttribute
    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

}
