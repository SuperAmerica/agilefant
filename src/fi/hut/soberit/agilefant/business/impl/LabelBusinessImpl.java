package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.LabelBusiness;
import fi.hut.soberit.agilefant.db.LabelDAO;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

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
    
    @Override
    public void store(Label label){
        throw new OperationNotPermittedException("Labels cannot be edited!");
    }
    
    public Integer createLabel(Label label){
        label.setName(label.getDisplayName());
        User currentUser = SecurityUtil.getLoggedUser();
        label.setCreator(currentUser);
        label.setTimestamp(new DateTime());
        Integer id = (Integer)labelDAO.create(label);
        return id;
    }

    public void deleteLabel(Label label) {
       labelDAO.remove(label);
    }
    public Integer createStoryLabel(Label label, Story story) {
        if (labelDAO.labelExists(label.getDisplayName(), story)){
            throw new OperationNotPermittedException("Label exists!");
        }
        label.setStory(story);
        return createLabel(label);
    }
    
    public List<Label> lookupLabelsLike(String labelName) {
        return labelDAO.lookupLabelsLike(labelName);
    }

}