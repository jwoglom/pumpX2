#!/bin/bash

log=$1
if [[ "$log" == *.log ]]; then
  csv=${1/.log/.csv}
  tsv=${1/.log/.tsv}
  stderr=${1/.log/.stderr}
  keyfile=${1/.log/.authkey}
elif [[ "$log" == *.btsnoop ]]; then
  csv=${1/.btsnoop/.csv}
  tsv=${1/.btsnoop/.tsv}
  stderr=${1/.btsnoop/.stderr}
  keyfile=${1/.btsnoop/.authkey}
else
  echo "File provided did not end in .log or .btsnoop"
  exit 1
fi

if [[ "$PUMP_AUTHENTICATION_KEY" == "" ]]; then
  echo "============================================">&2
  echo "WARNING: PUMP_AUTHENTICATION_KEY is not set.">&2
  echo "Encrypted messages will not be decoded.">&2
  echo "============================================">&2
fi
echo $PUMP_AUTHENTICATION_KEY > $keyfile

repoRoot=$(git rev-parse --show-toplevel)

if [[ "$log" == "" ]]; then
    echo "Run with the path to a btsnoop-hci.log file"
    exit 1
fi

tshark -r $log -T fields -E separator=, -E quote=d -e frame.number -e btatt.opcode -e btatt.value 'btatt.value' > $csv
$repoRoot/scripts/get-btsnoop-opcodes.py $csv > $tsv 2> $stderr
