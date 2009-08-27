package fi.hut.soberit.agilefant.exception;

/**
 * Used for disallowed controller operations.
 * @author rjokelai
 *
 */
public class OperationNotPermittedException extends RuntimeException {

    private static final long serialVersionUID = 2583489006574217797L;

    /**
     * Create a new exception.
     */
    public OperationNotPermittedException() {
        super();
    }

    /**
     * Create a new exception with a message.
     * @param message
     */
    public OperationNotPermittedException(String message) {
        super(message);
    }

}
