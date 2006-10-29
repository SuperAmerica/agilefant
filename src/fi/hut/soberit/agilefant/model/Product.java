package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

public class Product extends Backlog{
	
    private Collection<Deliverable> deliverables = new HashSet<Deliverable>();
}
