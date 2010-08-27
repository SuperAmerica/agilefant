package fi.hut.soberit.agilefant.transfer;

import javax.xml.bind.annotation.XmlAttribute;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

public class BacklogInfoTO {

    enum BacklogType { product, project, iteration };
    
    private int id;
    private String name;
    private BacklogType type;
    
    public BacklogInfoTO() {}
    public BacklogInfoTO(Backlog backlog) {
        this.id = backlog.getId();
        this.name = backlog.getName();
        this.type = getBacklogType(backlog); 
    }
    
    private BacklogType getBacklogType(Backlog backlog) {
        if (backlog instanceof Product) {
            return BacklogType.product;
        } else if (backlog instanceof Project) {
            return BacklogType.project;
        }
        return BacklogType.iteration;
    }

    @XmlAttribute(name = "objectId")
    public int getId() {
        return id;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    @XmlAttribute
    public BacklogType getType() {
        return type;
    }

}