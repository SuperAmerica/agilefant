package fi.hut.soberit.agilefant.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum StoryState {

    @XmlEnumValue("NOT_STARTED") NOT_STARTED,
    @XmlEnumValue("STARTED")     STARTED,
    @XmlEnumValue("PENDING")     PENDING,
    @XmlEnumValue("BLOCKED")     BLOCKED,
    @XmlEnumValue("IMPLEMENTED") IMPLEMENTED,
    @XmlEnumValue("DONE")        DONE,
    @XmlEnumValue("DEFERRED")    DEFERRED;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }

    public static final Set<StoryState> valueSet;

    static {
        Set<StoryState> allValues = new HashSet<StoryState>();
        allValues.add(NOT_STARTED);
        allValues.add(STARTED);
        allValues.add(PENDING);
        allValues.add(BLOCKED);
        allValues.add(IMPLEMENTED);
        allValues.add(DONE);
        allValues.add(DEFERRED);
        valueSet = Collections.unmodifiableSet(allValues);
    }

}
