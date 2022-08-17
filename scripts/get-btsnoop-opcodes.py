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

lastSeqNum = {'READ': None, 'WRITE': None}

for rline in reader:
    type = ''

    btId, btOp, value = rline

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
            packets.append(['READ', currentRead])
            currentRead = [value]
        elif type == 'WRITE':
            packets.append(['WRITE', currentWrite])
            currentWrite = [value]
    lastSeqNum[type] = seqNum

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

