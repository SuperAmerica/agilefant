package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.List;

public class AssignmentMenuNode {

    private AssignmentMenuNodeType type;
    private int id;
    private String title;
    private List<AssignmentMenuNode> children = new ArrayList<AssignmentMenuNode>();

    public AssignmentMenuNodeType getType() {
        return type;
    }

    public void setType(AssignmentMenuNodeType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AssignmentMenuNode> getChildren() {
        return children;
    }

    public void setChildren(List<AssignmentMenuNode> children) {
        this.children = children;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("AssigmentMenuNode(id=");
        result.append(id);
        result.append(", type=");
        result.append(type);
        result.append(", title='");
        result.append(title);
        result.append("', children=");
        result.append(children.toString());
        result.append(")");
        return result.toString();
    }
    

}
