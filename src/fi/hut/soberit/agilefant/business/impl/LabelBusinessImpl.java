package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.LabelBusiness;
import fi.hut.soberit.agilefant.db.LabelDAO;
import fi.hut.soberit.agilefant.model.Label;

@Service("labelBusiness")
@Transactional
public class LabelBusinessImpl extends GenericBusinessImpl<Label> implements
        LabelBusiness {
    
    private LabelDAO labelDAO;

    public LabelBusinessImpl() {
        super(Label.class);
    }
    
    @Autowired
    public void setLabelDAO(LabelDAO labelDAO) {
        this.genericDAO = labelDAO;
        this.labelDAO = labelDAO;
    }
}