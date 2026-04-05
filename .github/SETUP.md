# Repository Setup Guide

Steps required when setting up the CI/CD workflows in a new GitHub remote repository.

---

## 1. Create and install a GitHub App (bot account)

The release workflows (`release-prepare.yml`, `release-publish.yml`) use a GitHub App token to
create branches, push commits, and open PRs. Using an App token instead of `GITHUB_TOKEN` allows
the bot's PRs to trigger `pr-check.yaml` (the default `GITHUB_TOKEN` does not).

1. Go to **GitHub → Settings → Developer settings → GitHub Apps → New GitHub App**.
2. Fill in the required fields (name, homepage URL). Disable the webhook.
3. Set the following **repository permissions**:
    - Contents: **Read and write**
    - Pull requests: **Read and write**
    - Metadata: **Read-only** (mandatory)
4. Click **Create GitHub App**.
5. Note the **App ID** shown on the app's settings page — you will need it in step 3.
6. Scroll to **Private keys** and click **Generate a private key**. Save the downloaded `.pem`
   file — you will need it in step 3.
7. From the app's settings page, click **Install App** and install it on the repository.

---

## 2. Create a GPG key for artifact signing

Maven Central and the Gradle Plugin Portal require artifacts to be signed.

1. Generate a key:
   ```bash
   gpg --gen-key
   ```
2. Publish the public key to a key server:
   ```bash
   gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
   ```
3. Export the private key in armored format (this becomes the `GPG_PRIVATE_KEY` secret):
   ```bash
   gpg --armor --export-secret-keys <KEY_ID>
   ```

---

## 3. Add repository secrets and variables

Go to **Settings → Secrets and variables → Actions**.

### Secrets

| Name                     | Value                                                |
|--------------------------|------------------------------------------------------|
| `GH_BOT_PRIVATE_KEY`     | Full contents of the `.pem` file generated in step 1 |
| `GPG_PRIVATE_KEY`        | Armored private key exported in step 2               |
| `GPG_PASSWORD`           | Passphrase chosen when generating the GPG key        |
| `MAVEN_CENTRAL_USERNAME` | Maven Central user token (see step 4)                |
| `MAVEN_CENTRAL_PASSWORD` | Maven Central token password (see step 4)            |
| `GRADLE_PUBLISH_KEY`     | Gradle Plugin Portal API key (see step 5)            |
| `GRADLE_PUBLISH_SECRET`  | Gradle Plugin Portal API secret (see step 5)         |

### Variables

| Name     | Value                      |
|----------|----------------------------|
| `APP_ID` | The App ID noted in step 1 |

---

## 4. Set up Maven Central

1. Create an account at [central.sonatype.org](https://central.sonatype.org).
2. Register and verify the namespace `com.likethesalad.android`.
3. Go to **Account → Generate User Token** and use the generated username and password as
   `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_PASSWORD`.

---

## 5. Set up the Gradle Plugin Portal

1. Create an account at [plugins.gradle.org](https://plugins.gradle.org).
2. Ensure the plugin ID `com.likethesalad.aaper` is available / transferred to your account.
3. Go to **Profile → API Keys** and generate a key. Use the key and secret as `GRADLE_PUBLISH_KEY`
   and `GRADLE_PUBLISH_SECRET`.

---

## 6. Configure repository settings

### Allow auto-merge

`release-publish.yml` opens the final `release/**` → `main` PR and enables auto-merge on it so the
released commit lands on `main` automatically once checks pass.

Go to **Settings → General → Pull Requests** and check **Allow auto-merge**.

### Allow squash merging

Renovate is configured with `"automergeStrategy": "squash"` so that each dependency update lands
on `main` as a single clean commit. Without this setting enabled, Renovate's merge request will
fail.

Go to **Settings → General → Pull Requests** and check **Allow squash merging**.

> The release PR created by `release-publish.yml` uses `gh pr merge --auto --merge`
> (explicit `--merge` flag), so it is unaffected by the squash default.

### Branch protection on `main`

For auto-merge to wait for CI before merging (rather than merging immediately), `main` must have at
least one required status check.

Go to **Settings → Branches → Add branch protection rule** for `main`:

- [x] Require status checks to pass before merging
    - Add **`checks`** (the rollup job in `pr-check.yaml`) as a required check
- [x] Require branches to be up to date before merging

`checks` is a rollup job that depends on all other jobs in `pr-check.yaml` and always reports a
result, even when dependencies fail. Using it as the single required check means the branch rule
never needs to change when jobs are added or renamed in the workflow.

This also ensures Renovate PRs (which use the same auto-merge mechanism) are not merged until CI
passes.

### Branch protection on `release/**`

The pre-release PR (`pre-release/{version}` → `release/{version}`) must also wait for CI before a
human merges it, because this PR is the explicit human approval gate and merging it is what
triggers `release-publish.yml`.

Go to **Settings → Branches → Add branch protection rule** for `release/**`:

- [x] Require status checks to pass before merging
    - Add **`checks`** as a required check
- [x] Require branches to be up to date before merging

Using the same `checks` rollup job keeps the protection rule stable even if `pr-check.yaml` gains
more jobs later.
