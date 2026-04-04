#!/usr/bin/env bash

set -euo pipefail

changelog_file="${CHANGELOG_FILE:-CHANGELOG.md}"
version="${VERSION:-}"
release_date="${RELEASE_DATE:-$(date +%Y-%m-%d)}"

if [[ -z "$version" ]]; then
  echo "VERSION is required" >&2
  exit 1
fi

if [[ ! -f "$changelog_file" ]]; then
  echo "Changelog file not found: $changelog_file" >&2
  exit 1
fi

if ! grep -q '^## Unreleased$' "$changelog_file"; then
  echo "\"## Unreleased\" not found in $changelog_file" >&2
  exit 1
fi

perl -0pi -e 's/^## Unreleased$/## Version '"$version"' ('"$release_date"')/m' "$changelog_file"
