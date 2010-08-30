package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.LabelBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
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
    private StoryBusiness storybusiness;
    
    @Autowired
    public void setLabelDAO(LabelDAO labelDAO) {
        this.genericDAO = labelDAO;
        this.labelDAO = labelDAO;
    }
    
    @Override
    public void store(Label label){
        throw new OperationNotPermittedException("Labels cannot be edited!");
    }

    public void deleteLabel(Label label) {
       labelDAO.remove(label);
    }
        
    public void createStoryLabels(List<String> labelNames, Integer storyId) {
        if(labelNames == null) {
            return;
        }
        User currentUser = SecurityUtil.getLoggedUser();
        Story story = storybusiness.retrieve(storyId);
        for (String name : labelNames){
                if (labelDAO.labelExists(name, story)){
                } else {
                    Label label = new Label();
                    label.setDisplayName(name);
                    label.setName(name);
                    label.setCreator(currentUser);
                    label.setStory(story);
                    label.setTimestamp(new DateTime());
                    int labelId = (Integer)labelDAO.create(label);
                    Label persisted = labelDAO.get(labelId);
                    story.getLabels().add(persisted);
                }
        }
    }
    
    public List<Label> lookupLabelsLike(String labelName) {
        return labelDAO.lookupLabelsLike(labelName);
    }
    


}