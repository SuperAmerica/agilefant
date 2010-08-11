package fi.hut.soberit.agilefant.util;

import java.util.Calendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

public class XmlDateTimeAdapter extends XmlAdapter<Calendar, DateTime> {

    @Override
    public Calendar marshal(DateTime v) throws Exception {
        if(v == null) {
            return null;
        }
        return v.toGregorianCalendar();
    }

    @Override
    public DateTime unmarshal(Calendar v) throws Exception {
        if(v == null) {
            return null;
        }
        return new DateTime(v);
    }



}
