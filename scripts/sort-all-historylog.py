#!/usr/bin/env python3
import sys
import csv
import subprocess
import os, os.path
import tempfile
import json
import datetime

def parseTs(ts):
    return str(datetime.datetime.fromtimestamp(int(ts) + 1199145600))


def sortLines(lines, sort=True, oneline=False):
    parts = [p.split("\t") for p in lines]
    allfields = [part[:-1] + [{i[0]: i[1] for i in filter(None, [i.split('=') if '=' in i else None for i in part[-1].split('[')[1].split(']')[0].split(',')]) if i[0] != 'cargo'}] for part in parts if '[' in part[-1] and ']' in part[-1]]
    allfields = [[parseTs(part[-1]['pumpTimeSec'])] + part for part in allfields]

    if sort:
        allfields.sort(key=lambda x: int(x[-1]['pumpTimeSec']))
    
    if oneline:
        print('[')
        for i, l in enumerate(allfields):
            print('%s%s' % (json.dumps(l), ',' if i != len(allfields)-1 else ''))
        print(']')
    else:
        print(json.dumps(allfields, indent=4))
    


oneline = False
if len(sys.argv) > 2:
    oneline = sys.argv[2] == 'oneline'

# csv
lines = open(sys.argv[1], 'r').read().splitlines()
sortLines(lines, sort=True, oneline=oneline)