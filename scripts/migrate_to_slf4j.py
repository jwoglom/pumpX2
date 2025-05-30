import os
import re
import sys

def get_class_name(filename):
    """Extracts the class name from a .java filename."""
    return os.path.splitext(os.path.basename(filename))[0]

def process_java_file(filepath):
    if not os.path.exists(filepath):
        print(f"Error: File not found at {filepath}")
        return
    if not filepath.endswith(".java"):
        print(f"Error: File {filepath} is not a .java file.")
        return

    class_name = get_class_name(filepath)

    try:
        with open(filepath, 'r') as f:
            original_lines = f.readlines()

        processed_lines = list(original_lines) # Work on a copy
        tag_line_successfully_replaced = False

        # Perform transformations
        for i, line_content in enumerate(processed_lines):
            current_line = line_content

            # Step 2: Replace the line with 'String TAG' on it
            if 'String TAG' in current_line and not tag_line_successfully_replaced:
                # Ensure it's a standalone declaration, not in a comment or string literal
                # This is a simple check; more robust parsing might be needed for complex cases.
                if re.match(r'\s*(public|private|protected)?\s*(static)?\s*(final)?\s*String\s+TAG\s*=.*;', current_line.strip()):
                    indentation = re.match(r"(\s*)", current_line).group(1) if re.match(r"(\s*)", current_line) else "    " # Default indent
                    processed_lines[i] = f'{indentation}private static final Logger log = LoggerFactory.getLogger({class_name}.class);\n'
                    tag_line_successfully_replaced = True
                    # Don't apply L.x to this new logger line immediately
                    continue

            # Apply other transformations
            current_line = processed_lines[i] # Re-fetch in case it was the logger line
            current_line = current_line.replace('L.d(TAG, ', 'log.debug(')
            current_line = current_line.replace('L.e(TAG, ', 'log.error("error", ') # Note: "error" is hardcoded as per original request
            current_line = current_line.replace('L.w(TAG, ', 'log.warn(')
            current_line = current_line.replace('L.i(TAG, ', 'log.info(')
            processed_lines[i] = current_line

        if not tag_line_successfully_replaced:
            # If TAG was not replaced, only L.x changes might have occurred.
            # Check if any actual changes were made before writing.
            if processed_lines != original_lines:
                with open(filepath, 'w') as f:
                    f.writelines(processed_lines)
                print(f"Processed (L.x replacements only, 'String TAG' for logger not found or not replaced): {filepath}")
            else:
                print(f"No changes made ('String TAG' for logger not found and no L.x replacements): {filepath}")
            return

        # If we reach here, tag_line_successfully_replaced is True, so we need to manage imports.

        final_output_content = []
        package_declaration_line = None
        existing_import_lines = []
        code_body_lines = [] # Lines that are not package or import

        # Separate package, imports, and body from the processed_lines
        # This parsing is basic and assumes imports are contiguous after package.
        imports_section_ended = False
        for line in processed_lines:
            stripped_line = line.strip()
            if stripped_line.startswith('package ') and package_declaration_line is None:
                package_declaration_line = line
            elif stripped_line.startswith('import ') and not imports_section_ended:
                existing_import_lines.append(line)
            else:
                if stripped_line != "" and not stripped_line.startswith('import '): #  Content after imports started
                    imports_section_ended = True
                code_body_lines.append(line)

        # Add package declaration
        if package_declaration_line:
            final_output_content.append(package_declaration_line)

        # Prepare SLF4J imports
        slf4j_imports_to_ensure = [
            'import org.slf4j.Logger;\n',
            'import org.slf4j.LoggerFactory;\n'
        ]

        # Consolidate imports: SLF4J first, then others, no duplicates.
        final_imports_list = []
        # Add SLF4J imports (they will be unique at this stage)
        for slf4j_imp in slf4j_imports_to_ensure:
            final_imports_list.append(slf4j_imp)

        # Add existing imports only if they are not the SLF4J ones we just added
        existing_import_lines_stripped = {imp.strip() for imp in existing_import_lines}
        slf4j_imports_to_ensure_stripped = {imp.strip() for imp in slf4j_imports_to_ensure}

        for existing_imp in existing_import_lines:
            if existing_imp.strip() not in slf4j_imports_to_ensure_stripped:
                if existing_imp not in final_imports_list : # Avoid duplicates if original had them
                    final_imports_list.append(existing_imp)

        # Add a blank line between package and imports if both exist
        if package_declaration_line and final_imports_list:
            if not final_output_content[-1].endswith('\n\n') and not final_output_content[-1].isspace(): # Avoid double blank
                 # Check if package line already ends with \n, if not, add it. Then add another for blank line.
                 # Standard Java formatter usually puts a blank line.
                 # Let's assume package_declaration_line includes its own \n.
                 # We need to ensure the *next item* starts after a blank line.
                 # If final_output_content has package, and last char of package is \n, add another \n for blank line.
                 # This is simpler: if last line added was package and it has content, and imports exist, add blank line.
                 pass # The structure below will handle it.

        # Add imports to final output
        if final_imports_list:
            if final_output_content and final_output_content[-1].strip() != "" and not final_output_content[-1].endswith('\n'):
                 final_output_content[-1] = final_output_content[-1].rstrip('\r\n') + '\n' # Ensure prior line ends with newline
            if final_output_content and not final_output_content[-1].isspace(): # If content before imports, add blank line
                 final_output_content.append('\n')

            for imp_line in final_imports_list:
                final_output_content.append(imp_line)

        # Add code body
        # Add a blank line between imports and code body if both exist
        if final_imports_list and code_body_lines:
            # Check if the first line of code_body_lines is effectively blank or if we need to insert one
            first_code_line_is_blank = False
            for l in code_body_lines: # Find first non-blank line
                if l.strip() == "":
                    first_code_line_is_blank = True
                    continue # keep checking for actual code
                else:
                    break # found actual code

            if not first_code_line_is_blank and final_output_content and final_output_content[-1].strip() != "":
                 # Ensure last import ends with a newline before adding another for spacing
                if not final_output_content[-1].endswith('\n'):
                    final_output_content[-1] = final_output_content[-1].rstrip('\r\n') + '\n'
                if not final_output_content[-1].endswith('\n\n') : # Avoid double blank if last import ended with \n\n
                    final_output_content.append('\n')


        # Append the actual code body lines
        # The code_body_lines might contain the logger line we inserted.
        # It might also contain original import lines if the parsing was imperfect.
        # Let's reconstruct the code body carefully from processed_lines, skipping package and handled imports.

        # Rebuild the file content more carefully
        output_buffer = []
        if package_declaration_line:
            output_buffer.append(package_declaration_line)

        if final_imports_list:
            if output_buffer and output_buffer[-1].strip() != "" and not output_buffer[-1].endswith('\n\n'):
                # Ensure a blank line after package (if any) before imports
                if not output_buffer[-1].endswith('\n'): output_buffer[-1] += '\n'
                output_buffer.append('\n')
            elif not output_buffer: # No package, imports are first
                 pass

            for imp_line in final_imports_list:
                output_buffer.append(imp_line)

        # Add the rest of the code (everything in processed_lines that isn't package or an import line we've handled)
        first_code_line_appended = False
        for line_idx, line_content in enumerate(processed_lines):
            is_package = (line_content == package_declaration_line)
            # Check if this line is one of the imports we put into final_imports_list
            # This is tricky because line endings might differ. Compare stripped versions.
            is_handled_import = False
            for fi_imp in final_imports_list:
                if line_content.strip() == fi_imp.strip():
                    is_handled_import = True
                    break

            if not is_package and not is_handled_import:
                if not first_code_line_appended and output_buffer:
                    # Ensure a blank line between last import (if any) and first line of code
                    if final_imports_list and not output_buffer[-1].endswith('\n\n') and output_buffer[-1].strip() != "":
                         if not output_buffer[-1].endswith('\n'): output_buffer[-1] += '\n'
                         output_buffer.append('\n')
                    elif not final_imports_list and package_declaration_line and not output_buffer[-1].endswith('\n\n') and output_buffer[-1].strip() != "":
                         # No imports, but package exists. Ensure blank line after package before code.
                         if not output_buffer[-1].endswith('\n'): output_buffer[-1] += '\n'
                         output_buffer.append('\n')


                output_buffer.append(line_content)
                first_code_line_appended = True

        # Clean up potential multiple blank lines at the end of blocks
        final_write_lines = []
        for i, line in enumerate(output_buffer):
            if i > 0 and line.isspace() and output_buffer[i-1].isspace():
                continue # Skip consecutive blank lines
            final_write_lines.append(line)


        with open(filepath, 'w') as f:
            f.writelines(final_write_lines)
        print(f"Processed (TAG replaced, imports updated): {filepath}")

    except Exception as e:
        print(f"Error processing file {filepath}: {e}")
        import traceback
        traceback.print_exc()

