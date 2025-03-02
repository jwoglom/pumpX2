#!/bin/bash

cat "$1" | jq -c -s '
  .[] |
  if .error.message? then
    [.raw.ts, .raw.type, .raw.value, .error.message]
  elif .parsed.name then
    [.raw.ts, .raw.type, .parsed.messageProps.characteristic, .parsed.name, .raw.value, .parsed.params]
  else
    empty
  end
'