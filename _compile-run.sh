#!/bin/sh

# ---
# SBT
# ---

echo ""
echo "Don't use this script right now, use the 'assemble/run' script."
echo ""
exit 1

sbt compile

if [ $? != 0 ]
then
  echo "Compile failed, exiting"
  exit 1
fi

# ---
# ANT
# ---

cd deploy
ant

if [ $? != 0 ]
then
  echo "Ant failed, exiting"
  exit 2
fi

sleep 1

# ---
# RUN
# ---

echo "About to open 'release/Sarah.app' ..."
open release/Sarah.app