def main():
    """
    Processes a single .java file specified as a command-line argument
    or scans the current directory and its subdirectories if '--scan' is provided.
    """
    if len(sys.argv) < 2:
        print("Usage: python script_name.py <path_to_java_file>")
        print("   or: python script_name.py --scan [directory_path]")
        print("       (If no directory_path is given for --scan, current directory is used)")
        return

    argument = sys.argv[1]

    if argument == "--scan":
        scan_directory = os.getcwd() # Default to current directory
        if len(sys.argv) > 2:
            # User provided a specific directory to scan
            provided_path = sys.argv[2]
            if os.path.isdir(provided_path):
                scan_directory = os.path.abspath(provided_path)
            else:
                print(f"Error: Provided scan path '{provided_path}' is not a valid directory.")
                return

        print(f"Scanning recursively for .java files with 'String TAG' in: {scan_directory}...")
        files_found_with_tag = 0
        # os.walk yields a 3-tuple (dirpath, dirnames, filenames)
        for dirpath, dirnames, filenames in os.walk(scan_directory):
            for filename in filenames:
                if filename.endswith(".java"):
                    filepath = os.path.join(dirpath, filename)
                    try:
                        # Using 'utf-8' encoding, good practice.
                        with open(filepath, 'r', encoding='utf-8') as f_check:
                            content_for_check = f_check.read()
                        # Refined regex to better match typical 'String TAG = "ClassName";' declarations
                        if re.search(r'\s*(private|protected|public)?\s*(static)?\s*(final)?\s*String\s+TAG\s*=', content_for_check):
                            print(f"Found potential 'String TAG' logger in: {filepath}. Processing...")
                            process_java_file(filepath) # process_java_file should handle its own file I/O exceptions
                            files_found_with_tag += 1
                    except Exception as e: # Catch exceptions during file reading or the initial check
                        print(f"Error reading or initially parsing file {filepath} during scan: {e}")

        if files_found_with_tag == 0:
            print(f"No .java files containing a suitable 'String TAG' field found in {scan_directory} and its subdirectories.")
        else:
            print(f"\nDirectory scan complete. Processed {files_found_with_tag} file(s).")

    elif os.path.isfile(argument): # Processing a single file
        if argument.endswith(".java"):
            print(f"Processing single file: {argument}")
            process_java_file(os.path.abspath(argument)) # Use absolute path
        else:
            print(f"Error: Provided file '{argument}' is not a .java file.")
    else: # Argument is not '--scan' and not a file
        print(f"Error: Argument '{argument}' not recognized.")
        print("Usage: python script_name.py <path_to_java_file>")
        print("   or: python script_name.py --scan [directory_path]")


if __name__ == "__main__":
    main()