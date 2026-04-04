# .github Architecture

This document describes the structure, features, and design decisions of the `.github` directory.
Its purpose is to allow a new agent or developer to fully understand the CI/CD setup and replicate
a similar approach in another repository.

For manual setup instructions (secrets, branch protection, etc.) see [SETUP.md](SETUP.md).

---

## Directory Structure

```
.github/
├── ARCHITECTURE.md          # This file
├── SETUP.md                 # Manual setup instructions for a new remote repo
├── renovate.json            # Renovate dependency-update bot configuration
├── scripts/
│   ├── bump-version.sh      # Shell helper: bumps gradle.properties to the next minor version
│   └── update-changelog.sh  # Shell helper: turns "Unreleased" into a versioned heading
├── actions/
│   ├── setup-java/          # Composite action: installs the project JDK
│   ├── setup-bot-git-user/  # Composite action: creates GitHub App token + configures git
│   ├── publish-maven-central/   # Composite action: publishes to Maven Central
│   └── publish-gradle-portal/   # Composite action: publishes to Gradle Plugin Portal
└── workflows/
    ├── pr-check.yaml        # CI: runs on every PR
    ├── prepare-release.yml  # Manual trigger: starts a minor/major release
    ├── release.yml          # Manual trigger: publishes a minor/major release
    └── auto-patch-release.yml  # Scheduled: automated monthly patch release
```

---

## Features

### 1. PR Checks

**What it does:** Runs on every pull request and on `workflow_dispatch`. Executes the full
verification suite (`./checks.sh`) and then runs connected Android instrumentation tests on an
emulated device. Uploads build reports as an artifact on failure.

**Files involved:**
- `workflows/pr-check.yaml`
- `actions/setup-java/action.yml`

**How it works:**

The workflow has two jobs:

- `build` (`Gradle check`): checks out the code, sets up Java, runs `./checks.sh`, then launches
  an Android emulator via `reactivecircus/android-emulator-runner` and runs
  `./gradlew -p demo-app connectedDebugAndroidTest`.
- `checks` (rollup): runs with `if: always()` and depends on `build`. It is the **only** job
  registered as a required status check in branch protection (see the rollup pattern below). It
  exits 1 if any dependency resulted in `failure` or `cancelled`.

**Key detail — rollup job:** To avoid having to update branch protection rules every time a new job
is added to this workflow, branch protection only requires the `checks` job. When adding a new job,
add it to `checks`'s `needs` list — the branch rule stays unchanged.

---

### 2. Manual Minor/Major Release

**What it does:** A two-step manual process for releasing a new minor or major version. Step 1
prepares a release branch and PR for review; step 2 publishes the artifacts and wraps up.

**Files involved:**
- `workflows/prepare-release.yml` (step 1)
- `workflows/release.yml` (step 2)
- `scripts/update-changelog.sh`
- `scripts/bump-version.sh`
- `actions/setup-bot-git-user/action.yml`
- `actions/setup-java/action.yml`
- `actions/publish-maven-central/action.yml`
- `actions/publish-gradle-portal/action.yml`

**Version source:** The `version` key in `gradle.properties` at the root of the repository.

**Step 1 — `prepare-release.yml`:**

- Triggered manually (`workflow_dispatch`) on the `main` branch. Guards against running on other
  branches.
- Reads `version` from `gradle.properties` (e.g. `3.1.0`).
- Creates and pushes a `release/3.1.0` branch.
- Runs `sh .github/scripts/update-changelog.sh` and updates version references in `README.md`
  via `sed`.
- Creates a `pre-release/3.1.0` branch, commits those changes, and opens a PR:
  `pre-release/3.1.0` → `release/3.1.0`.
- A human reviews and merges that PR, then manually triggers step 2 on `release/3.1.0`.

**Step 2 — `release.yml`:**

- Triggered manually (`workflow_dispatch`) on a `release/*` branch. Guards against running on
  other branches.
- Two sequential jobs:
  - `release`: publishes to Maven Central and the Gradle Plugin Portal.
  - `post_release` (runs after `release` succeeds): reads the version from `gradle.properties`,
    creates a git tag (`v3.1.0`), pushes it, creates a GitHub Release, runs
    `sh .github/scripts/bump-version.sh` to advance `gradle.properties` to the next version,
    commits and pushes, then opens a PR from the release branch back to `main`.

---

### 3. Automated Monthly Patch Release

**What it does:** On the 1st of every month at noon UTC, automatically releases the next patch
version (e.g. `3.0.0` → `3.0.1`) if the repository has no open PRs. The entire flow — publishing,
tagging, changelog, and merging back to `main` — is unattended.

