#!/bin/sh

set -eu

properties_file="${PROPERTIES_FILE:-gradle.properties}"

if [ ! -f "$properties_file" ]; then
  echo "Properties file not found: $properties_file" >&2
  exit 1
fi

current_version=$(grep -m 1 '^version=[0-9][0-9]*\.[0-9][0-9]*\.[0-9][0-9]*$' "$properties_file" | cut -d= -f2)

if [ -z "$current_version" ]; then
  echo "Could not read version from $properties_file" >&2
  exit 1
fi

major=$(printf '%s\n' "$current_version" | cut -d. -f1)
minor=$(printf '%s\n' "$current_version" | cut -d. -f2)

new_version="$major.$((minor + 1)).0"

sed -i.bak "s/^version=$current_version$/version=$new_version/" "$properties_file"
rm -f "$properties_file.bak"

echo "Bumped version: $current_version -> $new_version"
