package fi.hut.soberit.agilefant.business;

/**
 * Business interface for handling functionality related to password.
 * 
 * @author Teemu Ilmonen
 * 
 */
public interface PasswordBusiness {
	/**
	 * Generates a password and mails it to the e-mail address of an
	 * <code>User</code>.
	 * 
	 * @param user_id
	 *            Id of the user who needs a new password sent.
	 */
	public void generateAndMailPassword(int user_id);
}
