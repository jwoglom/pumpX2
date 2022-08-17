#!/bin/bash

log=$1
csv=${1/.log/.csv}
tsv=${1/.log/.tsv}

repoRoot=$(git rev-parse --show-toplevel)

if [[ "$log" == "" ]]; then
    echo "Run with the path to a btsnoop-hci.log file"
    exit 1
fi

tshark -r $log -T fields -E separator=, -E quote=d -e frame.number -e btatt.opcode -e btatt.value 'btatt.value' > $csv
$repoRoot/scripts/get-btsnoop-opcodes.py $csv > $tsv