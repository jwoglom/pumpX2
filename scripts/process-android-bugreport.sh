#!/bin/bash

zip=$1
if [[ "$zip" == *.zip ]]; then
  log=${1/.zip/.log}
  keyfile=${1/.zip/.authkey}
elif [[ "$zip" == "" ]]; then
  filename=$(date +"%Y-%m-%dT%H-%M-%S%z")
  echo "Running adb bugreport $filename"
  adb bugreport $filename
  zip=${filename}.zip
else
  echo "File provided did not end in .zip -- did you run adb bugreport?"
  exit 1
fi

hcifile=FS/data/misc/bluetooth/logs/btsnoop_hci.log

unzip -l "$zip" | grep -q "$hcifile";
if [ "$?" != "0" ]; then
  echo "No btsnoop_hci.log found in the bugreport zip"
  exit 1
fi

unzip -p "$1" "$hcifile" > $log

if [[ -f "$keyfile" ]]; then
  echo "Using existent keyfile $keyfile: $(cat $keyfile)"
  export PUMP_AUTHENTICATION_KEY=$(cat $keyfile)
fi

if [[ "$PUMP_AUTHENTICATION_KEY" == "" ]]; then
  read -p "Enter the 6-digit pump pairing code: " PUMP_AUTHENTICATION_KEY
fi

repoRoot=$(git rev-parse --show-toplevel 2>/dev/null || echo $(dirname "$0")/../)
echo repoRoot=$repoRoot

echo Processing btsnoop_hci.log...
PUMP_AUTHENTICATION_KEY=$PUMP_AUTHENTICATION_KEY $repoRoot/scripts/process-btsnoop-hci.sh "$log"