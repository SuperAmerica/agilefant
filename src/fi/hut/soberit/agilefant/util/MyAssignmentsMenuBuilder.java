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

    private MenuDataNode ensureNode(Project project) { // returns node if it exists, creates it if doesn't
        Integer currentIndex = projectIds.get(project.getId());
        if (currentIndex != null) {
            return nodes.get(currentIndex);
        }
        MenuDataNode node = constructNode(project);
        node.setExpand(true);
        projectIds.put(project.getId(), nodes.size());
        nodes.add(node);
        return node;
    }

    private MenuDataNode ensureNode(Iteration iteration) { // returns node if it exists, creates it if doesn't
        if(iteration.getParent() != null){
            // check to see if node exists.
            Integer currentIndex = iterationIds.get(iteration.getId());
            if (currentIndex != null) {
                Integer projectIndex = projectIds
                        .get(iteration.getParent().getId());
                MenuDataNode projectNode = nodes.get(projectIndex);
                return projectNode.getChildren().get(currentIndex);
            }
            
            // if node doesn't exist, create it.
            MenuDataNode projectNode = ensureNode((Project) iteration.getParent());
            MenuDataNode node = constructNode(iteration);         
            // add to iterationIds (ID of iteration, location of node under its parent (e.g. fifth child, first child))
            iterationIds.put(iteration.getId(), projectNode.getChildren().size());
            projectNode.getChildren().add(node);
            return node;
        } else {
            // standalone iteration
            // check to see if node exists
            Integer currentIndex = projectIds.get(iteration.getId());
            if (currentIndex != null) {
                return nodes.get(currentIndex);
            }
            
            // if node doesn't exist, create it.
            MenuDataNode node = constructNode(iteration);
            // add to projectIds (ID of standalone iteration, location of node)
            projectIds.put(iteration.getId(), nodes.size());
            nodes.add(node);
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
