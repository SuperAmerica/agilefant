package fi.hut.soberit.agilefant.transfer;

import java.util.Set;

import flexjson.JSON;

public class AutocompleteDataNode {
    private String name = "";
    private Integer id = 0;
    private Set<Integer> idList = null;
    private String baseClassName;
    private boolean enabled = true;
    private Object originalObject;
    
    public AutocompleteDataNode(Class<?> baseClass, Integer id, String name) {
        this.baseClassName = baseClass.getCanonicalName(); 
        this.setId(id);
        this.setName(name);
    }
    
    public AutocompleteDataNode(Class<?> baseClass, Integer id, String name, Set<Integer> idList) {
        this(baseClass, id, name);
        this.setIdList(idList);
    }
    public AutocompleteDataNode(Class<?> baseClass, Integer id, String name, boolean enabled) {
        this(baseClass, id, name);
        this.enabled = enabled;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    @JSON(include=true)
    public Set<Integer> getIdList() {
        return idList;
    }
    public void setIdList(Set<Integer> idList) {
        this.idList = idList;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public void setOriginalObject(Object originalObject) {
        this.originalObject = originalObject;
    }
}
