#!/bin/bash

DIR=$1


REQUEST=$(grep -lr 'compiled from: Request.kt' $DIR/java_src/|xargs basename|sed 's/\.java//g')
RESPONSE=$(grep -lr 'compiled from: Response.kt' $DIR/java_src/|xargs basename|sed 's/\.java//g')

echo "Request: $REQUEST"
echo "Response: $RESPONSE"


grep -lr "implements $REQUEST" $DIR/java_src/|xargs basename|sed 's/\.java//g'|sort > $DIR/request-messages.txt
grep -lr "implements $RESPONSE" $DIR/java_src/|xargs basename|sed 's/\.java//g'|sort > $DIR/response-messages.txt

ls -lah $DIR/request-messages.txt
ls -lah $DIR/response-messages.txt