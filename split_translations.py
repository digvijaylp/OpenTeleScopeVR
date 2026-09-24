#!/usr/bin/env python3
import os
import re

RES_DIR = "app/src/main/res"

def split_file(filename, output_file_name):
    if not os.path.exists(filename):
        print(f"File {filename} not found, skipping.")
        return

    with open(filename, "r", encoding="utf-8") as f:
        content = f.read()

    # Match delimiter: === LOCALE: <tag> ===
    chunks = re.split(r'===\s*LOCALE:\s*([a-zA-Z0-9_\-\+]+)\s*===', content)
    
    if len(chunks) < 2:
        print(f"No delimiters found in {filename}.")
        return

    # chunks[0] is header/intro, then pairs of (locale_tag, xml_content)
    for i in range(1, len(chunks), 2):
        locale = chunks[i].strip()
        xml_body = chunks[i+1].strip()

        # Handle base/default folder mapping
        folder_name = f"values-{locale}" if locale != "default" else "values"
        target_dir = os.path.join(RES_DIR, folder_name)
        os.makedirs(target_dir, exist_ok=True)

        target_path = os.path.join(target_dir, output_file_name)
        with open(target_path, "w", encoding="utf-8") as out:
            out.write(xml_body + "\n")
        print(f"Wrote {target_path}")

def main():
    split_file("all_strings.txt", "strings.xml")
    split_file("all_arrays.txt", "arrays.xml")
    print("\nAll locales split successfully!")

if __name__ == "__main__":
    main()