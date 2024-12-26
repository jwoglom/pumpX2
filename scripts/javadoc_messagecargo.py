#!/usr/bin/env python3

import re
import os
import sys
import tempfile
import shutil
import html

# Enable or disable debug prints here:
DEBUG = True

def debug_print(*args):
    if DEBUG:
        print(*args)

DIR = sys.argv[1]

BYTES_METHOD_SIZES = {
    'toFloat': 4,
    'toUint32': 4,
    'toUint64': 8,
    'firstTwoBytesLittleEndian': 2,
    'firstByteLittleEndian': 1,
}

def add_buildcargo_javadoc(java_file):
    """
    Finds 'public static byte[] buildCargo(...)' in the given Java file,
    locates the 'return Bytes.combine(...)' call, does bracket-based scanning
    to find each argument, and builds/merges a Javadoc table showing the
    byte layout. This version includes the actual argument text (e.g.
    'toUint32(totalVolume) [byte 0]' instead of 'toUint32(...) [byte 0]').
    """

    classname = java_file[1 + java_file.rindex('/'):java_file.rindex('.')]

    with open(java_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # Regex that captures:
    #  - optional existing Javadoc (maybe_javadoc)
    #  - indentation (indent)
    #  - the method signature for public static byte[] buildCargo(...)
    #  - the method body
    #  - the closing brace
    buildcargo_pattern = re.compile(
        r'(?P<maybe_javadoc>(?:^[ \t]*/\*\*[\s\S]*?\*/\s*)?)'  # optional existing Javadoc
        r'(?P<indent>^[ \t]*)'
        r'(?P<signature>public\s+static\s+byte\[\]\s+buildCargo\s*\([^)]*\)\s*\{\s*)'
        r'(?P<body>[\s\S]*?)'
        r'(?P<closing>^[ \t]*\})',
        re.MULTILINE
    )

    def strip_comments(text):
        """
        Remove single-line (// ...) and block (/* ... */) comments from 'text'.
        """
        # Remove block comments (/* ... */), multiline allowed
        text = re.sub(r'/\*.*?\*/', '', text, flags=re.DOTALL)
        # Remove single-line comments (// ...)
        text = re.sub(r'//[^\r\n]*', '', text)
        return text

    def split_args(args_string):
        """
        Splits top-level comma-separated arguments of Bytes.combine(...).
        We'll track parentheses, curly braces, and square brackets separately.
        Only when all are zero do we treat a comma as a delimiter.
        """
        parts = []
        parens = 0
        braces = 0
        brackets = 0
        current = []

        for c in args_string:
            if c == '(':
                parens += 1
                current.append(c)
            elif c == ')':
                parens -= 1
                current.append(c)
            elif c == '{':
                braces += 1
                current.append(c)
            elif c == '}':
                braces -= 1
                current.append(c)
            elif c == '[':
                brackets += 1
                current.append(c)
            elif c == ']':
                brackets -= 1
                current.append(c)
            elif c == ',' and parens == 0 and braces == 0 and brackets == 0:
                parts.append(''.join(current).strip())
                current = []
            else:
                current.append(c)

        if current:
            parts.append(''.join(current).strip())

        return parts

    def parse_bytes_call(arg_str):
        """
        If this is something like 'Bytes.toUint32(totalVolume)', return
        (methodName, argumentText), e.g. ('toUint32', 'totalVolume').
        If not recognized, return (None, None).
        """
        # Try matching e.g. "Bytes.toUint32(anythingInside)"
        # We'll allow multiple arguments inside if present (like writeString).
        match = re.match(r'Bytes\.(\w+)\s*\((.*)\)', arg_str)
        if not match:
            return (None, None)
        method = match.group(1)
        inside = match.group(2).strip()
        return (method, inside)

    def bytes_for_argument(arg_str, start_offset):
        """
        Return a list of (offset, description) for each contributed byte
        in one argument expression. For example:
          'Bytes.toUint32(totalVolume)' => 4 rows:
            (0, "toUint32(totalVolume) [byte 0]")
            (1, "toUint32(totalVolume) [byte 1]")
            ...
        """
        arg_str = arg_str.strip()

        # A) new byte[]{...}
        if re.match(r'^new\s+byte\[\]\s*\{', arg_str):
            inside = re.search(r'new\s+byte\[\]\s*\{([^}]*)\}', arg_str)
            if inside:
                elems = [x.strip() for x in inside.group(1).split(',') if x.strip()]
                lines = []
                for i, _ in enumerate(elems):
                    desc = f"new byte[]{{...}} [element {i}]"
                    lines.append((start_offset + i, desc))
                return lines
            return []

        # B) Bytes.writeString(...)
        #   We'll show full text, e.g. "writeString(something, 5) [byte 0]"
        #   Then detect it returns 0..N bytes:
        wmatch = re.match(r'Bytes\.writeString\s*\(\s*(.+)\)', arg_str)
        if wmatch:
            # The inside might be "someStr, 5" or "someStr ,5"
            # we want the second argument as an int
            inside = wmatch.group(1).strip()
            # We'll do a quick parse: split by comma at top level
            # (assuming there is exactly one comma)
            subparts = split_args(inside)
            # e.g. subparts[0] = "someStr"
            #      subparts[1] = "5"
            if len(subparts) == 2:
                try:
                    length = int(subparts[1].strip())
                except:
                    length = 0
                desc_base = f"writeString({inside})"
                lines = []
                for i in range(length):
                    lines.append((start_offset + i, f"{desc_base} [byte {i}]"))
                return lines
            # fallback
            return []

        # C) Known fixed-size calls, e.g. Bytes.toUint32(...)
        method, inside = parse_bytes_call(arg_str)
        if method and method in BYTES_METHOD_SIZES:
            size = BYTES_METHOD_SIZES[method]
            desc_base = f"{method}({inside})"
            lines = []
            for i in range(size):
                lines.append((start_offset + i, f"{desc_base} [byte {i}]"))
            return lines

        # D) If none of the above recognized patterns => 0 bytes
        return []

    def find_bytes_combine_args(body):
        """
        Looks in 'body' for 'return Bytes.combine('.
        Then bracket-scans to find the matching ')', returning the raw substring.
        """
        pattern = r'return\s+Bytes\.combine\s*\('
        match = re.search(pattern, body)
        if not match:
            debug_print("=== DEBUG: 'return Bytes.combine(' not found in body ===")
            return None

        start_idx = match.end()  # position right after '('
        paren_count = 1  # we found the first '('
        i = start_idx
        while i < len(body) and paren_count > 0:
            c = body[i]
            if c == '(':
                paren_count += 1
            elif c == ')':
                paren_count -= 1
            i += 1

        if paren_count != 0:
            debug_print("=== DEBUG: Did not find matching ')' for Bytes.combine(... ===")
            return None

        # substring is from start_idx up to i-1
        return body[start_idx : i-1].strip()

    def build_table(indent, body):
        args_substring = find_bytes_combine_args(body)
        if not args_substring:
            return None

        # remove comments from the substring
        cleaned_args = strip_comments(args_substring)
        # then split into top-level arguments
        args_list = split_args(cleaned_args)

        offset = 0
        all_rows = []
        for arg in args_list:
            chunk = bytes_for_argument(arg, offset)
            all_rows.extend(chunk)
            if chunk:
                offset = chunk[-1][0] + 1

        if not all_rows:
            return None

        lines = []
        lines.append(f"{indent} * <table border=\"1\">")
        lines.append(f"{indent} * <caption>Byte layout for {classname}.buildCargo(...):</caption>")
        lines.append(f"{indent} * <tr><th>Offset</th><th>Description</th></tr>")
        for off, desc in all_rows:
            lines.append(f"{indent} * <tr><td>{off}</td><td><code>{html.escape(desc)}</code></td></tr>")
        lines.append(f"{indent} * </table>")
        return "\n".join(lines)

    def build_javadoc(indent, old_javadoc, body):
        new_table = build_table(indent, body)
        if not new_table:
            return old_javadoc  # no changes if no arguments recognized

        if not old_javadoc.strip():
            # build a brand-new javadoc
            return (
                f"{indent}/**\n"
                f"{new_table}\n"
                f"{indent} */\n"
            )
        else:
            # merge with existing javadoc
            merged = old_javadoc.rstrip()
            merged = re.sub(r'\s*\*/\s*$', '', merged)  # remove trailing "*/"
            merged += f"\n{indent} * <br />\n"
            merged += new_table + "\n"
            merged += f"{indent} */\n"
            return merged

    def replacement(match):
        maybe_javadoc = match.group("maybe_javadoc") or ""
        indent = match.group("indent")
        signature = match.group("signature")
        body = match.group("body")
        closing = match.group("closing")

        new_javadoc = build_javadoc(indent, maybe_javadoc, body)
        if not new_javadoc:
            return match.group(0)
        return f"{new_javadoc}{indent}{signature}{body}{closing}"

    new_content = buildcargo_pattern.sub(replacement, content)
    if new_content != content:
        with tempfile.NamedTemporaryFile(mode='w', delete=False, encoding='utf-8') as tmp_file:
            tmp_file.write(new_content)
        shutil.move(tmp_file.name, java_file)
        return True
    return False

def process_directory(directory):
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                if add_buildcargo_javadoc(file_path):
                    print("Processed", file_path)

if __name__ == '__main__':
    process_directory(DIR)
