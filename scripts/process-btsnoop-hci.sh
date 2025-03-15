#!/bin/bash

log=$1
if [[ "$log" == *.log ]]; then
  csv=${1/.log/.csv}
  jsonl=${1/.log/.jsonl}
  stderr=${1/.log/.stderr}
  keyfile=${1/.log/.authkey}
elif [[ "$log" == *.btsnoop ]]; then
  csv=${1/.btsnoop/.csv}
  jsonl=${1/.btsnoop/.jsonl}
  stderr=${1/.btsnoop/.stderr}
  keyfile=${1/.btsnoop/.authkey}
else
  echo "File provided did not end in .log or .btsnoop"
  exit 1
fi

if [[ -f "$keyfile" && "$PUMP_AUTHENTICATION_KEY" == "" ]]; then
  echo "Using existent keyfile $keyfile: $(cat $keyfile)"
  PUMP_AUTHENTICATION_KEY=$(cat $keyfile)
elif [[ "$PUMP_AUTHENTICATION_KEY" == "" ]]; then
  echo "============================================">&2
  echo "PUMP_AUTHENTICATION_KEY will be set to default value">&2
  echo "============================================">&2
  export PUMP_AUTHENTICATION_KEY=IGNORE_HMAC_SIGNATURE_EXCEPTION
else
  echo $PUMP_AUTHENTICATION_KEY > $keyfile
fi

repoRoot=$(git rev-parse --show-toplevel 2>/dev/null || echo $(dirname "$0")/../)
echo repoRoot=$repoRoot
if [[ "$log" == "" ]]; then
    echo "Run with the path to a btsnoop-hci.log file"
    exit 1
fi

echo Exporting wireshark csv...

if ! command -v tshark 2>&1 >/dev/null; then
  echo "tshark CLI is not installed -- please install wireshark"
  exit 1
fi

tshark -r $log -T fields -E separator=, -E quote=d -e frame.number -e btatt.opcode -e btatt.value -e btatt.uuid128 -e frame.time_epoch 'btatt.value' > $csv

echo Parsing...
PUMP_AUTHENTICATION_KEY=$PUMP_AUTHENTICATION_KEY $repoRoot/scripts/wireshark-csv-to-jsonl.py $csv > $jsonl 2> $stderr
