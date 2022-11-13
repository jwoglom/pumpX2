#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile

def parse(op, param='historylog'):
    cwd = os.path.join(os.path.dirname(os.path.realpath(__file__)), '../')
    try:
        return subprocess.check_output(['./gradlew', 'cliparser', '-q', f'--args={param} {op}'], cwd=cwd).decode().strip()
    except Exception:
        return 'ERROR'

print(parse(sys.argv[1]))