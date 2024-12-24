#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile
import arrow
import json

def parse_ts(ts):
    if not ts:
        return ''
    return str(arrow.get(float(ts)))

fpath = sys.argv[1]
reader = csv.reader(open(fpath, 'r', encoding='unicode_escape'))

def parseJsonLines(path):
    cwd = os.path.join(os.path.dirname(os.path.realpath(__file__)), '../')
    try:
        return subprocess.check_output(['./gradlew', 'cliparser', '-q', f'--args=parseJsonLines {path}'], cwd=cwd).decode().strip()
    except Exception as e:
        return json.dumps({"error": str(e)})

packets = []

currentRead = []
currentWrite = []

lastSeqNum = {'READ': None, 'WRITE': None}

for rline in reader:
    type = ''

    btChar = ''
    if len(rline) == 3:
        btId, btOp, value = rline
        ts = ''
    elif len(rline) == 4:
        btId, btOp, value, ts = rline
    elif len(rline) == 5:
        btId, btOp, value, btChar, ts = rline

    if btOp == '0x1b':
        type = 'READ'
    elif btOp == '0x52': # android btsnoop
        type = 'WRITE'
    elif btOp == '0x12': # iOS packetlogger
        type = 'WRITE'
    
    
    if not type:
        print('unknown btOp, skipping', btOp, file=sys.stderr)
        continue

    value = value.split("â")[0]

    remainingPackets = int(value[0:2], 16)
    seqNum = int(value[2:4], 16)
    #if seqNum == 0:
    #    # SKIP stream messages
    #    continue

    if lastSeqNum[type] is None or lastSeqNum[type] == seqNum:
        if type == 'READ':
            currentRead.append(value)
        elif type == 'WRITE':
            currentWrite.append(value)
    else:
        if type == 'READ':
            packets.append(['READ', currentRead, btChar, parse_ts(ts)])
            currentRead = [value]
        elif type == 'WRITE':
            packets.append(['WRITE', currentWrite, btChar, parse_ts(ts)])
            currentWrite = [value]
    lastSeqNum[type] = seqNum

if currentRead:
    packets.append(['READ', currentRead, btChar, parse_ts(ts)])
if currentWrite:
    packets.append(['WRITE', currentWrite, btChar, parse_ts(ts)])



f = tempfile.NamedTemporaryFile(delete=False)
for packet in packets:
    type, group, btChar, ts = packet
    f.write((json.dumps({
        "type": type,
        "btChar": btChar,
        "value": "".join(group),
        "ts": ts
    })+"\n").encode())
    f.flush()
f.close()
print(f.name, file=sys.stderr)
print(parseJsonLines(f.name))

