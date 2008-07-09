/**
 * Data model package, consists of Hibernate entity beans that map the contents
 * of the database to the object oriented model.
 */
@TypeDefs(
/*
 * Package-level typedefs to avoid writing the long type definitions all the
 * time.
 */
{
        @TypeDef(name = "af_time", typeClass = fi.hut.soberit.agilefant.db.hibernate.TimeUserType.class),
        @TypeDef(name = "truncated_varchar", typeClass = fi.hut.soberit.agilefant.db.hibernate.StringTruncateFilter.class, parameters = { @Parameter(name = "subtypes", value = "fi.hut.soberit.agilefant.db.hibernate.VarcharUserType") }),
        @TypeDef(name = "escaped_varchar", typeClass = fi.hut.soberit.agilefant.db.hibernate.StringEscapeFilter.class, parameters = { @Parameter(name = "subtypes", value = "fi.hut.soberit.agilefant.db.hibernate.VarcharUserType") }),
        @TypeDef(name = "escaped_truncated_varchar", typeClass = fi.hut.soberit.agilefant.db.hibernate.StringEscapeFilter.class, parameters = { @Parameter(name = "subtypes", value = "fi.hut.soberit.agilefant.db.hibernate.StringTruncateFilter fi.hut.soberit.agilefant.db.hibernate.VarcharUserType") }),
        @TypeDef(name = "escaped_text", typeClass = fi.hut.soberit.agilefant.db.hibernate.StringEscapeFilter.class, parameters = { @Parameter(name = "subtypes", value = "fi.hut.soberit.agilefant.db.hibernate.TextUserType") }) })
package fi.hut.soberit.agilefant.model;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

