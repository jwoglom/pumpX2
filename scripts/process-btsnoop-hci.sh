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

if [[ -f "$keyfile" ]]; then
  echo "Using existent keyfile $keyfile: $(cat $keyfile)"
elif [[ "$PUMP_AUTHENTICATION_KEY" == "" ]]; then
  echo "============================================">&2
  echo "WARNING: PUMP_AUTHENTICATION_KEY is not set.">&2
  echo "Encrypted messages will not be decoded.">&2
  echo "============================================">&2
else
  echo $PUMP_AUTHENTICATION_KEY > $keyfile
fi

repoRoot=$(git rev-parse --show-toplevel 2>/dev/null || echo $(dirname "$0")/../)
echo repoRoot=$repoRoot
if [[ "$log" == "" ]]; then
    echo "Run with the path to a btsnoop-hci.log file"
    exit 1
fi

tshark -r $log -T fields -E separator=, -E quote=d -e frame.number -e btatt.opcode -e btatt.value -e btatt.uuid128 -e frame.time_epoch 'btatt.value' > $csv
$repoRoot/scripts/wireshark-csv-to-jsonl.py $csv > $jsonl 2> $stderr
