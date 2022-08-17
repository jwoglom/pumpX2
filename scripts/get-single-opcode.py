#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile

def parse(op, param='parse'):
    cwd = os.path.join(os.path.dirname(os.path.realpath(__file__)), '../')
    try:
        return subprocess.check_output(['./gradlew', 'cliparser', '-q', f'--args={param} {op}'], cwd=cwd).decode().strip()
    except Exception:
        return 'ERROR'

if len(sys.argv) > 2:
    print(parse(sys.argv[1], sys.argv[2]))
else:
    print(parse(sys.argv[1]))