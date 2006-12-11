#! /bin/sh

loc() {
	echo -n "$1: "
	shift
	cat `find "$@"` | wc -l
}

loc "Java code total" src -name '*.java'
loc "Database code" src/fi/hut/soberit/agilefant/db -name '*.java'
loc "Data model code" src/fi/hut/soberit/agilefant/model -name '*.java'
loc "Security code" src/fi/hut/soberit/agilefant/security -name '*.java'
loc "Service code" src/fi/hut/soberit/agilefant/service -name '*.java'
loc "Utility code" src/fi/hut/soberit/agilefant/util -name '*.java'
loc "Web code" src/fi/hut/soberit/agilefant/web -name '*.java'
loc "Java tests" test -name '*.java'

echo

loc "Build code" build.*
loc "Configuration code" conf -type f \! -path '*/.svn*'
loc "JavaScript code" web/static -name '*.js'
loc "Dynamic web content total" web/WEB-INF -type f \! -path '*/.svn*'
loc "JavaServer Pages code" web/WEB-INF -name '*.jsp'
loc "JSP tag code" web/WEB-INF -name '*.tag'
loc "JSP TagLib code" web/WEB-INF -name '*.tld'
