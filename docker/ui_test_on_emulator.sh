#!/usr/bin/env bash

set -eu

wait_for_emulator
echo "Successfully finish wait for emulator"
unlock_emulator_screen
echo "Successfully finish unlock emulator screen"

# emulator isn't ready yet, wait 1 min more
# prevents APK installation error
sleep 60

echo "Successfully start emulator"
# run Android UI tests
#./gradlew connectedAndroidTest

kill_running_emulators

echo "Successfully kill emulator"