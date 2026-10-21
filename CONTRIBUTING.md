# Contributing to purevariants.exporter

Contributions are welcome. Please open an issue to discuss larger changes before
you start, and submit your changes as a pull request against `main`.

## Prerequisites

- **JDK 11 or newer** for the Maven build (tested with JDK 17). The bundles are
  compiled for Java 11 (`Bundle-RequiredExecutionEnvironment: JavaSE-11`), independent
  of the JDK used for the build.
- **Java 11 or newer** to run the plugin in pure::variants.
- For development in the Eclipse IDE: an IDE that runs on its own required Java
  version (current Eclipse releases need Java 17); see *Setting up the Eclipse IDE*.
- Maven 3.9 or newer
- Access to a p2 repository that provides the pure::variants SDK, see below.

### pure::variants SDK

pure::variants is commercial software by pure-systems GmbH. Its SDK is not publicly
available, so the build cannot resolve it from a public repository.

The target platform consists of two files in `releng/de.itemis.purevariants.exporter.target/`:

- `purevariants-5-base.target`: all public dependencies (Eclipse, Orbit, JAXB, test libraries).
- `purevariants-5.target`: references the base target and adds pure::variants
  **5.0.14.685** from a p2 repository hosted by itemis, which requires a login.
  Credentials go into `~/.m2/settings.xml` as a `<server>` with the id `artifacts.itemis`.
  This is the complete target platform, used by default and for development in the IDE.

If you have access to another p2 update site of pure::variants (e.g. from
pure-systems), pass its URL on the command line:

```sh
mvn clean verify -Dpurevariants.p2.url=https://<your pure::variants update site>
```

This activates the Maven profile `custom-purevariants-repository`, which uses only
`purevariants-5-base.target` and resolves pure::variants from the given URL, so no
itemis login is needed. If that site requires a login, add its credentials to
`~/.m2/settings.xml` with the server id `artifacts.itemis`.

## Building

```sh
mvn clean verify
```

This compiles all bundles, runs the unit tests and builds the p2 update site in
`releng/de.itemis.purevariants.exporter.update/target/repository`.

