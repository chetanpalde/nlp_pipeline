#!/bin/sh
# list.sh - make a pretty list of the gate source, missing class files etc.
# $Id: list.sh 6815 2005-08-17 11:01:05Z hcunningham $

clear
cd ../../
find gate2 -print | \
  grep -v '^gate2/lib/' | sed 's,gate2/lib,gate2/lib/...,' | \
  grep -v '^gate2/doc/javadoc/' | \
  sed 's,gate2/doc/javadoc,gate2/doc/javadoc/...,' | \
  grep -v '^gate2/misc/' | sed 's,gate2/misc,gate2/misc/...,' | \
  grep -v '^gate2/classes/' | sed 's,gate2/classes,gate2/classes/...,' | \
  grep -v '^gate2/bin/images/' | \
  grep -v 'Default.vfPackage' | \
  grep -v 'CVS' | \
  sed 's,gate2/bin/images,gate2/bin/images/...,' | \
  sort

