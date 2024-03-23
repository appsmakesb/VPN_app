#!/bin/bash

# Check if argument is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <string_value>"
    exit 1
fi

script_dir=$(dirname "$0")
# Argument provided, assign it to a variable
string_value="$1"

# Define the string resource XML
string_resource="<string name=\"main_ad_app_id\">$string_value</string></resources>"

# Path to strings.xml file
strings_xml="app/src/main/res/values/strings.xml"

# Check if strings.xml exists
if [ ! -f "$strings_xml" ]; then
    echo "Error: strings.xml file not found in the specified path."
    exit 1
fi

# Check if string resource already exists
if grep -q "<string name=\"main_ad_app_id\">" "$strings_xml"; then
    echo "String resource 'main_ad_app_id' already exists in strings.xml."
    exit 1
fi

# Add the string resource to strings.xml
echo "$string_resource" >> "$strings_xml"

echo "String resource 'main_ad_app_id' successfully added to strings.xml."