Tip: When building with a JDK newer than 11, Tycho warns `No system packages found
in profile nor toolchain for JavaSE-11` and checks the code against the API of the
build JDK. To check strictly against the Java 11 API, register a JDK 11 in
`~/.m2/toolchains.xml` as a [Maven toolchain](https://maven.apache.org/guides/mini/guide-using-toolchains.html).

## Testing

There are two test projects in `tests/`:

| Project | Kind | Maven build | Eclipse IDE |
|---|---|---|---|
| `de.itemis.purevariants.exporter.exporter.test` | Unit tests (fragment of the exporter bundle) | runs | JUnit Test |
| `de.itemis.purevariants.exporter.exporter.it.test` | Integration tests | only with a license server | JUnit Plug-in Test |

### Running the integration tests with Maven

The integration tests need a licensed pure::variants, so they are skipped by
default. To run them, pass the URL of a pure::variants floating license server:

```sh
mvn clean verify -Dpurevariants.license.server=https://<license server>:443
```

This activates the Maven profile `integration-tests`. The license server is passed
to pure::variants as a default preference via
`tests/de.itemis.purevariants.exporter.exporter.it.test/license/plugin_customization.ini`.
The test runtime contains the complete pure::variants feature, including its local
server for the current operating system.

If the license server is wrong or not reachable, pure::variants does not load any
model and the tests fail with `NullPointerException`s on the exported model.

### Setting up the Eclipse IDE

1. Use an Eclipse IDE with the Plug-in Development Environment (PDE), e.g. the
   *Eclipse IDE for RCP and RAP Developers*, running on Java 17 or newer (required
   by current Eclipse IDE releases; the project itself targets Java 11).
2. Import the projects with **File > Import > General > Existing Projects into
   Workspace**, select the repository root and check **Search for nested
   projects**. The projects already contain their Eclipse configuration
   (`.project`, `.classpath`), so no Maven import is needed.
3. Open `releng/de.itemis.purevariants.exporter.target/purevariants-5.target`, wait
   until it is resolved (enter your credentials for the pure::variants p2 repository
   when asked) and click **Set as Active Target Platform**.
4. Check that the **Problems** view shows no errors, in particular none in
   `META-INF/MANIFEST.MF` of the exporter bundle.

### Running the unit tests

Right-click `de.itemis.purevariants.exporter.exporter.test` and select
**Run As > JUnit Test**.

### Running the integration tests

1. Right-click `de.itemis.purevariants.exporter.exporter.it.test` and select
   **Run As > JUnit Plug-in Test**. This creates a launch configuration; the first
   run is expected to fail.
2. Open **Run > Run Configurations…**, select the new configuration under
   *JUnit Plug-in Test* and change it as follows:

   | Tab | Setting | Value |
   |---|---|---|
   | Test | Test runner | JUnit 5 |
   | Main | Location | keep the default `${workspace_loc}/../junit-workspace` |
   | Main | Clear | **unchecked**, otherwise the license setting (see below) is deleted before every run |
   | Main | Program to run | Run an application: `[No Application] - Headless Mode` |
   | Arguments | Working directory | Other: `${workspace_loc:de.itemis.purevariants.exporter.exporter.it.test}`, because the tests load their test models from the relative path `resources/` |
   | Plug-ins | Launch with | all workspace and enabled target plug-ins |

3. On the **Plug-ins** tab, click **Validate Plug-ins**. It must not report any
   problems.
4. Set up the pure::variants license, see below.
5. Click **Run**.

### Setting up the pure::variants license

The integration tests run in their own workspace (the *Location* of the launch
configuration, by default `junit-workspace` next to your IDE workspace).
pure::variants must be licensed in that workspace. The license setting is a
workspace preference, so it needs to be set only once, as long as **Clear** stays
unchecked.

Use one of the following ways to configure a floating license server:

- **Edit the preference file.** Create or edit the following file in the test
  workspace:

  ```
  junit-workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings/com.ps.consul.eclipse.ui.float.prefs
  ```

  with this content (keep the `\:` escapes):

  ```properties
  eclipse.preferences.version=1
  licenseServerLocation=https\://<license server>\:443
  ```

- **Use the pure::variants preferences.** Create a launch configuration under
  **Run > Run Configurations… > Eclipse Application** with the same *Location* as
  the integration test configuration, **Clear** unchecked, *Run a product*
  `org.eclipse.platform.ide` and *all workspace and enabled target plug-ins*. Run
  it, open **Preferences > pure::variants** in the started Eclipse, enter the
  license server and close that Eclipse again. Do not run it at the same time as
  the tests, because both use the same workspace.

If pure::variants asks for a license server while the tests run, the configured
server is wrong or not reachable.

## Deploying

`mvn deploy` uploads the update site to the remote p2 repository configured in
`releng/de.itemis.purevariants.exporter.update/pom.xml`. Every deploy goes to its
own folder, so existing update sites are never overwritten:

| Command | Target folder |
|---|---|
| `mvn clean deploy` | `purevariants.exporter/snapshots/<version>.<timestamp>`, e.g. `snapshots/3.0.1.202610151542` |
| `mvn clean deploy -Dp2.repository.stream=releases/<version>` | `purevariants.exporter/releases/<version>`, e.g. `releases/3.0.2` |

Use the `releases/<version>` form only once per release.

### Test deploy to a local folder

To try out the deploy step without touching the remote repository, override the
target URL and the transport on the command line:

```sh
mvn clean deploy -Drepository.url=file:///tmp/p2 -Dprotocol=wagon-file
```

The update site is then written to `/tmp/p2/purevariants.exporter/snapshots/<version>.<timestamp>`.

## Versioning and changelog

- Record every user-visible change in the `Unreleased` section of
  [CHANGELOG.md](CHANGELOG.md).
- To bump the version, run `./bump_version.sh <version>` (e.g. `3.0.2`). It updates
  all POMs (including the configuration POM), manifests, features, the update site
  category and the exporter version written into exported files (`EXPORTER_VERSION`).
  It does not commit; on success it prints a proposed commit message. Update the
  version history in [README.md](README.md) manually.
