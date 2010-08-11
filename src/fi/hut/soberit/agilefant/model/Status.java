package fi.hut.soberit.agilefant.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * A status enumeration, which represents the status of project.
 * 
 */
@XmlEnum
public enum Status {
    @XmlEnumValue("GREEN")      GREEN,
    @XmlEnumValue("YELLOW")     YELLOW,
    @XmlEnumValue("RED")        RED,
    @XmlEnumValue("GREY")       GREY,
    @XmlEnumValue("BLACK")      BLACK;

    public int getOrdinal() {
        return this.ordinal();
    }

    public String getName() {
        return this.name();
    }

}
