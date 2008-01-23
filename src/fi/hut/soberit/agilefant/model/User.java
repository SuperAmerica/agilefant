package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.db.hibernate.Email;
import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * Hibernate entity bean representing a user. User represents a person using the
 * webapp: it's more a thing of the implementation than anything conceptual.
 * <p>
 * The user carries information on username, password, full name and email. Also
 * there're different collections of items, where this user is assigned.
 */
@Entity
public class User implements PageItem {

    private int id;

    private String password;

    private String loginName;

    private String fullName;

    private String email;
    
    private String initials;

    private Collection<Task> assignments = new HashSet<Task>();

    private Collection<Backlog> backlogs = new HashSet<Backlog>();

    private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all users.
     */
    // tag this field as the id
    @Id
    // generate automatically
    @GeneratedValue(strategy = GenerationType.AUTO)
    // not nullable
    @Column(nullable = false)
    public int getId() {
        return id;
    }

    /**
     * Set the id of this object.
     * <p>
     * You shouldn't normally call this.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Get full name. */
    @Type(type = "escaped_truncated_varchar")
    public String getFullName() {
        return fullName;
    }

    /** Set full name. */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /** Get login name. */
    @Column(unique = true)
    @Type(type = "escaped_truncated_varchar")
    public String getLoginName() {
        return loginName;
    }

    /** Set login name. */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /** Get password. */
    @Type(type = "truncated_varchar")
    public String getPassword() {
        return password;
    }

    /** Set password. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** {@inheritDoc} */
    @Transient
    public Collection<PageItem> getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    /** Get login name. */
    @Transient
    public String getName() {
        // TODO Auto-generated method stub
        return this.loginName;
    }

    /** {@inheritDoc} */
    @Transient
    public PageItem getParent() {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Transient
    public boolean hasChildren() {
        // TODO Auto-generated method stub
        return false;
    }

    /** Get backlog items, where the user is assigned. */
    @OneToMany(mappedBy = "assignee")
    public Collection<BacklogItem> getBacklogItems() {
        return backlogItems;
    }

    /** Set backlog items, where the user is assigned. */
    public void setBacklogItems(Collection<BacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }

    /** Get backlogs, where the user is assigned. */
    @OneToMany(mappedBy = "assignee")
    public Collection<Backlog> getBacklogs() {
        return backlogs;
    }

    /** Set backlogs, where the user is assigned. */
    public void setBacklogs(Collection<Backlog> backlogs) {
        this.backlogs = backlogs;
    }

    /** Set all Assignables, where this user is assigned. */
    @Transient
    public Collection<Assignable> getAssignables() {
        Collection<Assignable> collection = new HashSet<Assignable>();

        collection.addAll(getBacklogs());
        collection.addAll(getBacklogItems());

        return collection;
    }

    /**
     * Get email addresses. Note that the field is validated to be a valid a
     * email address: an exception is thrown on store, if it's invalid.
     */
    @Column(nullable = true)
    @Email
    @Type(type = "truncated_varchar")
    public String getEmail() {
        return email;
    }

    /**
     * Set email addresses. Note that the field is validated to be a valid a
     * email address: an exception is thrown on store, if it's invalid.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the user's initials.
     * @return the initials
     */
    public String getInitials() {
        return initials;
    }

    /**
     * Set the user's initials.
     * @param initials the initials to set
     */
    public void setInitials(String initials) {
        this.initials = initials;
    }
}
