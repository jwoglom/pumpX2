#!/bin/bash
exec $(git rev-parse --show-toplevel)/gradlew cliparser -q --console=plain --warning-mode none --args="parse $*"
