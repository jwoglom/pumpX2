#!/usr/bin/env python3
import sys
import csv
import subprocess

fpath = sys.argv[1]
reader = csv.reader(open(fpath, 'r', encoding='unicode_escape'))
headers = next(reader)

def parse(type, value):
    try:
        return subprocess.check_output(['./gradlew', 'cliparser', '-q', f'--args={type} {value}'], cwd='../').decode()
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
    #print(parse(type, line['Value']).decode())

for packet in packets:
    type, group = packet
    print(f'{type}\t{group}\t', end='')
    print(parse(type, "".join(group)))
    sys.stdout.flush()

