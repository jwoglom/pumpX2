import re
import os
import sys
import tempfile
import shutil

DIR = sys.argv[1]

def convert_message_props(java_file):
    """Converts @MessageProps annotations to Javadoc comments."""

    with open(java_file, 'r+') as f:
        content = f.read()

        pattern = r"^(\s*)@MessageProps\(\s*([^\)]+)\s*\)\s*$"
        javadoc_pattern = r"/\*\*[\s\S]*?\*/\n"

        def convert_value(value):
            if value.endswith(".class"):
                clsname = value[:-6]
                fp = find_file_path(clsname)
                if fp:
                    return "{@link %s}" % fp
            elif "." in value:
                fp = find_file_path(value[:value.rindex(".")])
                if fp:
                    return "{@link %s#%s}" % (fp, value[1+value.rindex("."):])
            return value

        def replacement(match):
            indentation = match.group(1)
            props_string = match.group(2)
            props = {}

            if "\n" not in indentation:
                indentation = "\n" + indentation

            param_pattern = r"(\w+)\s*=\s*([^,]+)(?:,|$)"
            param_matches = re.findall(param_pattern, props_string)

            for param_name, param_value in param_matches:
                props[param_name] = convert_value(param_value.strip())

            javadoc_match = re.search(javadoc_pattern, content, re.MULTILINE)
            if javadoc_match and javadoc_match.span(0) < match.span(0):
                javadoc = javadoc_match.group(0).replace('*/', '*')
                javadoc = terminate_sentence(javadoc)
                javadoc += f"{indentation} *"
                javadoc += f"{indentation} * <br>"
                javadoc += f"{indentation} *"
            else:
                javadoc = f"{indentation}/**"

            # Javadoc takes the first sentence (up to a period followed by whitespace) as the
            # summary shown in package/class index tables. The summary lines below therefore
            # end with a period; otherwise the whole properties table is pulled into the
            # index cell with its HTML tags stripped.
            if 'MessageType#REQUEST' in props.get('type'):
                javadoc += f"{indentation} * Request message for {props.get('response')}, opCode {props.get('opCode')}, size {props.get('size')}."
                javadoc += f"{indentation} *"
                javadoc += f"{indentation} * <br>"
                javadoc += f"{indentation} *"
            elif 'MessageType#RESPONSE' in props.get('type'):
                javadoc += f"{indentation} * Response message for {props.get('request')}, opCode {props.get('opCode')}, size {props.get('size')}."
                javadoc += f"{indentation} *"
                javadoc += f"{indentation} * <br>"
                javadoc += f"{indentation} *"
            javadoc += f"{indentation} * "
            javadoc += f"{indentation} * <table border='1'>"
            javadoc += f"{indentation} * <caption>Message properties:</caption>"
            for name, value in props.items():
                javadoc += f"{indentation} * <tr><th>&nbsp;<code>{name}</code>&nbsp;</th><td>&nbsp;<code>{value}</code>&nbsp;</td></tr>"
            javadoc += f"{indentation} * </table>"
            javadoc += f"{indentation} */"
            return javadoc + match.group(0)

        new_content = re.sub(pattern, replacement, content, flags=re.MULTILINE)

        with tempfile.NamedTemporaryFile(mode='w', delete=False) as tmp_file:
            tmp_file.write(new_content)
            shutil.move(tmp_file.name, java_file)
        return new_content != content

def terminate_sentence(javadoc):
    """Ensures the text of an existing Javadoc comment ends with sentence punctuation.

    Javadoc ends the summary sentence at the first period followed by whitespace. A
    hand-written comment such as "See {@link Foo}" with no period would otherwise run
    straight into the generated table, and the table would appear in the index cells.
    Comments that contain block tags (@see, @param, ...) are left alone.
    """
    if re.search(r"^\s*\*\s*@\w+", javadoc, re.MULTILINE):
        return javadoc
    m = re.search(r"([^\s*/])[\s*/]*$", javadoc)
    if m and m.group(1) not in ".!?:;>":
        return javadoc[:m.end(1)] + "." + javadoc[m.end(1):]
    return javadoc

def process_directory(directory):
    """Recursively processes Java files in a directory."""
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                if convert_message_props(file_path):
                    print("Processed", file_path)

def find_file_path(clsname):
    clsname = clsname.replace(".", "/")
    clsname += ".java"
    for root, _, files in os.walk(DIR):
        for file in files:
            file_path = os.path.join(root, file)
            if file_path.endswith(clsname) and "com/jwoglom/" in file_path:
                f = file_path.split("com/jwoglom/")
                return "com.jwoglom." + (f[1].replace("/", ".")[:-5])
    return None


if __name__ == '__main__':
    process_directory(DIR)