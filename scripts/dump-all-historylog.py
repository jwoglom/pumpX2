#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile
import json

def parseBulk(path):
    cwd = os.path.join(os.path.dirname(os.path.realpath(__file__)), '../')
    try:
        return subprocess.check_output(['./gradlew', 'cliparser', '-q', f'--args=bulkhistorylog {path}'], cwd=cwd).decode().strip()
    except Exception:
        return 'ERROR'


def parseJson(jsonfile):
    j = json.loads(open(jsonfile, "r").read())

    for opcodeGroup in j:
        messages = opcodeGroup["messages"]
        f = tempfile.NamedTemporaryFile(delete=False)
        for m in messages:
            f.write(b'' + m.encode() + b'\n')
        f.close()

        print(parseBulk(f.name))


parseJson(sys.argv[1])