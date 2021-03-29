#!/usr/bin/env bash

set -eu

#
# Wait for emulator
#
bootanim=""
failcounter=0
timeout_in_sec=360

until [[ "$bootanim" =~ "stopped" ]]; do
  bootanim=`adb -e shell getprop init.svc.bootanim 2>&1 &`
  if [[ "$bootanim" =~ "device not found" || "$bootanim" =~ "device offline"
    || "$bootanim" =~ "running" ]]; then
    let "failcounter += 1"
    echo "Waiting for emulator to start"
    if [[ $failcounter -gt timeout_in_sec ]]; then
      echo "Timeout ($timeout_in_sec seconds) reached; failed to start emulator"
      exit 1
    fi
  fi
  sleep 1
done

echo "Emulator is ready"

#
# Unlock emulator screen
#
${ANDROID_HOME}/platform-tools/adb shell input keyevent 82 &
${ANDROID_HOME}/platform-tools/adb shell input keyevent 4 &

echo "Successfully finish unlock emulator screen"

# emulator isn't ready yet, wait 1 min more
# prevents APK installation error
sleep 60

echo "Successfully start emulator"

# run Android UI tests
#./gradlew connectedAndroidTest

#
# Kill running emulators
#
${ANDROID_HOME}/platform-tools/adb devices | grep emulator | cut -f1 | while read line; do ${ANDROID_HOME}/platform-tools/adb -s $line emu kill; done

echo "Successfully kill emulator"