#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile
import datetime
import json

def parse_ts(ts):
    if not ts:
        return ''
    return str(datetime.datetime.fromtimestamp(float(ts)))

fpath = sys.argv[1]
reader = csv.reader(open(fpath, 'r', encoding='unicode_escape'))
headers = next(reader)

'''
to parse single json line:
$ ./gradlew cliparser -q --console=plain --args='json' < <(echo '{"type": "ReadResp", "btChar": "", "value": "", "ts": ""}') 2>/dev/null|jq
'''
def parseJsonLines(path):
    cwd = os.path.join(os.path.dirname(os.path.realpath(__file__)), '../')
    try:
        return subprocess.check_output(['./gradlew', 'cliparser', '-q', '--console=plain', f'--args=jsonlines {path}'], cwd=cwd).decode().strip()
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

    if len(value) <= 4:
        print('btVal less than 4, skipping', value, file=sys.stderr)
        continue

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
    prettyMap = {'READ':'ReadResp', 'WRITE':'WriteReq'}
    f.write((json.dumps({
        "type": prettyMap.get(type, type),
        "btChar": btChar,
        "value": "".join(group),
        "ts": ts
    })+"\n").encode())
    sys.stderr.flush()
    f.flush()
f.close()
print(f.name, file=sys.stderr)
print(parseJsonLines(f.name))

