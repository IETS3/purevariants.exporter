# Changelog

All notable changes to this project are documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Added
- Build against another pure::variants p2 update site with `-Dpurevariants.p2.url=<url>`; the target platform is split into `purevariants-5-base.target` (public dependencies) and `purevariants-5.target` (base + pure::variants).
- Integration tests run in the Maven build with `-Dpurevariants.license.server=<url>` (pure::variants floating license server); without it they are compiled but skipped.
- `CONTRIBUTING.md` (including how to run the tests in the IDE and with Maven) and `CHANGELOG.md`.

### Changed
- Licensed under the Eclipse Public License 2.0 (EPL-2.0).
- Repository prepared for open-sourcing: all bundle versions aligned, typos fixed.
- Each deploy goes to its own folder instead of overwriting `latest`: `snapshots/<version>.<timestamp>` by default, `releases/<version>` with `-Dp2.repository.stream=releases/<version>`.
- README rewritten.
- `bump_version.sh` now also updates the configuration POM and `EXPORTER_VERSION`, validates its argument, verifies the result and no longer commits; it prints a proposed commit message instead.

### Removed
- Non-functional Ant task bundle (`de.itemis.purevariants.exporter.ant`).
- Empty GUI test module and the SWTBot dependencies.

## 3.0.1 - 2025-06-11

### Changed
- Plugin jars are no longer signed, to support older Eclipse distributions that have problems with signed jars.

## 3.0.0 - 2025-03-31

### Added
- Feature model IDs are exported, so that relations can be resolved by the IETS3 importer in MPS.
- The exporter version is written to the exported files (`exporterVersion`), so that the IETS3 importer can check compatibility.
- Examples for restrictions and relations in the `AcceptanceTestModel` example project.

## 2.0.0 - 2024-10-21

### Added
- Export of all feature and variant models of a selected pure::variants project into two files (`<project>_FeatureModels_iets3.xml`, `<project>_VariantModels_iets3.xml`).
- Export of feature attributes (including ranges and invisible attributes), constraints, restrictions and relations.
- Export of instances and sub-models, including the defining models of variant references.
- Example wizard with the `AcceptanceTestModel` example project.

### Changed
- Based on pure::variants 5.0.14 and Eclipse 2022-03 (4.23), Java 11.
- Build updated to a newer Tycho version and JUnit 5; development on macOS is supported.
- Integration and GUI tests are disabled in the Maven build, because the pure::variants license cannot be set up there.

### Fixed
- Properties with list values are exported correctly.

## 1.0.0 - 2022-06-22

### Added
- Initial release: export of pure::variants models to XML files for use with IETS3.Variability in MPS.
