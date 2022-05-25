#!/usr/bin/env python3

import jinja2
from pprint import pprint
import json
import sys

MAIN_TEMPLATES = {
  "app/src/main/java/com/jwoglom/pumpx2/pump/messages/request/template.j2": \
    "app/src/main/java/com/jwoglom/pumpx2/pump/messages/request/{requestName}.java",

  "app/src/main/java/com/jwoglom/pumpx2/pump/messages/response/template.j2": \
    "app/src/main/java/com/jwoglom/pumpx2/pump/messages/response/{responseName}.java",
}

TEST_TEMPLATES = {
  "app/src/test/java/com/jwoglom/pumpx2/pump/messages/request/template.j2": \
    "app/src/test/java/com/jwoglom/pumpx2/pump/messages/request/{requestName}Test.java",

  "app/src/test/java/com/jwoglom/pumpx2/pump/messages/response/template.j2": \
    "app/src/test/java/com/jwoglom/pumpx2/pump/messages/response/{responseName}Test.java"
}

TEMPLATES = {
  **MAIN_TEMPLATES,
  **TEST_TEMPLATES
}

class Arg:
  type = "int"
  name = "test"
  index = 0
  size = 2


class Context:
  opcode = 0
  size = 0
  requestName = "Request"
  responseName = "Response"
  args = [Arg()]


def render(file, ctx):
  return jinja2.Environment(loader=jinja2.BaseLoader()).from_string(open(file, 'r').read()).render(**ctx)


def add_args(ctx, opt):
  ctx[opt + "Args"] = []
  ctx[opt + "Size"] = 0
  while input(f"Add {opt} argument? (y/n)") in ('y', 'Y'):
    arg = {}
    arg["name"] = input('Arg name: ')
    typ = input('Type or size? ').lower()
    if typ == 'short' or typ == 'int':
      arg["size"] = 2
      arg["type"] = 'int'
    elif typ == 'uint32' or typ == 'long':
      arg["type"] = 'long'
      arg["size"] = 4
    elif typ == 'uint64' or typ == 'biginteger':
      arg["size"] = 8
      arg["type"] = 'BigInteger'
    else:
      arg["size"] = int(typ)
      arg["type"] = 'int'
    arg["index"] = int(input('Index into sequence: '))
    ctx[opt + "Args"].append(arg)
    ctx[opt + "Size"] += arg["size"]
  
  return ctx

def build_ctx():
  j = input('Enter JSON? ')
  if j and len(j) > 4:
    return json.loads(j)

  ctx = {}
  ctx["requestName"] = input('Request name: ')
  ctx["requestOpcode"] = int(input('Request opcode: '))
  ctx = add_args(ctx, "request")

  ctx["responseName"] = input('Response name: ')
  ctx["responseOpcode"] = int(input('Response opcode: '))
  ctx = add_args(ctx, "response")  

  print(json.dumps(ctx))
  
  return ctx


def main():
  templates = TEMPLATES
  if "--only-tests" in sys.argv:
    print("Only rendering tests")
    templates = TEST_TEMPLATES

  ctx = build_ctx()

  for tpl, out in templates.items():
    f = out.format(**ctx)
    open(f, "w").write(render(tpl, ctx))
    print(f"Wrote {f}")

if __name__ == '__main__':
  

  main()