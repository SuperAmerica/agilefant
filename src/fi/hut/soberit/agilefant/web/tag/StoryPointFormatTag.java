package fi.hut.soberit.agilefant.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.model.Story;

public class StoryPointFormatTag extends SpringTagSupport {
    
    private static final long serialVersionUID = -459169890514056670L;
    
    private Story story;
    
    public static final String STORY_POINT_UNITS = "sp.";
    
    @Override
    public int doStartTag() throws JspException {

        String printString = "";
        
        if (story.getStoryPoints() == null) {
            printString = "&mdash;";
        }
        else {
            printString = story.getStoryPoints().toString() + STORY_POINT_UNITS;
        }
        try {
            super.getPageContext().getOut().print(printString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return Tag.EVAL_BODY_INCLUDE;
    }
    
    public void setStory(Story story) {
        this.story = story;
    } 
}
