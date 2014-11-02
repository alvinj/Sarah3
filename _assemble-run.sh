#!/bin/sh

rm sarah3.log 2> /dev/null

# ------------
# SBT Assembly
# ------------

sbt assembly

if [ $? != 0 ]
then
  echo "Compile/assemble failed, exiting"
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

#--------------------------------------------------
# Manually Adjust Info.plist File (Add Hi-Res Key)
#--------------------------------------------------

cd ..
sh _addHiResKeyToPlistFile.sh

# ---
# RUN
# ---

open deploy/release/Sarah.app

#echo ""
#echo "If this fails with this error: 'LSOpenURLsWithRole() failed with error -10810'"
#echo "you need to configure your Accessibility options in System Preferences >"
#echo "Security & Privacy > Privacy Tab > Accessibility"
#echo ""





