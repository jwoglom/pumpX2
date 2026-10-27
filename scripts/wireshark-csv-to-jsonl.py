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

# Packets are grouped per (type, characteristic), since multi-packet messages on
# different characteristics interleave. Each group keeps the btChar and ts of its
# own first packet.
groups = {}
lastSeqNum = {}

for rline in reader:
    btId = ''
    btOp = ''
    value = ''
    btChar = ''
    ts = ''
    type = ''

    if len(rline) == 3:
        btId, btOp, value = rline
        ts = ''
    elif len(rline) == 4:
        btId, btOp, value, ts = rline
        btChar = ''
    elif len(rline) == 5:
        btId, btOp, value, btChar, ts = rline

    if btOp.lower() == '0x1b':
        type = 'READ'
    elif btOp.lower() == '0x52': # android btsnoop
        type = 'WRITE'
    elif btOp.lower() == '0x12': # iOS packetlogger
        type = 'WRITE'
    
    
    if not type:
        print('unknown btOp, skipping', btOp, file=sys.stderr)
        continue

    value = value.split("â")[0]

    if len(value) <= 4:
        print('btVal less than 4, skipping', value, file=sys.stderr)
        continue

    seqNum = int(value[2:4], 16)
    key = (type, btChar)

    isCombined = key in groups and lastSeqNum[key] == seqNum

    # split combined historylog packets
    if value[:6] == '000081':
        isCombined = False

    # qualifying events are bare 4-byte bitmasks, not packetized messages
    if btChar.lower().replace('-', '').startswith('7b83fff7'):
        isCombined = False

    if isCombined:
        groups[key][1].append(value)
    else:
        if key in groups:
            packets.append(groups[key])
        groups[key] = [type, [value], btChar, parse_ts(ts), int(btId) if btId.isdigit() else 0]
    lastSeqNum[key] = seqNum

packets.extend(groups.values())
packets.sort(key=lambda p: p[4])



f = tempfile.NamedTemporaryFile(delete=False)
for packet in packets:
    type, group, btChar, ts, _frame = packet
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

