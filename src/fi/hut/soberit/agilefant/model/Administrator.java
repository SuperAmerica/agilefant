package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing a administrator to product relationship.
 * <p>
 * Administrators are teams or individual users who have all access enabled on
 * specified project. The default for new projects is to have automatic admins.
 * </p>
 * <p>
 * Each admin pair consists of an admin id and a project id.
 * </p>
 * @warning The administration stuff is currently not functioning although info
 *          is being stored in the DB.
 * 
 */
@BatchSize(size=20)
@Entity
@Table(name = "admin_product")
@XmlAccessorType( XmlAccessType.NONE )
public class Administrator {

    private int admin_id;
    
    private int product_id;
    
    /**
     * Get the admin id of this object.
     */
    @Id
    @Type(type = "int")
    @Column(nullable = false)
    public int getAdminId() {
        return admin_id;
    }
    
    /**
     * Set the admin id of this object.
     * <p>
     * You shouldn't normally call this.
     */
    public void setAdminId(int id) {
        this.admin_id = id;
    }
    
    /**
     * Get the product id of this object.
     */
    @Id
    @Type(type = "int")
    @Column(nullable = false)
    public int getProductId() {
        return product_id;
    }
    
    /**
     * Set the product id of this object.
     * <p>
     * You shouldn't normally call this.
     */
    public void setProductId(int id) {
        this.product_id = id;
    }
}