**Files involved:**
- `workflows/auto-patch-release.yml`
- `scripts/update-changelog.sh`
- `actions/setup-bot-git-user/action.yml`
- `actions/setup-java/action.yml`
- `actions/publish-maven-central/action.yml`
- `actions/publish-gradle-portal/action.yml`

**How it works:**

Two jobs:

**`check` job:**
1. Counts all open PRs in the repository via `gh pr list`. Sets `should_release=false` and exits
   cleanly if any are found — this is expected, not an error.
2. Fetches full git history and all tags, finds the latest semver tag matching
   `v{major}.{minor}.{patch}`, increments the patch component, and exposes it as an output.

**`release` job** (only runs when `should_release == 'true'`):
1. Sets up the bot user and checks out `main` using the bot token (required so that subsequent
   pushes work under the bot identity).
2. Creates and pushes an `auto-patch/{version}` branch.
3. **Temporarily** patches `gradle.properties` with the new patch version using `sed`. This is
   intentionally **not committed** — it only affects the local runner so that the changelog script,
   the publish tasks (`publishAndReleaseToMavenCentral`, `publishPlugins`), and the plugin's
   embedded `BuildConfig.SDK_DEPENDENCY_URI` use the correct version.
4. Runs `sh .github/scripts/update-changelog.sh` and updates README version references.
5. Publishes to Maven Central, then to the Gradle Plugin Portal.
6. Restores `gradle.properties` (`git checkout -- gradle.properties`), stages only `CHANGELOG.md`
   and `README.md`, commits, and pushes. This keeps `gradle.properties` on `main` pointing at the
   planned next minor version, unaffected by patch releases.
7. Creates and pushes a git tag (`v{version}`), then creates a GitHub Release targeting the
   `auto-patch/{version}` branch.
8. Opens a PR `auto-patch/{version}` → `main` and calls `gh pr merge --auto --merge` to enable
   GitHub's native auto-merge. The PR merges automatically once `pr-check.yaml` passes. A regular
   (non-squash) merge is used so the tagged commit remains in `main`'s linear history.

**Prerequisite:** "Allow auto-merge" must be enabled in repo Settings → General, and `main` must
have branch protection requiring the `checks` status check (see SETUP.md).

---

### 4. Dependency Auto-updates (Renovate)

**What it does:** Renovate automatically opens PRs for outdated dependencies and merges them
without human intervention once CI passes.

**Files involved:**
- `renovate.json`

**Configuration:**

- `"automerge": true` — Renovate sets GitHub's native auto-merge flag on every PR it opens.
  GitHub merges the PR as soon as the `checks` status check passes, without Renovate needing to
  run again.
- `"schedule": ["* * 2-31 * *"]` — Renovate does not create or update PRs on the 1st of the
  month. This coordinates with the automated patch release: by the time noon UTC arrives on the
  1st, no new Renovate PRs exist to block the open-PR check.
- `packageRules`: the `androidx.test` stack (including `fragment-testing`) is grouped into a
  single PR because partial upgrades cause AGP consistent-resolution conflicts in the demo app.

**Why automerge works safely:** Renovate PRs must pass `pr-check.yaml` (which runs the full test
suite and instrumentation tests) before GitHub merges them. The `checks` rollup job is the required
status check, so CI is always the gate.

---

## Composite Actions

Composite actions live in `.github/actions/` and are called with `uses: ./.github/actions/{name}`.
Each one eliminates a repeated block of steps across multiple workflows.

> **Note for authors:** Every `run` step inside a composite action **must** specify `shell: bash`
> (or another explicit shell). Unlike regular workflow steps, composite actions do not inherit a
> default shell.

### `setup-java`

Installs the JDK used by all workflows. The version is defined here only — change it in one place
to apply the update everywhere.

**Used by:** `pr-check.yaml`, `release.yml` (job `release`), `auto-patch-release.yml`.

### `setup-bot-git-user`

**Inputs:** `app-id`, `private-key`  
**Outputs:** `token` — the GitHub App installation token

Creates a scoped GitHub App token via `actions/create-github-app-token`, resolves the bot's
numeric user ID from the GitHub API (needed to construct a valid committer email), and configures
global `git user.name` / `git user.email` so all subsequent commits in the job are attributed to
the bot.

The `token` output is used by callers for:
- `token:` in `actions/checkout` (so pushes authenticate as the bot)
- `GH_TOKEN:` in steps that call `gh` (PR creation, release creation)

**Used by:** `prepare-release.yml`, `release.yml` (both jobs), `auto-patch-release.yml`.

**Why a GitHub App token instead of `GITHUB_TOKEN`:** `GITHUB_TOKEN` cannot trigger other
workflow runs. PRs opened with it will not run `pr-check.yaml`, which means Renovate automerge
and the `auto-patch-release` PR auto-merge would both silently fail to kick off CI.

### `publish-maven-central`

