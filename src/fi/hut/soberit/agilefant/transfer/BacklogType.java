package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

public enum BacklogType {
    PRODUCT, 
    PROJECT, 
    ITERATION;
    
    
    public static BacklogType forBacklog(Backlog backlog) {
        if (backlog instanceof Product) {
            return PRODUCT;
        
        } else if (backlog instanceof Project) {
            return PROJECT;
            
        } else if (backlog instanceof Iteration) {
            return ITERATION;
            
        }
        return null;
    }
    
    
}
