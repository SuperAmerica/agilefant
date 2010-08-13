package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Entity
@Table(name = "widgets")
@XmlAccessorType( XmlAccessType.NONE )
public class AgilefantWidget {
        
    @Id
    @GeneratedValue
    private int id;
    
    @Column(nullable = false)
    private String type;
    
    @ManyToOne
    private WidgetCollection widgetCollection;
    
    @Column
    private Integer listNumber;
    
    @Column
    private Integer position;
    
    @Column
    private Integer objectId;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WidgetCollection getWidgetCollection() {
        return widgetCollection;
    }

    public void setWidgetCollection(WidgetCollection widgetCollection) {
        this.widgetCollection = widgetCollection;
    }

    public Integer getListNumber() {
        return listNumber;
    }

    public void setListNumber(Integer listNumber) {
        this.listNumber = listNumber;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }


    public Integer getObjectId() {
        return objectId;
    }


    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }
    
}
