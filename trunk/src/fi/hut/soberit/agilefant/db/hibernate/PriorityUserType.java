package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.model.Priority;

public class PriorityUserType extends OrdinalEnumUserType<Priority> {

    public PriorityUserType() {
        super(Priority.class);
    }
}
