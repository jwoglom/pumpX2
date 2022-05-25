#!/bin/bash

echo Request name:
read REQUEST_NAME

echo Response name:
read RESPONSE_NAME


TEMPLATES=(
  app/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/messages/request/template.txt
  app/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/messages/response/template.txt
)

for TEMPLATE in ${TEMPLATES[@]}; do
  python3 -c "print(__import__('string').Template(open('$TEMPLATE','r').read()).substitute(os.environ))"
done
