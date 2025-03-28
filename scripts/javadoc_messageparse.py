#!/usr/bin/env python3

import re
import os
import sys
import tempfile
import shutil
import html

DIR = sys.argv[1]

def add_parse_javadoc(java_file):
    """
    Finds the 'public void parse(byte[] raw)' method in the given Java file,
    looks for assignments of the form 'this.xxx = yyy;', and inserts
    a Javadoc comment above the method describing those assignments in a table.
    """

    classname = java_file[1+java_file.rindex('/'):java_file.rindex('.')]
    with open(java_file, 'r', encoding='utf-8') as f:
        content = f.read()

    # This pattern captures (in order):
    # 1) An optional existing Javadoc block above the method (group "maybe_javadoc")
    # 2) The indentation before the parse method (group "indent")
    # 3) The parse method signature including the opening brace (group "signature")
    # 4) The method body (group "body")
    # 5) The closing brace on its own line (group "closing")
    parse_method_pattern = re.compile(
        r'(?P<maybe_javadoc>(?:^[ \t]*/\*\*[\s\S]*?\*/\s*)?)'  # optional existing Javadoc
        r'(?P<indent>^[ \t]*)'                               # indentation
        r'(?P<signature>public\s+void\s+parse\s*\(\s*byte\[\]\s+raw\s*\)\s*\{\s*)'
        r'(?P<body>[\s\S]*?)'
        r'(?P<closing>^[ \t]*\})',
        re.MULTILINE
    )

    # Regex to capture lines in the body that assign something like: this.xxx = yyy;
    assignment_pattern = re.compile(
        r'^[ \t]*this\.(?P<varname>\w+)\s*=\s*(?P<value>[^;]+);',
        re.MULTILINE
    )

    def build_javadoc(indent, assignments, existing_javadoc=None):
        """
        Builds the Javadoc string, using the same indentation and
        creating a row in a table for each assignment.
        """
        # Start the comment
        if existing_javadoc:
            existing_javadoc = existing_javadoc.strip()
            existing_javadoc = re.sub(r'\s*\*\/\s*$', '', existing_javadoc)
            javadoc = existing_javadoc
            javadoc += f"{indent} *"
            javadoc += f"{indent} * <br>"
            javadoc += f"{indent} *"
        else:
            javadoc = f"{indent}/**\n"
        javadoc += f"{indent} * <table border=\"1\">\n"
        javadoc += f"{indent} * <caption>Parsed fields for {classname}:</caption>\n"
        cnt = 0
        for varname, value in assignments:
            if not varname in ('raw', 'cargo'):
                cnt += 1
                javadoc += f"{indent} * <tr><td><code>{varname}</code></td><td><code>{html.escape(value.strip())}</code></td></tr>\n"
        javadoc += f"{indent} * </table>\n"
        javadoc += f"{indent} */\n"
        if cnt == 0:
            return existing_javadoc
        return javadoc

    def replacement(match):
        """
        Called for each match of the parse method. We parse out all the
        'this.xxx = yyy;' lines, build the Javadoc, and insert it above
        the method signature.
        """
        maybe_javadoc = match.group("maybe_javadoc") or ""
        indent = match.group("indent")
        signature = match.group("signature")
        body = match.group("body")
        closing = match.group("closing")

        # Find all lines that match the assignment pattern
        assignments = assignment_pattern.findall(body)
        if not assignments:
            # If there are no assignments, just return as is
            return match.group(0)

        new_javadoc = build_javadoc(indent, assignments, maybe_javadoc.strip())

        # Reconstruct the method with the updated or appended Javadoc
        return f"{new_javadoc}{indent}{signature}{body}{closing}"

    # Perform the substitution on the content
    new_content = parse_method_pattern.sub(replacement, content)

    # If there are changes, write them back
    if new_content != content:
        with tempfile.NamedTemporaryFile(mode='w', delete=False, encoding='utf-8') as tmp_file:
            tmp_file.write(new_content)
        shutil.move(tmp_file.name, java_file)
        return True
    return False

def process_directory(directory):
    """
    Recursively processes Java files in a directory.
    """
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                if add_parse_javadoc(file_path):
                    print("Processed", file_path)

if __name__ == '__main__':
    process_directory(DIR)
