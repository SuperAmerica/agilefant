package fi.hut.soberit.agilefant.web.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class TaskContextLinkGenerator implements ContextLinkGenerator<Task>, Serializable {
    private static final long serialVersionUID = -3534351891255292779L;
    private Task task;

    public TaskContextLinkGenerator() {
    }

    public Task getObject() {
        return task;
    }

    public void setObject(Task obj) {
        task = obj;
    }
    
    public String createLink() {
        Story story = task.getStory();
        Backlog backlog;
        
        if (task == null) {
            return "";
        }
        
        if (story != null) {
            backlog = story.getBacklog();
        }
        
        else {
            backlog = task.getIteration();
        }
        
        if (backlog == null) {
            return "";
        }
        
        String uri;
        if (backlog != null && backlog instanceof Product) {
            uri= "editProduct.action?productId=" + backlog.getId();
        } else if (backlog != null && backlog instanceof Project) {
            uri = "editProject.action?projectId=" + backlog.getId();
        } else if (backlog != null && backlog instanceof Iteration) {
            uri = "editIteration.action?iterationId=" + backlog.getId();
        } else {
            return "";
        }

        List<String> fragmentPieces = new ArrayList<String>();
        if (story != null) {
            fragmentPieces.add("storyId=" + story.getId());
        }
        
        fragmentPieces.add("taskId=" + task.getId());
        
        uri += "#" + StringUtils.join(fragmentPieces, "&");
        return uri;
    }
}
