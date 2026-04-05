# .github Architecture

This document describes the structure, features, and design decisions of the `.github` directory.
Its purpose is to let a new developer or agent understand the CI/CD setup quickly and reproduce the
same patterns in another repository.

For manual setup instructions such as secrets, GitHub App installation, and branch protection, see
[SETUP.md](SETUP.md).

---

## Directory Structure

```
.github/
├── ARCHITECTURE.md              # This file
├── SETUP.md                     # Manual setup instructions for a new remote repo
├── renovate.json                # Renovate dependency-update bot configuration
├── scripts/
│   └── generate-changelog.sh    # Builds a version section from merged PRs via GitHub API
├── actions/
│   ├── setup-java/              # Composite action: installs the project JDK
│   ├── setup-bot-git-user/      # Composite action: creates GitHub App token + configures git
│   ├── publish-maven-central/   # Composite action: publishes to Maven Central
│   └── publish-gradle-portal/   # Composite action: publishes to Gradle Plugin Portal
└── workflows/
    ├── pr-check.yaml            # CI: runs on every PR
    ├── release-prepare.yml      # Schedule/manual: prepares a release and opens a review PR
    └── release-publish.yml      # PR-close trigger: publishes after pre-release PR merge
```

---

## Features

### 1. PR Checks

**What it does:** Runs on every pull request and on `workflow_dispatch`. Executes the full
verification suite (`./checks.sh`) and Android instrumentation tests on an emulator. Uploads build
reports as an artifact on failure.

**Files involved:**
- `workflows/pr-check.yaml`
- `actions/setup-java/action.yml`

**How it works:**

The workflow has two jobs:

- `build`: checks out the code, sets up Java, runs `./checks.sh`, launches an Android emulator via
  `reactivecircus/android-emulator-runner`, and runs
  `./gradlew -p demo-app connectedDebugAndroidTest`.
- `checks`: runs with `if: always()`, depends on `build`, and is the only job registered in branch
  protection as a required status check. It fails if any dependency ended in `failure` or
  `cancelled`.

**Key detail — rollup job:** branch protection only requires the `checks` job. When new CI jobs are
added, extend the `needs` list on `checks` instead of changing repository settings.

---

### 2. Release Preparation

**What it does:** Creates a release candidate branch pair and a human-review PR before anything is
published.

**Files involved:**
- `workflows/release-prepare.yml`
- `scripts/generate-changelog.sh`
- `actions/setup-bot-git-user/action.yml`

**Trigger:**

- Scheduled on the 1st of every month at noon UTC
- Manual via `workflow_dispatch`

**How it works:**

The workflow has two jobs:

- `check`: validates whether a release should happen and calculates the next version.
- `prepare`: creates the release branches, updates files, commits them, and opens the PR.

**`check` job details:**

1. On scheduled runs only, counts open PRs via `gh pr list`. If any are open, it exits cleanly with
   `should_release=false`.
2. Checks out `main` with full history.
3. Finds the latest semver tag matching `v{major}.{minor}.{patch}`.
4. Queries merged PRs since the latest tag, excluding release automation branches
   (`pre-release/*`, `release/*`).
5. If no merged PRs are found, exits cleanly with `should_release=false`.
6. Chooses the next version bump:
   - any `breaking` label → major
   - otherwise any `enhancement` label → minor
   - otherwise → patch
   - manual `version_override` input wins over labels
7. Exposes the computed version as a job output.

**`prepare` job details:**

1. Checks out the repository, then sets up the bot git user via the local composite action.
2. Checks out again with the bot token so pushes and PR operations authenticate as the bot.
3. Creates and pushes `release/{version}` from `main`.
4. Creates `pre-release/{version}` from that release branch.
5. Updates `gradle.properties` to the release version.
6. Runs `sh .github/scripts/generate-changelog.sh`.
7. Updates README plugin-version references via `sed`.
8. Commits the prepared files and pushes `pre-release/{version}`.
9. Opens a PR `pre-release/{version}` → `release/{version}`.

**Human step:** a maintainer reviews and merges the pre-release PR. `pr-check.yaml` runs on that PR
exactly like any other pull request.

---

### 3. Release Publishing

**What it does:** Publishes artifacts only after the human-approved pre-release PR is merged into a
release branch.

**Files involved:**
- `workflows/release-publish.yml`
- `actions/setup-bot-git-user/action.yml`
- `actions/setup-java/action.yml`
- `actions/publish-maven-central/action.yml`
- `actions/publish-gradle-portal/action.yml`

**Trigger:**

- `pull_request` with `types: [closed]` on `release/**`

The job is guarded so it only runs when:

- the PR was merged, and
- the source branch starts with `pre-release/`

This prevents accidental publishes from unrelated PRs targeting `release/**`.

**How it works:**

1. Checks out the repository, then sets up the bot git user.
2. Checks out the merged `release/{version}` branch using the bot token.
3. Sets up Java.
4. Reads the release version from `gradle.properties`. No temporary version patching is needed
   because the version was already committed during the prepare phase.
5. Publishes to Maven Central.
6. Publishes to the Gradle Plugin Portal.
7. Creates and pushes a git tag `v{version}` on the current `release/{version}` HEAD.
8. Creates a GitHub Release for that tag.
9. Opens a PR `release/{version}` → `main` and enables GitHub auto-merge with
   `gh pr merge --auto --merge`.

---

### 4. Changelog Generation

**What it does:** Builds release notes from merged pull requests and inserts a new version section
into `CHANGELOG.md`.

**Files involved:**
- `scripts/generate-changelog.sh`

**Inputs:**
- `VERSION`
- `GH_TOKEN`
- `GITHUB_REPOSITORY`

