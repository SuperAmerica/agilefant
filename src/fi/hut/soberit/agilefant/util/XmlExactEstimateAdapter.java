package fi.hut.soberit.agilefant.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import fi.hut.soberit.agilefant.model.ExactEstimate;

public class XmlExactEstimateAdapter extends XmlAdapter<Long, ExactEstimate> {

    @Override
    public Long marshal(ExactEstimate v) throws Exception {
        if(v == null) {
            return null;
        }
        return v.getMinorUnits();
    }

    @Override
    public ExactEstimate unmarshal(Long v) throws Exception {
        return new ExactEstimate(v);
    }

}
