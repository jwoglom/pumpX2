#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile
import arrow

def parse_ts(ts):
    if not ts:
        return ''
    return '#'+str(arrow.get(float(ts)))

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

lastSeqNum = {'READ': None, 'WRITE': None}

for rline in reader:
    type = ''

    if len(rline) == 3:
        btId, btOp, value = rline
        ts = ''
    elif len(rline) == 4:
        btId, btOp, value, ts = rline

    if btOp == '0x1b':
        type = 'READ'
    elif btOp == '0x52':
        type = 'WRITE'
    
    if not type:
        continue

    value = value.split("â")[0]

    remainingPackets = int(value[0:2], 16)
    seqNum = int(value[2:4], 16)
    if seqNum == 0:
        # SKIP stream messages
        continue

    if lastSeqNum[type] is None or lastSeqNum[type] == seqNum:
        if type == 'READ':
            currentRead.append(value)
        elif type == 'WRITE':
            currentWrite.append(value)
    else:
        if type == 'READ':
            packets.append(['READ', currentRead, parse_ts(ts)])
            currentRead = [value]
        elif type == 'WRITE':
            packets.append(['WRITE', currentWrite, parse_ts(ts)])
            currentWrite = [value]
    lastSeqNum[type] = seqNum

if currentRead:
    packets.append(['READ', currentRead, parse_ts(ts)])
if currentWrite:
    packets.append(['WRITE', currentWrite, parse_ts(ts)])



f = tempfile.NamedTemporaryFile(delete=False)
print(f.name, file=sys.stderr)
for packet in packets:
    type, group, ts = packet
    f.write(("".join(group) + ts+"\n").encode())
    # print(f'{type}\t{group}\t', end='')
    #print(parse(type, ))
    # sys.stdout.flush()
f.close()
print(f.name, file=sys.stderr)
print(parseBulk(f.name))