**Optional inputs:**
- `RELEASE_DATE`
- `CHANGELOG_FILE`

**How it works:**

1. Finds the latest semver git tag.
2. Determines the tag commit date.
3. Fetches merged PRs after that date with `gh pr list`, excluding `pre-release/*` and
   `release/*` branches.
4. Categorises PRs by labels:
   - `bug` → `### Bug Fixes`
   - `enhancement` → `### Features`
   - otherwise → listed directly under the version heading
5. Inserts a new `## Version X.Y.Z (YYYY-MM-DD)` section immediately after the
   `<!-- CHANGELOG_INSERT -->` marker.

**Why the marker exists:** the old `## Unreleased` block was replaced with an HTML comment marker so
the insertion point stays stable and invisible in rendered Markdown.

---

### 5. Dependency Auto-updates

**What it does:** Renovate opens dependency update PRs and enables GitHub auto-merge on them once
CI passes.

**Files involved:**
- `renovate.json`

**Configuration highlights:**

- `"automerge": true` means Renovate uses GitHub's native auto-merge instead of merging directly.
- `"automergeStrategy": "squash"` keeps dependency updates as single commits on `main`.
- `"schedule": ["* * 2-31 * *"]` avoids creating or updating Renovate PRs on the 1st of the month,
  leaving the scheduled release window clear.
- `packageRules` groups the `androidx.test` dependency family because partial upgrades tend to break
  the demo app's instrumentation stack.

---

## Composite Actions

Composite actions live in `.github/actions/` and are called with
`uses: ./.github/actions/{name}`.

> **Note for authors:** every `run` step inside a composite action must declare an explicit shell.

### `setup-java`

Installs the shared JDK version for all workflows.

**Used by:** `pr-check.yaml`, `release-publish.yml`

### `setup-bot-git-user`

**Inputs:** `app-id`, `private-key`  
**Outputs:** `token`

Creates a GitHub App installation token, resolves the bot's numeric user ID, and configures global
git author information for subsequent commits.

The token is used by callers for:

- `token:` in `actions/checkout`
- `GH_TOKEN:` in steps that invoke `gh`

**Used by:** `release-prepare.yml`, `release-publish.yml`

**Why a GitHub App token instead of `GITHUB_TOKEN`:** PRs created with `GITHUB_TOKEN` do not
trigger other workflows. The release flows rely on bot-created PRs running `pr-check.yaml`, so an
App token is required.

### `publish-maven-central`

Publishes all modules to Maven Central with signing enabled.

**Used by:** `release-publish.yml`

### `publish-gradle-portal`

Publishes the Gradle plugin to the Gradle Plugin Portal with signing enabled.

**Used by:** `release-publish.yml`

---

## Design Patterns and Decisions

### Rollup status check

Branch protection requires only the `checks` job from `pr-check.yaml`. This keeps the repository
settings stable even when CI grows additional jobs.

### Two-phase release flow

Preparation and publishing are split across two workflows:

- `release-prepare.yml` handles version calculation and file changes.
- `release-publish.yml` only runs after a human-approved merge into `release/**`.

This keeps the publish phase simple and ensures no artifact publication happens before review.

### Version is committed before publish

`gradle.properties` is updated during preparation and merged into `release/{version}` before
publishing starts. That means Gradle, generated metadata, and release automation all read the same
committed version value.

### Regular merge from `release/**` to `main`

The final PR back to `main` must use a regular merge, not squash. The release tag points at the
commit on `release/{version}`; a squash merge would create a different commit on `main` and leave
the tag outside `main`'s ancestry.

### Scheduled releases only run on a quiet repository

The monthly scheduled release preparation exits early if any PR is open. Combined with Renovate's
schedule exclusion on the 1st, this keeps the release window predictable.

---

## Adapting for Another Repository

### Portable as-is

- The `checks` rollup-job pattern in `pr-check.yaml`
- The `setup-bot-git-user` composite action structure
- The split prepare/publish release workflow model
- The `<!-- CHANGELOG_INSERT -->` marker pattern
- The regular-merge requirement for the final release PR

### Project-specific parts to replace

| What | Where used | Replace with |
|------|------------|--------------|
| `./checks.sh` | `pr-check.yaml` | Your project's verification command |
| `./gradlew -p demo-app connectedDebugAndroidTest` | `pr-check.yaml` | Your integration test command, or remove it |
| `reactivecircus/android-emulator-runner` | `pr-check.yaml` | Remove if not an Android project |
| `gradle.properties` version regex | `release-prepare.yml`, `release-publish.yml` | Path and pattern for your version source |
| `sh .github/scripts/generate-changelog.sh` | `release-prepare.yml` | Your release-note generation command |
| README plugin version `sed` pattern | `release-prepare.yml` | Regex for your project's documented version references |
| `v[0-9]+\.[0-9]+\.[0-9]+` tag pattern | `release-prepare.yml`, `generate-changelog.sh` | Your tag format |
| `publish-maven-central` action | `release-publish.yml` | Replace or remove if not publishing there |
| `publish-gradle-portal` action | `release-publish.yml` | Replace or remove if not a Gradle plugin |
| Renovate `packageRules` grouping | `renovate.json` | Your dependency grouping needs |

### Required secrets and variables

| Name | Purpose |
|------|---------|
| `GH_BOT_PRIVATE_KEY` | GitHub App private key |
| `APP_ID` | GitHub App ID |
| `GPG_PRIVATE_KEY` | Artifact signing |
| `GPG_PASSWORD` | Artifact signing |

Publishing-destination secrets (`MAVEN_CENTRAL_*`, `GRADLE_PUBLISH_*`) are only needed if those
services are used.