**Inputs:** `maven-central-username`, `maven-central-password`, `gpg-private-key`, `gpg-password`

Runs `./gradlew publishAndReleaseToMavenCentral -Prelease=true` with credentials wired into the
correct environment variables. The `-Prelease=true` flag activates GPG signing.

**Used by:** `release.yml` (job `release`), `auto-patch-release.yml`.

### `publish-gradle-portal`

**Inputs:** `gradle-publish-key`, `gradle-publish-secret`, `gpg-private-key`, `gpg-password`

Runs `./gradlew publishPlugins -Prelease=true` with credentials. Kept separate from
`publish-maven-central` because not every release scenario publishes to both destinations.

**Used by:** `release.yml` (job `release`), `auto-patch-release.yml`.

---

## Design Patterns and Decisions

### Rollup status check

Branch protection is configured with a single required check: the `checks` job in
`pr-check.yaml`. This job uses `if: always()` and declares all other jobs in `needs`, then fails
if any dependency resulted in `failure` or `cancelled`. Benefits:

- Adding a new CI job only requires updating `needs` in `checks` — branch protection rules never
  need to change.
- Renovate automerge and the `auto-patch-release` PR auto-merge both target this single check
  name, so they automatically respect any future jobs added to the workflow.

### Non-squash merge for release PRs

Release PRs (from `auto-patch/{version}` → `main`, and from `release/{version}` → `main`) use
regular merge commits, not squash. This ensures the tagged commit (e.g. `v3.0.1`) is present in
`main`'s commit history. A squash merge would create a new commit on `main` that the tag does not
point to, breaking `git describe` and any tooling that walks tag ancestry.

### `gradle.properties` as the minor/major version source of truth

`gradle.properties` always contains the next planned minor or major version (e.g. `3.1.0`). The
`bump-version.sh` advances it after each release. Patch releases deliberately do **not** modify
this file — they derive the patch version from the latest git tag, inject it temporarily into
`gradle.properties` for the Gradle build only, then restore the file before committing. This keeps
the two versioning tracks independent.

### Renovate schedule coordinates with patch release day

The release runs at noon UTC on the 1st. Renovate is excluded from the 1st entirely
(`* * 2-31 * *`). Any Renovate PRs created before the 1st carry the GitHub auto-merge flag; GitHub
merges them as soon as CI passes, independent of Renovate's own schedule. Together this ensures
no Renovate PRs are open by the time the patch release check runs.

---

## Adapting for Another Repository

### Portable as-is

These patterns can be copied without changes:

- The rollup `checks` job in `pr-check.yaml`
- The `setup-bot-git-user` composite action (same secrets/variables structure)
- The `setup-java` composite action pattern (swap version/distribution for other JDKs)
- The Renovate automerge + schedule exclusion approach
- The `auto-patch-release` two-job structure (check + release), open-PR guard, and git tag
  derivation logic
- The non-squash PR merge strategy for release branches

### Project-specific parts to replace

| What | Where used | Replace with |
|------|-----------|--------------|
| `./checks.sh` | `pr-check.yaml` | Your project's build/test command |
| `./gradlew -p demo-app connectedDebugAndroidTest` | `pr-check.yaml` | Your integration test command, or remove the step |
| `reactivecircus/android-emulator-runner` | `pr-check.yaml` | Remove if not an Android project |
| `sh .github/scripts/update-changelog.sh` | `prepare-release.yml`, `auto-patch-release.yml` | Your changelog generation command |
| `sh .github/scripts/bump-version.sh` | `release.yml` | Your version increment command |
| `gradle.properties` version regex | `prepare-release.yml`, `release.yml` | Path and pattern for your version file |
| `sed` README version pattern | `prepare-release.yml`, `auto-patch-release.yml` | Regex matching your README's version references |
| `v[0-9]+\.[0-9]+\.[0-9]+` tag pattern | `auto-patch-release.yml` | Adjust if your tags use a different format |
| `publish-maven-central` action | `release.yml`, `auto-patch-release.yml` | Replace or remove if not publishing to Maven Central |
| `publish-gradle-portal` action | `release.yml`, `auto-patch-release.yml` | Replace or remove if not a Gradle plugin |
| Renovate `packageRules` grouping | `renovate.json` | Your project's dependency grouping needs |

### Required secrets and variables

Regardless of the project, the following are always needed for the bot and release flows:

| Name | Purpose |
|------|---------|
| `GH_BOT_PRIVATE_KEY` | GitHub App private key (bot identity) |
| `APP_ID` (variable) | GitHub App ID |
| `GPG_PRIVATE_KEY` | Artifact signing |
| `GPG_PASSWORD` | Artifact signing |

Publishing-destination secrets (`MAVEN_CENTRAL_*`, `GRADLE_PUBLISH_*`) are only needed if those
destinations are used.
