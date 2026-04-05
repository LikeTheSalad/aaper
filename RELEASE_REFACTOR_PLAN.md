# Release Workflow Refactor — Implementation Plan

## Goal

Replace `prepare-release.yml`, `release.yml`, and `auto-patch-release.yml` with two
focused workflow files:

- **`release-prepare.yml`** — triggered by `schedule` or `workflow_dispatch`; calculates
  the new version, prepares files, and opens a PR for human review before anything is
  published.
- **`release-publish.yml`** — auto-triggered when the human merges the pre-release PR;
  publishes artifacts, creates the tag and GitHub Release, then opens a final PR back to
  `main`.

Each file has one trigger type and one clear purpose.

---

## New Two-Branch Release Flow

```
main
 │
 ├─ release-prepare.yml creates ──► release/3.1.0  (empty, from main)
 │                                        │
 │                                        └─► pre-release/3.1.0
 │                                                  │  (gradle.properties + CHANGELOG + README)
 │                                            PR opened (pre-release → release)
 │                                            ← human reviews; CI runs automatically →
 │                                                  │ merge (squash or regular — human's choice)
 │                                                  ▼
 │                                            release/3.1.0  (now has the prepared files)
 │                                                  │
 │                                            release-publish.yml auto-triggers
 │                                                  │
 │                                            artifacts published
 │                                            tag v3.1.0 created on release/3.1.0 HEAD
 │                                            GitHub Release created
 │                                                  │
 │                                            PR opened (release → main)
 │                                            ← auto-merges once CI passes →
 │                                                  │ regular merge (non-squash — preserves tag
 │◄────────────────────────────────────────────────┘   in main's linear history)
```

---

## `release-prepare.yml`

### Trigger

```yaml
on:
  schedule:
    - cron: '0 12 1 * *'   # automated monthly release
  workflow_dispatch:
    inputs:
      version_override:     # optional: force bump type regardless of labels
        type: choice
        options: ['', 'patch', 'minor', 'major']
        default: ''
```

### Steps

1. **[schedule only]** Count open PRs via `gh pr list`; exit cleanly if any exist.
2. Checkout `main` with full history (`fetch-depth: 0`).
3. Find the latest semver tag (`v{major}.{minor}.{patch}`). Exit cleanly if no PRs have
   been merged since that tag.
4. **Calculate new version** from labels of merged PRs since last tag:
   - At least one `breaking` label → major bump (e.g. `3.0.0` → `4.0.0`)
   - At least one `enhancement`, no `breaking` → minor bump (e.g. `3.0.0` → `3.1.0`)
   - Neither → patch bump (e.g. `3.0.0` → `3.0.1`)
   - `workflow_dispatch` `version_override` set → use it, ignore labels
5. Create and push `release/{version}` branch from `main`.
6. Create and push `pre-release/{version}` branch from `release/{version}`.
7. On `pre-release/{version}`:
   - Set `gradle.properties` to the new version.
   - Run `generate-changelog.sh` to insert the new version section into `CHANGELOG.md`.
   - Update README version references via `sed`.
8. Commit all changes and push `pre-release/{version}`.
9. Open PR: `pre-release/{version}` → `release/{version}`.

**Human reviews and merges the PR.** `pr-check.yaml` runs on it automatically.
Branch protection on `release/**` should require the `checks` status before merging
(see SETUP.md).

---

## `release-publish.yml`

### Trigger

```yaml
on:
  pull_request:
    types: [closed]
    branches:
      - 'release/**'
```

Guard in the job's `if`:
```yaml
if: >
  github.event.pull_request.merged == true &&
  startsWith(github.event.pull_request.head.ref, 'pre-release/')
```

This ensures the publish job only runs when a `pre-release/**` PR is merged into a
`release/**` branch — not on any other PR close event against that branch.

### Steps

1. Set up bot git user (via `setup-bot-git-user` composite action).
2. Checkout `release/{version}` with bot token.
3. Set up Java (via `setup-java` composite action).
4. Read version from `gradle.properties` — already set in the prepare phase, no temporary
   patching needed.
5. Publish to Maven Central via `publish-maven-central` composite action.
6. Publish to Gradle Plugin Portal via `publish-gradle-portal` composite action.
7. Create and push git tag `v{version}` on current HEAD.
8. Create GitHub Release targeting `release/{version}`.
9. Open PR `release/{version}` → `main` and enable auto-merge
   (`gh pr merge --auto --merge`). Uses **regular (non-squash) merge** to keep the tagged
   commit in `main`'s linear history.

---

## Version Calculation Logic

```
latest tag:  v3.0.0
PRs since:   #91 (labels: bug), #92 (labels: enhancement), #93 (no labels)

breaking? NO  →  enhancement? YES  →  minor bump  →  3.1.0
```

