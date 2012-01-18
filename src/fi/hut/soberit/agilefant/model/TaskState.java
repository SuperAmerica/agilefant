package fi.hut.soberit.agilefant.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TaskState {

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

}
