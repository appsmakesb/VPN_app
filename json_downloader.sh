#!/bin/bash

# Get the directory of the script
script_dir=$(dirname "$0")

# Check if URL argument is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <URL>"
    exit 1
fi

# URL of the JSON file
url="$1"

# Destination folder
destination="$script_dir/app"

# Create the destination folder if it doesn't exist
mkdir -p "$destination"

# Download the JSON file
echo "Downloading JSON file from $url..."
curl -o "$destination/google-services.json" "$url"

# Check if download was successful
if [ $? -eq 0 ]; then
    echo "Download successful. JSON file saved to $destination"
else
    echo "Error downloading JSON file"
fi
