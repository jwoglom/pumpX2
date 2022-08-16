#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile

fpath = sys.argv[1]
reader = csv.reader(open(fpath, 'r', encoding='unicode_escape'))
headers = next(reader)

def parseBulk(path):
    cwd = os.path.join(os.path.dirname(os.path.realpath(__file__)), '../')
    try:
        return subprocess.check_output(['./gradlew', 'cliparser', '-q', f'--args=bulkparse {path}'], cwd=cwd).decode().strip()
    except Exception:
        return 'ERROR'

packets = []

currentRead = []
currentWrite = []

for rline in reader:
    line = {headers[i]: rline[i] for i in range(len(headers))}
    type = ''
    if line['Info'].startswith('Rcvd'):
        type = 'READ'
    elif line['Info'].startswith('Sent Write'):
        type = 'WRITE'

    value = line['Value']
    
    #print(f"{type}\t{value}\t", end='\n')
    if value.endswith("¦"):
        if type == 'READ':
            currentRead.append(value.split("â")[0])
        elif type == 'WRITE':
            currentWrite.append(value.split("â")[0])
    else:
        if type == 'READ':
            packets.append(['READ', [value] + currentRead])
            currentRead = []
        elif type == 'WRITE':
            packets.append(['WRITE', [value] + currentWrite])
            currentWrite = []

if currentRead:
    packets.append(['READ', currentRead])
if currentWrite:
    packets.append(['WRITE', currentWrite])



f = tempfile.NamedTemporaryFile(delete=False)
for packet in packets:
    type, group = packet
    f.write(("".join(group) + "\n").encode())
    # print(f'{type}\t{group}\t', end='')
    #print(parse(type, ))
    # sys.stdout.flush()
f.close()
print(f.name, file=sys.stderr)
print(parseBulk(f.name))

