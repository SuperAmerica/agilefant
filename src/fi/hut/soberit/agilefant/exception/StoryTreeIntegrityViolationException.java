package fi.hut.soberit.agilefant.exception;

import java.util.List;

import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

public class StoryTreeIntegrityViolationException extends RuntimeException {

    private static final long serialVersionUID = 2396029624894442253L;
    private List<StoryTreeIntegrityMessage> messages;

    public StoryTreeIntegrityViolationException(
            List<StoryTreeIntegrityMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        StringBuilder ret = new StringBuilder();
        for (StoryTreeIntegrityMessage message : this.messages) {
            ret.append(message.getMessageName());
            ret.append(' ');
            ret.append((char)Character.LINE_SEPARATOR);
        }
        return ret.toString();
    }
}
