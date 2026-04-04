#!/bin/sh

set -eu

changelog_file="${CHANGELOG_FILE:-CHANGELOG.md}"
version="${VERSION:-}"
release_date="${RELEASE_DATE:-$(date +%Y-%m-%d)}"

if [ -z "$version" ]; then
  echo "VERSION is required" >&2
  exit 1
fi

if [ ! -f "$changelog_file" ]; then
  echo "Changelog file not found: $changelog_file" >&2
  exit 1
fi

if ! grep -q '^## Unreleased$' "$changelog_file"; then
  echo "\"## Unreleased\" not found in $changelog_file" >&2
  exit 1
fi

sed -i.bak "s/^## Unreleased$/## Version $version ($release_date)/" "$changelog_file"
rm -f "$changelog_file.bak"
