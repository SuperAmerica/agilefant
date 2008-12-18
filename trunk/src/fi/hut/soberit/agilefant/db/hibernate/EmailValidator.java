package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;

import org.hibernate.validator.Validator;

/**
 * Implementation of the email validator. When using the email annotation in the
 * hibernate data model, this class is used to validate if the field is a proper
 * email address.
 * 
 * @author Turkka Äijälä
 * @see fi.hut.soberit.agilefant.db.hibernate.Email
 */
public class EmailValidator implements Validator<Email>, Serializable {

    private static final long serialVersionUID = 4334203403474352735L;

    public void initialize(Email parameters) {
    }

    public boolean isValid(Object value) {

        // nulls qualify
        if (value == null)
            return true;

        // non-string objects don't qualify
        if (!(value instanceof String))
            return false;

        String string = (String) value;

        // empty strings qualify
        if (string.equals(""))
            return true;

        // use commons-validator to validate email addresses
        return org.apache.commons.validator.EmailValidator.getInstance()
                .isValid(string);
    }
}