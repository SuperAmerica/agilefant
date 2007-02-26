README for AgilEfant developers
-------------------------------
This document is intended for people who are interested in developing
AgilEfant by fixing old bugs or adding new functionality. It mainly
describes where to look for first and which files to put focus on.

Before starting any development work with AgilEfant, one should familiarize
himself with Cycles Of Control concept, Hibernate, WebWork, Spring and
their configurations and with concepts like dependency injection and
transaction management. Very good knowledge of J2EE is also required for
good results.

Directory structure
---
Directory structure is self explaining enough for any experienced developer
so there's no need to go it through here.

Important configuration files
---
- Hibernate
When adding classes for Hibernate to be persisted, remember to add your class
to also to conf/hibernate.cfg.xml so that Hibernate will use it to load
mappings.

- WebWork
WebWork configuation is located in conf/classes/xwork.xml and 
conf/classes/webwork.properties. All classes used in xwork.xml are loaded
using Spring as a object factory. This means that the classes are declared 
as a non-singleton beans in Springs application context. Read WebWork
documentation for information.

System wide converters are declared in xwork-conversion.properties.

XWork annotations are used in certain classes. XWork 1.2.1 (current version when 
writing this) had all annotation classes missing @Retention annotation causing
them not to work. web/WEB-INF/classes contains fixed binaries for required
classes. This bug has been reported to OpenSymphony and will be fixed in 
XWork 1.2.1.

- Spring
Beans are initialized by reading all applicationContext*.xml files from conf. 
Suffix of the file tries to describe the area where it belongs. Feel free
to add your own files if needed to keep configuration files small enough.

Keep in mind that all WebWork classes (actions and interceptors) are
configured in applicationContext-actions.xml. To avoid unnecessary 
configuration lines byName autowire strategy is used, so keep that in mind when
declaring your own beans. Remember also that all actions MUST be
declared as singleton set to false. Forgetting this makes same
instance of class to be shared between all requests causing odd behaviour.

Where to start
---
Most of the actual business logic of AgilEfant is located in  package
fi.hut.soberit.agilefant.web. When adding new functionality, that's the 
place to start.












