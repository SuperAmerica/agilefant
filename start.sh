#!/bin/bash
/Users/braden/Downloads/tomcat/bin/catalina.sh stop
ant webapp
ant deploy-local
/Users/braden/Downloads/tomcat/bin/catalina.sh jpda start
