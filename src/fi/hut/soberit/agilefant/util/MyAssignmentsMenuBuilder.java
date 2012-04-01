package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.MenuDataNode;

public class MyAssignmentsMenuBuilder {

    private List<MenuDataNode> nodes = new ArrayList<MenuDataNode>();
    
    private Map<Integer, Integer> projectIds = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> standaloneIds = new HashMap<Integer, Integer>();
    
    private Map<Integer, Integer> iterationIds = new HashMap<Integer, Integer>();
    
    private MenuDataNode constructNode(Backlog backlog) {
        MenuDataNode node = new MenuDataNode();
        node.setId(backlog.getId());
        node.setTitle(backlog.getName());
        return node;
    }
    
    private MenuDataNode constructNode(Story story) {
        MenuDataNode node = new MenuDataNode();
        node.setId(story.getIteration().getId());
        node.setTitle(story.getName());
        return node;
    }
    
    private void insertNode(Project project, MenuDataNode node) { // adds project node to list
        Integer nodePos = projectIds.size();
        // add to projectIds (ID of project, location of node in list)
        projectIds.put(project.getId(), nodePos);
        nodes.add(nodePos, node);
        
        // update position of standalone iterations
        for (Map.Entry<Integer, Integer> standalone : standaloneIds.entrySet()) {
            Integer standaloneId = standalone.getKey();
            Integer oldPos = standalone.getValue();
            standaloneIds.put(standaloneId, oldPos + 1);
        }
    }
    
    private void insertNode(Iteration iteration, MenuDataNode node, MenuDataNode parentNode) { // adds iteration node to list
        if (iteration.getParent() == null) { // standalone iteration
            Integer nodePos = nodes.size();
            // add to standaloneIds (ID of iteration, location of node in list)
            standaloneIds.put(iteration.getId(), nodePos);
            nodes.add(nodePos, node);
        } else { // non-standalone
            // add to iterationIds (ID of iteration, location of node under its parent (e.g. fifth child, first child))
            iterationIds.put(iteration.getId(), parentNode.getChildren().size());
            parentNode.getChildren().add(node);
        }
    }
    
    private MenuDataNode ensureNode(Project project) { // returns node if it exists, creates it if doesn't
        Integer currentIndex = projectIds.get(project.getId());
        if (currentIndex != null) {
            return nodes.get(currentIndex);
        }
        MenuDataNode node = constructNode(project);
        node.setExpand(true);
        insertNode(project, node);
        return node;
    }
    
    private MenuDataNode ensureNode(Iteration iteration) { // returns node if it exists, creates it if doesn't
        if (iteration.getParent() != null){ // non-standalone iteration
            // check to see if node exists.
            Integer currentIndex = iterationIds.get(iteration.getId());
            if (currentIndex != null) {
                Integer projectIndex = projectIds.get(iteration.getParent().getId());
                MenuDataNode projectNode = nodes.get(projectIndex);
                return projectNode.getChildren().get(currentIndex);
            }
            
            // if node doesn't exist, create it.
            MenuDataNode projectNode = ensureNode((Project) iteration.getParent());
            MenuDataNode node = constructNode(iteration);
            insertNode(iteration, node, projectNode);
            return node;
        } else {
            // standalone iteration
            // check to see if node exists
            Integer currentIndex = standaloneIds.get(iteration.getId());
            if (currentIndex != null) {
                return nodes.get(currentIndex);
            }
            
            // if node doesn't exist, create it.
            MenuDataNode node = constructNode(iteration);
            node.setExpand(true);
            insertNode(iteration, node, null);
            return node;
        }        
    }
    
    public void insert(Iteration iteration) {
        ensureNode(iteration);
    }
    
    public void insert(Project project) {
        ensureNode(project);
    }
    
    public void insert(Story story) {
        MenuDataNode iterationNode = ensureNode((Iteration) story.getIteration());

        MenuDataNode node = constructNode(story);
        iterationNode.getChildren().add(node);
    }
    
    public List<MenuDataNode> getNodes() {
        return nodes;
    }

}
