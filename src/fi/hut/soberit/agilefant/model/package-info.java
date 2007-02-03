/** 
 * Package-level typedef to name our time-usertype 
 * into just time, to avoid writing the full class name all the time. */
@TypeDefs(
    {
    @TypeDef(
        name="af_time",
        typeClass = fi.hut.soberit.agilefant.db.hibernate.TimeUserType.class
    ),
    @TypeDef(
            name="truncated_string",
            typeClass = fi.hut.soberit.agilefant.db.hibernate.TruncatedStringUserType.class
        )
    }
)
package fi.hut.soberit.agilefant.model;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