```
latest tag:  v3.1.0
PRs since:   #94 (labels: bug), #95 (no labels)

breaking? NO  →  enhancement? NO  →  patch bump  →  3.1.1
```

```
latest tag:  v3.1.1
PRs since:   #96 (labels: breaking, enhancement)

breaking? YES  →  major bump  →  4.0.0
```

---

## CHANGELOG.md Format Change

Replace the `## Unreleased` block with a comment marker:

**Before:**
```markdown
Change Log
==========

## Unreleased

* Some hand-written notes

## Version 3.0.0 ...
```

**After:**
```markdown
Change Log
==========
<!-- CHANGELOG_INSERT -->

## Version 3.0.0 ...
```

`generate-changelog.sh` inserts the new `## Version` section immediately after
`<!-- CHANGELOG_INSERT -->`. The comment is invisible in rendered Markdown and survives
every release cycle untouched.

---

## Scripts

### Retired (deleted)
- `bump-version.sh` — no longer needed; `gradle.properties` is updated to the released
  version in the prepare phase and never bumped speculatively.
- `update-changelog.sh` — replaced by `generate-changelog.sh`.
- `generate-patch-changelog.sh` — merged into `generate-changelog.sh`.

### Created
**`generate-changelog.sh`** — single script covering all release types.

Responsibilities:
- Accept `VERSION`, `GH_TOKEN`, `GITHUB_REPOSITORY` env vars.
- Query merged PRs since the last release tag via `gh pr list`.
- Categorise by labels using `jq`:
  - `bug` → `### Bug Fixes`
  - `enhancement` → `### Features`
  - no matching label → listed directly under the version heading (no sub-heading)
- Insert the new `## Version {version} ({date})` block after `<!-- CHANGELOG_INSERT -->`.

If the script approaches 500 lines, extract a `pr-entries.sh` helper that handles the
`gh pr list` query and `jq` categorisation, called from the main script.

---

## Why `release/**` → `main` Must Use Regular (Non-Squash) Merge

The git tag `v3.1.0` is placed on `release/3.1.0`'s HEAD during the publish phase.
A squash merge of `release/3.1.0` → `main` creates a brand-new commit on `main` with a
different SHA. The tag stays on the original branch commit, which is no longer in
`main`'s ancestry. `git log main`, `git describe --tags`, and any tooling that walks
main's history would not find the tag.

A regular merge brings the tagged commit directly into `main`'s linear history.
`gh pr merge --auto --merge` in the publish step enforces this explicitly.

The `pre-release/**` → `release/**` merge has no such constraint (no tag exists yet),
so the human can squash or regular-merge at their discretion.

---

## Files Created
| File | Notes |
|------|-------|
| `.github/workflows/release-prepare.yml` | Replaces `prepare-release.yml` + `auto-patch-release.yml` |
| `.github/workflows/release-publish.yml` | Replaces `release.yml` |
| `.github/scripts/generate-changelog.sh` | Replaces three retired scripts |

## Files Retired (deleted)
| File |
|------|
| `.github/workflows/prepare-release.yml` |
| `.github/workflows/release.yml` |
| `.github/workflows/auto-patch-release.yml` |
| `.github/scripts/bump-version.sh` |
| `.github/scripts/update-changelog.sh` |
| `.github/scripts/generate-patch-changelog.sh` |

## Files Modified
| File | Change |
|------|--------|
| `CHANGELOG.md` | Replace `## Unreleased` block with `<!-- CHANGELOG_INSERT -->` |
| `.github/ARCHITECTURE.md` | Full rewrite of CI/CD sections |
| `.github/SETUP.md` | Add branch protection for `release/**`; remove obsolete steps |
| `CLAUDE.md` | Update CI/CD & Release Process section |

`renovate.json` requires no changes — Renovate branches (`renovate/**`) do not match
`release/**`, so `release-publish.yml` will never fire on a Renovate merge.

---

## Implementation Tasks (in order)

1. **Update `CHANGELOG.md`** — replace `## Unreleased` block with `<!-- CHANGELOG_INSERT -->`.
2. **Create `generate-changelog.sh`** — write the unified changelog script.
3. **Create `release-prepare.yml`** — implement the prepare phase.
4. **Create `release-publish.yml`** — implement the publish phase.
5. **Delete retired files** — remove the three old workflow files and three old scripts.
6. **Update `ARCHITECTURE.md`** — rewrite the CI/CD section to reflect the new structure.
7. **Update `SETUP.md`** — add branch protection for `release/**`; remove obsolete steps.
8. **Update `CLAUDE.md`** — update the CI/CD & Release Process section.
