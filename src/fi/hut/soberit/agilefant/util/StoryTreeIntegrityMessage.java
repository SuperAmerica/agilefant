package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.Story;

public class StoryTreeIntegrityMessage {

    private Story source;
    private Story target;
    private StoryHierarchyIntegrityViolationType message;

    public StoryTreeIntegrityMessage(Story source, Story target,
            StoryHierarchyIntegrityViolationType message) {
        this.source = source;
        this.target = target;
        this.message = message;
    }

    public Story getSource() {
        return source;
    }

    public StoryHierarchyIntegrityViolationType getMessage() {
        return message;
    }

    public Story getTarget() {
        return target;
    }

}
