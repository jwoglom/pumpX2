#!/bin/bash

cat "$1" | jq -c -s '.[] | select(.parsed.name) | [.raw.ts, .raw.type, .parsed.messageProps.characteristic, .parsed.name, .raw.value, .parsed.params]'
