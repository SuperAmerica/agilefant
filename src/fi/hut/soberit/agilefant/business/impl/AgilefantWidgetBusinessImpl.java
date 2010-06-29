package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.AgilefantWidgetBusiness;
import fi.hut.soberit.agilefant.db.AgilefantWidgetDAO;
import fi.hut.soberit.agilefant.model.AgilefantWidget;

@Service("agilefantWidgetBusiness")
@Transactional(readOnly = true)
public class AgilefantWidgetBusinessImpl extends
        GenericBusinessImpl<AgilefantWidget> implements AgilefantWidgetBusiness {

    private AgilefantWidgetDAO agilefantWidgetDAO;
    
    @Autowired
    public void setAgilefantWidgetDAO(AgilefantWidgetDAO agilefantWidgetDAO) {
        this.genericDAO = agilefantWidgetDAO;
        this.agilefantWidgetDAO = agilefantWidgetDAO;
    }

    public AgilefantWidgetBusinessImpl() {
        super(AgilefantWidget.class);
    }

}
