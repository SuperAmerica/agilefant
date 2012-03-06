package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * This class defines a Hibernate entity bean which is used to map 
 * URL tokens to iteration IDs. 
 * 
 * These tokens are used during authentication for readonly access.
 * 
 * @author Dustin Fennell
 *
 */
@Entity
@Table(name = "readonly_iteration_token")
@XmlAccessorType( XmlAccessType.NONE )
public class IterationToken {
    
    private String urlToken;
    private int iterationId;
    
    @Id
    @Column
    public String getUrlToken() {
        return urlToken;
    }
    
    @Column
    public int getIterationId() {
        return iterationId;
    }
}
