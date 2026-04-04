#!/usr/bin/env bash

set -euo pipefail

properties_file="${PROPERTIES_FILE:-gradle.properties}"

if [[ ! -f "$properties_file" ]]; then
  echo "Properties file not found: $properties_file" >&2
  exit 1
fi

current_version="$(
  perl -ne 'print "$1\n" if /^version=(\d+\.\d+\.\d+)$/' "$properties_file" | head -n 1
)"

if [[ -z "$current_version" ]]; then
  echo "Could not read version from $properties_file" >&2
  exit 1
fi

if [[ ! "$current_version" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
  echo "Could not find minor version in: $current_version" >&2
  exit 1
fi

new_version="${BASH_REMATCH[1]}.$((BASH_REMATCH[2] + 1)).0"

perl -0pi -e 's/^version=\Q'"$current_version"'\E$/version='"$new_version"'/m' "$properties_file"

echo "Bumped version: $current_version -> $new_version"
