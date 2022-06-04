#!/usr/bin/env python3

import jinja2
from pprint import pprint
import json
import sys
import re

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

MESSAGES_ENUM = "app/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java"

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

def addToMessagesEnum(ctx):
  text = open(MESSAGES_ENUM, "r").read()
  before, after = text.split("// MESSAGES_END", 1)

  def camel_to_snake(name):
    name = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', name).upper()


  base_name = ctx["requestName"].split("Request")[0]
  snake_name = camel_to_snake(base_name)
  requestName = ctx["requestName"]
  responseName = ctx["responseName"]

  importBefore, importAfter = before.split("// IMPORT_END")

  before = importBefore + f"import com.jwoglom.pumpx2.pump.messages.request.{requestName};\nimport com.jwoglom.pumpx2.pump.messages.response.{responseName};\n// IMPORT_END" + importAfter

  full = before + f"{snake_name}({requestName}.class, {responseName}.class),\n    // MESSAGES_END" + after

  with open(MESSAGES_ENUM, "w") as f:
    f.write(full)


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
  
  addToMessagesEnum(ctx)

if __name__ == '__main__':
  

  main()