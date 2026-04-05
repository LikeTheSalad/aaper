# github-tools: Shared CI/CD Repository Plan

A new repository (`github-tools`) will be the single source of truth for GitHub Actions workflows
and composite actions shared across `aaper`, `android-stem`, and `asmifier`. Each consuming repo
keeps only thin, declarative workflow files that delegate to the shared repo.

---

## Key observations across the three repos

| | aaper (branch) | android-stem | asmifier |
|---|---|---|---|
| Java version | 21 | 17 | 21 |
| Checks command | `./checks.sh` | `./check.sh` | `./gradlew check` |
| OS matrix | ubuntu only | ubuntu + windows | ubuntu + windows |
| Instrumentation tests | yes (Android emulator) | no | no |
| Release trigger | automated (schedule + labels) | manual dispatch | manual dispatch |
| Publish target | Maven Central + Gradle Portal | Gradle Portal only | Gradle Portal only |
| Bot token for git/PRs | GitHub App (GH_BOT_PRIVATE_KEY) | GitHub App (PRIVATE_KEY) | GitHub App (PRIVATE_KEY) |
| Changelog | generate-changelog.sh (PR API) | `./gradlew changelogUpdate` | `./gradlew changelogUpdate` |

All three repos will be migrated to the new automated release flow (schedule + PR labels), replacing
their manual `workflow_dispatch` workflows. The old `./gradlew changelogUpdate` and
`./gradlew versionBump` Gradle tasks are no longer needed once the shared scripts handle those
responsibilities.

---

## New repository structure

```
~/Repos/github-tools/
├── README.md                            # Setup guide + usage reference (replaces aaper's SETUP.md)
└── .github/
    ├── actions/
    │   ├── setup-java/                  # Installs JDK (configurable version)
    │   ├── setup-bot-git-user/          # Creates App token + configures git identity
    │   ├── generate-changelog/          # Generates + inserts CHANGELOG section from merged PRs
    │   │   ├── action.yml
    │   │   └── generate-changelog.sh    # Moved from aaper's .github/scripts/
    │   ├── publish-maven-central/       # Publishes all modules to Maven Central
    │   └── publish-gradle-portal/       # Publishes plugin to Gradle Plugin Portal
    └── workflows/
        ├── pr-check.yml                 # Reusable: runs CI on every PR
        ├── release-prepare.yml          # Reusable: computes version, creates branches and PR
        └── release-publish.yml          # Reusable: publishes after pre-release PR is merged
```

> **Why `generate-changelog` is a composite action and not a plain script:** reusable workflow
> `run:` steps execute in the calling repo's workspace. The only way to ship a script with a shared
> workflow and call it reliably is to wrap it in a composite action, which exposes
> `$GITHUB_ACTION_PATH` pointing to the fetched action directory.

---

## Composite actions

All composite actions are called with `uses: LikeTheSalad/github-tools/.github/actions/<name>@main`.

### `setup-java`

| Input | Default | Description |
|-------|---------|-------------|
| `java-version` | `21` | JDK version to install (Temurin distribution) |

### `setup-bot-git-user`

| Input | Required | Description |
|-------|----------|-------------|
| `app-id` | yes | GitHub App ID (from `vars.APP_ID`) |
| `private-key` | yes | GitHub App private key (from `secrets.GH_BOT_PRIVATE_KEY`) |

**Output:** `token` — installation token used for authenticated checkouts and `gh` calls.

### `generate-changelog`

Queries merged PRs since the latest semver tag and inserts a new `## Version X.Y.Z` section into
`CHANGELOG.md` after the `<!-- CHANGELOG_INSERT -->` marker. The script is bundled alongside
`action.yml` and invoked via `$GITHUB_ACTION_PATH`.

| Input | Default | Description |
|-------|---------|-------------|
| `version` | required | The new version string, e.g. `3.2.0` |
| `token` | required | GitHub token for `gh pr list` calls |
| `changelog-file` | `CHANGELOG.md` | Path to the changelog |

### `publish-maven-central`

