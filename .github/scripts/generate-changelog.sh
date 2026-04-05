#!/bin/sh
# Generates a changelog section from merged GitHub PRs since the last release
# tag and inserts it into CHANGELOG.md after the <!-- CHANGELOG_INSERT --> marker.
#
# Required env vars:
#   VERSION            - the new version, e.g. "3.1.0"
#   GH_TOKEN           - GitHub token for gh CLI calls
#   GITHUB_REPOSITORY  - owner/repo, e.g. "LikeTheSalad/aaper"
#
# Optional:
#   RELEASE_DATE    - override the release date (defaults to today, YYYY-MM-DD)
#   CHANGELOG_FILE  - path to the changelog (defaults to CHANGELOG.md)
#
# PR label → changelog category mapping:
#   "bug"         → ### Bug Fixes
#   "enhancement" → ### Features
#   (no match)    → listed first, directly under the version heading
set -eu

version="${VERSION:-}"
release_date="${RELEASE_DATE:-$(date +%Y-%m-%d)}"
changelog_file="${CHANGELOG_FILE:-CHANGELOG.md}"
repo="${GITHUB_REPOSITORY:-}"

[ -z "$version" ] && { echo "VERSION is required" >&2; exit 1; }
[ -z "$repo" ]    && { echo "GITHUB_REPOSITORY is required" >&2; exit 1; }
[ ! -f "$changelog_file" ] && { echo "Changelog not found: $changelog_file" >&2; exit 1; }

# Find the latest release tag
latest_tag=$(git tag --sort=-v:refname | grep -E '^v[0-9]+\.[0-9]+\.[0-9]+$' | head -1)
[ -z "$latest_tag" ] && { echo "No release tags found" >&2; exit 1; }

# Short date of the last tag (YYYY-MM-DD) used in the GitHub search qualifier
last_tag_date=$(git log -1 --format="%cs" "$latest_tag")

# Fetch merged PRs since the last release; exclude release-automation branches
prs_json=$(gh pr list \
  --state merged \
  --search "merged:>${last_tag_date}" \
  --json number,title,labels,headRefName \
  --limit 200 \
  --jq '[.[] | select(.headRefName | test("^(pre-release|release)/") | not)]')

pr_count=$(printf '%s' "$prs_json" | jq length)
if [ "$pr_count" -eq 0 ]; then
  echo "No merged PRs since $latest_tag ($last_tag_date) — nothing to add to changelog" >&2
  exit 1
fi

# Build categorised changelog body via jq.
# Uncategorised PRs appear first (no sub-heading), then labelled categories.
entries=$(printf '%s' "$prs_json" | jq -r \
  --arg repo "$repo" \
  '
  def link: "([#" + (.number|tostring) + "](https://github.com/" + $repo + "/pull/" + (.number|tostring) + "))";
  def entry: "* " + .title + " " + link;
  def lnames: [.labels[].name];
  def category:
    if   (lnames | any(. == "bug"))         then "bug"
    elif (lnames | any(. == "enhancement")) then "enhancement"
    else "other" end;
  def section(title; items):
    if (items | length) > 0
    then "### " + title + "\n\n" + (items | map(entry) | join("\n"))
    else "" end;

  (map(select(category == "other"))       | map(entry) | join("\n"))              as $other    |
  section("Bug Fixes"; map(select(category == "bug")))                            as $bug_fixes |
  section("Features";  map(select(category == "enhancement")))                   as $features  |

  [$other, $bug_fixes, $features] | map(select(. != "")) | join("\n\n")
  ')

# Insert the new section immediately after the <!-- CHANGELOG_INSERT --> marker.
# The marker is preserved so each future release inserts above the previous one.
tmp=$(mktemp)
trap 'rm -f "$tmp"' EXIT

inserted=0
while IFS= read -r line; do
  printf '%s\n' "$line"
  if [ "$inserted" -eq 0 ] && [ "$line" = "<!-- CHANGELOG_INSERT -->" ]; then
    printf '\n## Version %s (%s)\n\n%s\n' "$version" "$release_date" "$entries"
    inserted=1
  fi
done < "$changelog_file" > "$tmp"

if [ "$inserted" -eq 0 ]; then
  echo "<!-- CHANGELOG_INSERT --> marker not found in $changelog_file" >&2
  exit 1
fi

mv "$tmp" "$changelog_file"
printf 'CHANGELOG.md updated — %s PR(s) under version %s\n' "$pr_count" "$version"
