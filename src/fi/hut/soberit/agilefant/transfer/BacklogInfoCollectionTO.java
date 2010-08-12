package fi.hut.soberit.agilefant.transfer;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "backlogs")
public class BacklogInfoCollectionTO {

    private Collection<BacklogInfoTO> backlogs = new HashSet<BacklogInfoTO>();

    @XmlElement(name = "backlog")
    public Collection<BacklogInfoTO> getBacklogs() {
        return backlogs;
    }

    public void setBacklogs(Collection<BacklogInfoTO> backlogs) {
        this.backlogs = backlogs;
    }
}