| Input | Required | Description |
|-------|----------|-------------|
| `maven-central-username` | yes | |
| `maven-central-password` | yes | |
| `gpg-private-key` | yes | |
| `gpg-password` | yes | |

### `publish-gradle-portal`

| Input | Required | Description |
|-------|----------|-------------|
| `gradle-publish-key` | yes | |
| `gradle-publish-secret` | yes | |
| `gpg-private-key` | yes | |
| `gpg-password` | yes | |

---

## Reusable workflows

All reusable workflows are called with
`uses: LikeTheSalad/github-tools/.github/workflows/<name>@main`.

Consuming workflows pass all secrets with `secrets: inherit` — no secrets are declared by name in
the thin wrappers.

### `pr-check.yml`

Runs the project's verification suite, optionally on Windows, and optionally boots an Android
emulator for instrumentation tests.

| Input | Type | Default | Description |
|-------|------|---------|-------------|
| `java-version` | string | `21` | JDK version |
| `checks-command` | string | `./gradlew check` | Command to run as the main CI step |
| `run-on-windows` | boolean | `false` | Whether to also run on `windows-latest` |
| `run-instrumentation-tests` | boolean | `false` | Whether to boot an emulator and run instrumentation tests |
| `instrumentation-test-command` | string | `./gradlew connectedDebugAndroidTest` | Command for instrumentation tests |
| `instrumentation-api-level` | number | `33` | Android API level for the emulator |

**Rollup note:** The calling job (named `checks` in each thin wrapper) serves as the rollup. If any
job inside the reusable workflow fails, the calling `checks` job fails. Branch protection in each
consuming repo registers `checks` as the single required status check.

### `release-prepare.yml`

Calculates the next version from merged PR labels, creates `release/{version}` and
`pre-release/{version}` branches, updates `gradle.properties`, `CHANGELOG.md`, and optionally
`README.md`, then opens a PR for human review.

| Input | Type | Default | Description |
|-------|------|---------|-------------|
| `java-version` | string | `21` | JDK version |
| `app-id` | string | required | GitHub App ID (pass `vars.APP_ID`) |
| `version-override` | choice (`''`/`patch`/`minor`/`major`) | `''` | Force bump type; empty = auto-detect from labels |
| `readme-version-sed` | string | `''` | `sed` expression to update version in README (empty = skip). Use `{version}` as the version placeholder |
| `changelog-file` | string | `CHANGELOG.md` | Path to the changelog |

### `release-publish.yml`

Triggered when a `pre-release/**` PR is merged into `release/**`. Publishes artifacts, creates the
git tag and GitHub Release (with changelog body), then opens `release/{version}` → `main` with
auto-merge.

| Input | Type | Default | Description |
|-------|------|---------|-------------|
| `java-version` | string | `21` | JDK version |
| `app-id` | string | required | GitHub App ID (pass `vars.APP_ID`) |
| `publish-to-maven-central` | boolean | `false` | Whether to publish to Maven Central |
| `publish-to-gradle-portal` | boolean | `false` | Whether to publish to Gradle Plugin Portal |

---

## How consuming repos look after migration

### aaper

```
.github/
└── workflows/
    ├── pr-check.yaml
    ├── release-prepare.yml
    └── release-publish.yml
```

No local `actions/` or `scripts/` directories.

**`pr-check.yaml`:**
```yaml
name: PR Check
on:
  pull_request:
  workflow_dispatch:
jobs:
  checks:
    uses: LikeTheSalad/github-tools/.github/workflows/pr-check.yml@main
    with:
      checks-command: ./checks.sh
      run-instrumentation-tests: true
    secrets: inherit
```

**`release-prepare.yml`:**
```yaml
name: Release — Prepare
on:
  schedule:
    - cron: '0 12 1 * *'
  workflow_dispatch:
    inputs:
      version_override:
        description: 'Force version bump type'
        type: choice
        options: ['', 'patch', 'minor', 'major']
        default: ''
jobs:
  release:
    uses: LikeTheSalad/github-tools/.github/workflows/release-prepare.yml@main
    with:
      app-id: ${{ vars.APP_ID }}
      version-override: ${{ inputs.version_override }}
      readme-version-sed: 's/id("com.likethesalad.aaper") version "[0-9]\+\.[0-9]\+\.[0-9]\+"/id("com.likethesalad.aaper") version "{version}"/g'
    secrets: inherit
```

