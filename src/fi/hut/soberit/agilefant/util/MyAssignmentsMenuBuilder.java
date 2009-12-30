package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
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

    private MenuDataNode ensureNode(Project project) {
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

    private MenuDataNode ensureNode(Iteration iteration) {
        Integer currentIndex = iterationIds.get(iteration.getId());
        if (currentIndex != null) {
            Integer projectIndex = projectIds
                    .get(iteration.getParent().getId());
            MenuDataNode projectNode = nodes.get(projectIndex);
            return projectNode.getChildren().get(currentIndex);
        }
        MenuDataNode projectNode = ensureNode((Project) iteration.getParent());
        MenuDataNode node = constructNode(iteration);
        iterationIds.put(iteration.getId(), projectNode.getChildren().size());
        projectNode.getChildren().add(node);
        return node;
    }

    public void insert(Iteration iteration) {
        ensureNode(iteration);
    }

    public void insert(Project project) {
        ensureNode(project);
    }

    public List<MenuDataNode> getNodes() {
        return nodes;
    }

}
