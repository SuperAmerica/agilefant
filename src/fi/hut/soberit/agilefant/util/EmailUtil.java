package fi.hut.soberit.agilefant.util;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.EmailValidator;

import fi.hut.soberit.agilefant.model.User;

/**
 * Email utilities, for validating email addresses and sending email. Also
 * provides the string to postfix names with, and string to prefix subjects
 * with.
 * <p>
 * See http://jakarta.apache.org/commons/email/userguide.html for how to use
 * SimpleEmail and MultiPartEmail objects.
 * <p>
 * Use email.send(); to send a "SimpleEmail email;" or "MultiPartEmail email;".
 * 
 * @author Turkka Äijälä
 */
public class EmailUtil {

    private static final int DEFAULT_SMTP_PORT = 25;

    private String smtpHost;

    private int smtpPort = DEFAULT_SMTP_PORT;

    private boolean useAuthentication;

    private String login;

    private String password;

    private String namePostfix;

    private String subjectPrefix;

    /**
     * Tests if a string is a valid email address.
     * 
     * @param email
     *                email address to test
     * @return test result
     */
    public boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    /**
     * Create a "bare" simple email - instance, with only SMTP host/port
     * configured. You need to provide other details, ie. to, from, subject and
     * contents, before sending.
     * <p>
     * See http://jakarta.apache.org/commons/email/userguide.html for how to use
     * SimpleEmail and MultiPartEmail objects.
     * 
     * 
     * @return bare SimpleEmail object.
     * @see formatEmailSubject
     * @see formatEmailFullName
     */
    public SimpleEmail createSimpleEmail() {
        SimpleEmail email = new SimpleEmail();

        configureEmail(email);

        return email;
    }

    /**
     * Create a "bare" multipart email - instance, with only SMTP host/port
     * configured. You need to provide other details, ie. to, from, subject and
     * contents, before sending.
     * <p>
     * See http://jakarta.apache.org/commons/email/userguide.html for how to use
     * SimpleEmail and MultiPartEmail objects.
     * 
     * @return bare MultiPartEmail object
     * @see formatEmailSubject
     * @see formatEmailFullName
     */
    public MultiPartEmail createMultiPartEmail() {
        MultiPartEmail email = new MultiPartEmail();

        configureEmail(email);

        return email;
    }

    /**
     * Creates a simple email - instance, with SMTP host/port configured, as
     * well as to, from and subject - fields.
     * <p>
     * Sender and receiver names are formatted with formatEmailFullName, subject
     * is formatted with formatEmailSubject.
     * <p>
     * You need to provide contents before sending.
     * <p>
     * See http://jakarta.apache.org/commons/email/userguide.html for how to use
     * SimpleEmail and MultiPartEmail objects.
     * 
     * @param to
     *                User to send the email to
     * @param from
     *                User the email seems to be from
     * @param subject
     *                email subject
     * @return SimpleEmail object, with to, from, subject defined
     * @throws IllegalArgumentException
     *                 if to or from - user is invalid
     */
    public SimpleEmail createSimpleEmail(User to, User from, String subject)
            throws IllegalArgumentException, EmailException {
        isValidUser(from);
        isValidUser(to);

        SimpleEmail email = new SimpleEmail();

        configureEmail(email);

        email.addTo(to.getEmail(), formatEmailFullName(to));
        email.setFrom(from.getEmail(), formatEmailFullName(from));
        email.setSubject(formatEmailSubject(subject));

        return email;
    }

    /**
     * Creates a multipart email - instance, with SMTP host/port configured, as
     * well as to, from and subject - fields.
     * <p>
     * Sender and receiver names are formatted with formatEmailFullName, subject
     * is formatted with formatEmailSubject.
     * <p>
     * You need to provide contents before sending.
     * <p>
     * See http://jakarta.apache.org/commons/email/userguide.html for how to use
     * SimpleEmail and MultiPartEmail objects.
     * 
     * @param to
     *                User to send the email to
     * @param from
     *                User the email seems to be from
     * @param subject
     *                email subject
     * @return MultiPartEmail object, with to, from, subject defined
     * @throws IllegalArgumentException
     *                 if to or from - user is invalid
     */
    public MultiPartEmail createMultiPartEmail(User to, User from,
            String subject) throws IllegalArgumentException, EmailException {
        isValidUser(from);
        isValidUser(to);

        MultiPartEmail email = new MultiPartEmail();

        configureEmail(email);

        email.addTo(to.getEmail(), formatEmailFullName(to));
        email.setFrom(from.getEmail(), formatEmailFullName(from));
        email.setSubject(formatEmailSubject(subject));

        return email;
    }

    private void isValidUser(User user) throws IllegalArgumentException {
        if (user == null)
            throw new IllegalArgumentException("user was null");
        if (user.getEmail() == null || user.getEmail().equals(""))
            throw new IllegalArgumentException(
                    "user has no email address defined");
    }

    /**
     * Configures an email instance to have proper smtp-settings.
     */
    private void configureEmail(Email email) {
        email.setHostName(smtpHost);
        email.setSmtpPort(smtpPort);

        if (useAuthentication)
            email.setAuthentication(login, password);
    }

    /**
     * Formats a subject line, prefixing it with applications subject prefix. eg
     * "hello" -> "(Agilefant) hello".
     * 
     * @param subject
     *                subject to format
     * @return formatted subject
     */
    public String formatEmailSubject(String subject) {
        String result = "";

        if (subjectPrefix != null)
            result += subjectPrefix;

        result += subject;

        return result;
    }

    /**
     * Formats a full name of a user, postfixing it with applications postfix.
     * Eg. "John Doe" -> "John Doe (Agilefant)".
     * 
     * @param user
     *                full name, which to format
     * @return formatted full name
     */
    public String formatEmailFullName(String name) {

        // add the postfix, if it exists
        if (namePostfix != null)
            name += namePostfix;

        return name;
    }

    /**
     * Formats a full name of a user, postfixing it with applications postfix.
     * Eg. "John Doe" -> "John Doe (Agilefant)".
     * 
     * @param user
     *                user instance, full name of which to format
     * @return formatted full name
     */
    public String formatEmailFullName(User user) {
        String name = user.getFullName();

        // if there's was no full name defined, use the login name
        if (name == null || name.equals(""))
            name = user.getLoginName();

        // if there was even no login name, use an empty string
        if (name == null)
            name = "";

        return formatEmailFullName(name);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public boolean isUseAuthentication() {
        return useAuthentication;
    }

    public void setUseAuthentication(boolean useAuthentication) {
        this.useAuthentication = useAuthentication;
    }

    public String getNamePostfix() {
        return namePostfix;
    }

    public void setNamePostfix(String senderNamePostfix) {
        this.namePostfix = senderNamePostfix;
    }

    public String getSubjectPrefix() {
        return subjectPrefix;
    }

    public void setSubjectPrefix(String subjectPrefix) {
        this.subjectPrefix = subjectPrefix;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(int smtpPort) {
        this.smtpPort = smtpPort;
    }
}
