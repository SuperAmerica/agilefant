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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.db.hibernate.Email;
import flexjson.JSON;

/**
 * Hibernate entity bean representing a user. User represents a person using the
 * webapp: it's more a thing of the implementation than anything conceptual.
 * <p>
 * The user carries information on username, password, full name and email. Also
 * there're different collections of items, where this user is assigned.
 */
@BatchSize(size = 20)
@Entity
@Table(name = "users")
public class User {

    private int id;

    private String password;

    private String loginName;

    private String fullName;

    private String email;

    private String initials;

    private boolean enabled = true;

    private Collection<Team> teams = new HashSet<Team>();
    
    private Collection<Assignment> assignments = new HashSet<Assignment>();

    /**
     * Get the id of this object.
     * <p>
     * The id is unique among all users.
     */
    // tag this field as the id
    @Id
    // generate automatically
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JSON
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
    @JSON
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
    @JSON
    public String getLoginName() {
        return loginName;
    }

    /** Set login name. */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /** Get password. */
    @Type(type = "truncated_varchar")
    @JSON(include = false)
    public String getPassword() {
        return password;
    }

    /** Set password. */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Get login name. */
    @Transient
    @JSON
    public String getName() {
        return this.loginName;
    }

    /**
     * Get email addresses. Note that the field is validated to be a valid a
     * email address: an exception is thrown on store, if it's invalid.
     */
    @Column(nullable = true)
    @Email
    @Type(type = "truncated_varchar")
    @JSON
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
     * 
     * @return the initials
     */
    @JSON
    public String getInitials() {
        return initials;
    }

    /**
     * Set the user's initials.
     * 
     * @param initials
     *            the initials to set
     */
    public void setInitials(String initials) {
        this.initials = initials;
    }

    /**
     * Get the user's teams.
     * 
     * @return the teams
     */
    @ManyToMany(targetEntity = Team.class)
    @JoinTable(name = "team_user", joinColumns = { @JoinColumn(name = "User_id") }, inverseJoinColumns = { @JoinColumn(name = "Team_id") })
    @JSON(include = false)
    public Collection<Team> getTeams() {
        return teams;
    }

    /**
     * Set the user's teams.
     * 
     * @param teams
     */
    public void setTeams(Collection<Team> teams) {
        this.teams = teams;
    }

    /**
     * Check, if the user is disabled
     * 
     * @return true, if user is disabled, false otherwise
     */
    @JSON
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the user's enabled status.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the users assignments to backlogs.
     * @return
     */
    @OneToMany(mappedBy = "user")
    @JSON(include = false)
    public Collection<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Collection<Assignment> assignments) {
        this.assignments = assignments;
    }

}
