#!/bin/bash
exec $(git rev-parse --show-toplevel)/gradlew cliparser -q --args="encode $*"