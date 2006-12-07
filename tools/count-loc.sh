#! /bin/sh

echo Java code:
cat `find src test -name '*.java'` | wc

echo Configuration code:
cat `find build.* conf -type f` | grep -v '\.svn' | wc
