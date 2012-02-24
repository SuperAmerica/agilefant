package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

import flexjson.JSON;

/**
 * Hibernate entity bean representing a team consisting of multiple
 * <code>User</code>s.
 * 
 * @see User
 * @author rjokelai
 * 
 */
@BatchSize(size = 20)
@Entity
@Table(name = "teams")
@XmlAccessorType( XmlAccessType.NONE )
public class Team implements Comparable<Team> {

    private int id;

    private String name;

    private String description;

    private Collection<User> users = new HashSet<User>();
    
    private Collection<Product> products = new HashSet<Product>();

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all users.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JSON
    @XmlAttribute(name = "objectId")
    public int getId() {
        return id;
    }

    /**
     * Set the id.
     * 
     * @param id
     *            the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the name of the team.
     * 
     * @return the name
     */
    @Type(type = "escaped_truncated_varchar")
    @Column(unique = true)
    @JSON
    public String getName() {
        return name;
    }

    /**
     * Set the name of the team.
     * 
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the team's description.
     * 
     * @return the description
     */
    @Type(type = "escaped_text")
    @JSON
    public String getDescription() {
        return description;
    }

    /**
     * Set the team's description.
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the team's users.
     * 
     * @return the users
     */
    @ManyToMany(targetEntity = User.class)
    @JoinTable(name = "team_user", joinColumns = { @JoinColumn(name = "Team_id") }, inverseJoinColumns = { @JoinColumn(name = "User_id") })
    @BatchSize(size = 5)
    @JSON(include = false)
    public Collection<User> getUsers() {
        return users;
    }

    /**
     * Set the team's users.
     * 
     * @param users
     *            the users to set
     */
    public void setUsers(Collection<User> users) {
        this.users = users;
    }
    
    /**
     * Get the team's products
     * 
     * return the products
     */
    @ManyToMany(targetEntity = Product.class)
    @JoinTable(name = "team_product", joinColumns = { @JoinColumn(name = "Team_id") }, inverseJoinColumns = { @JoinColumn(name = "Product_id") })
    @BatchSize(size = 5)
    @JSON(include = false)
    public Collection<Product> getProducts() {
        return products;
    }
    
    /**
     * Set the team's products.
     * 
     * @param products the products to be set
     */
    public void setProducts(Collection<Product> products) {
        this.products = products;
    }

    /**
     * Compares the name of the team to an other team's name.
     */
    @Transient
    public int compareTo(Team o) {
        if (o == null) {
            return -1;
        }
        return getName().compareToIgnoreCase(o.getName());
    }

    @Transient
    @JSON(include = false)
    public int getNumberOfUsers() {
        return getUsers().size();
    }

}
