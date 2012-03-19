package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.List;

import flexjson.JSON;

public class MenuDataNode {
    
    private int id;
    private String title;
    private ScheduleStatus scheduleStatus = ScheduleStatus.ONGOING;
    private boolean expand = false;
    private boolean icon = false;
    private BacklogType type;
    
    private List<MenuDataNode> children = new ArrayList<MenuDataNode>();
    
    
    public void setId(int id) {
        this.id = id;
    }

    @JSON
    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JSON
    public String getTitle() {
        return title;
    }

    public void setChildren(List<MenuDataNode> children) {
        this.children = children;
    }

    @JSON
    public List<MenuDataNode> getChildren() {
        return children;
    }

    public void setIcon(boolean icon) {
        this.icon = icon;
    }

    @JSON
    public boolean isIcon() {
        return icon;
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    @JSON
    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }
    
    @JSON
    public boolean isExpand() {
        return expand;
    }
    
    public void setExpand(boolean expand) {
        this.expand = expand;
    }
    
    @JSON
    public BacklogType getAddClass()
    {
        return this.type;
    }
    
    public void setType(BacklogType type)
    {
        this.type = type;
    }
    
    
}
