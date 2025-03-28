#!/usr/bin/env python3

import sys
import struct

def find(inp):
    inp = inp.strip()
    data = []
    # byte array
    if ',' in inp:
        chrs = inp.split(',')
        data = bytearray([int(i) if int(i) >= 0 else int(i)+256 for i in chrs])
    else:
        # char array
        data = bytes.fromhex(inp)
    
    for i in range(len(data)):
        print(f'{i=}\tord={data[i]}\t', end='')
        print(f'chr={chr(data[i])}\t', end='')

        _le_ushort = le_ushort(data[i:i+2])
        print(f'{_le_ushort=}\t', end='')

        _le_uint32 = le_uint32(data[i:i+4])
        print(f'{_le_uint32=}\t', end='')

        print()

def le_ushort(data): # input size: 4
    try:
        return struct.unpack("<H", data)
    except:
        return ''

def le_uint32(data): # input size: 4
    try:
        return struct.unpack("<I", data)
    except:
        return ''

        



if len(sys.argv) > 1:
    find(sys.argv[-1])
else:
    print('Enter input: ')
    find(input())