**`release-publish.yml`:**
```yaml
name: Release — Publish
on:
  pull_request:
    types: [closed]
    branches:
      - 'release/**'
jobs:
  release:
    uses: LikeTheSalad/github-tools/.github/workflows/release-publish.yml@main
    with:
      app-id: ${{ vars.APP_ID }}
      publish-to-maven-central: true
      publish-to-gradle-portal: true
    secrets: inherit
```

### android-stem

**`pr-check.yaml`:**
```yaml
name: PR Check
on:
  pull_request:
  workflow_dispatch:
jobs:
  checks:
    uses: LikeTheSalad/github-tools/.github/workflows/pr-check.yml@main
    with:
      java-version: '17'
      checks-command: ./check.sh
      run-on-windows: true
    secrets: inherit
```

**`release-prepare.yml`** and **`release-publish.yml`**: same pattern as aaper with
`publish-to-gradle-portal: true` and no `readme-version-sed` (unless android-stem has version
references in its README).

### asmifier

Same as android-stem except `java-version` defaults to `21` so no override needed. The only
difference is `publish-to-gradle-portal: true` and no Maven Central.

---

## Migration steps per repo

### github-tools (new repo)

1. Create `~/Repos/github-tools`, initialize git, create initial commit.
2. Copy composite actions from `aaper/.github/actions/` into `github-tools/.github/actions/`,
   parameterizing `java-version` in `setup-java`.
3. Create `generate-changelog` composite action: move `generate-changelog.sh` from
   `aaper/.github/scripts/` into `.github/actions/generate-changelog/`, write `action.yml` to
   invoke it via `$GITHUB_ACTION_PATH`.
4. Write the three reusable workflows, migrating logic from `aaper`'s current workflows.
5. Write `README.md` covering setup requirements (GitHub App, GPG key, secrets, branch
   protection) and usage examples for each reusable workflow.
6. Push to GitHub as `LikeTheSalad/github-tools` (public repo).

### aaper

1. Delete `.github/actions/` and `.github/scripts/`.
2. Replace the three workflow files with the thin wrappers shown above.
3. Keep `CHANGELOG.md`, `SETUP.md`, `ARCHITECTURE.md` as-is (SETUP.md content moves to
   github-tools README but can stay here for reference).

### android-stem and asmifier

Both repos have the same migration steps (both already use a GitHub App with `secrets.PRIVATE_KEY`
and `vars.APP_ID`):

1. Add `<!-- CHANGELOG_INSERT -->` marker to `CHANGELOG.md` (insert above the first `## Version`
   heading).
2. Rename the bot secret from `PRIVATE_KEY` to `GH_BOT_PRIVATE_KEY` in each repo's Actions
   secrets (Settings → Secrets and variables → Actions).
3. Replace the three workflow files with thin wrappers (pr-check, release-prepare, release-publish).
4. Update `renovate.json`: add `"automerge": true`, `"automergeStrategy": "squash"`,
   `"schedule": ["* * 2-31 * *"]`.
5. Add branch protection on `release/**` (require `checks` status check).

---

## Implementation order

1. **Create `github-tools` repo** — directory, git init, GitHub remote.
2. **Port composite actions** — copy + generalize from aaper.
3. **Create `generate-changelog` composite action** — bundle script + action.yml.
4. **Write reusable workflows** — port logic from aaper's current workflows.
5. **Write `README.md`** for github-tools.
6. **Migrate aaper** — delete local actions/scripts, replace with thin wrappers.
7. **Migrate android-stem** — CHANGELOG marker, secret rename, thin wrappers, renovate update.
8. **Migrate asmifier** — CHANGELOG marker, add bot, thin wrappers, renovate update.
