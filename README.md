# purevariants.exporter

An Eclipse plugin for [pure::variants](https://www.pure-systems.com/) that exports
feature models and variant models as XML files. The exported files are the input for
the integration of pure::variants variability into **IETS3.Variability** in
[JetBrains MPS](https://www.jetbrains.com/mps/).

## Features

The exporter writes all models of a selected pure::variants project into two files:

| File | Content |
|---|---|
| `<project>_FeatureModels_iets3.xml` | all feature models: features with ID, name, type, variation type (mandatory, optional, alternative, or) and hierarchy, attributes (type, range, default value, fixed/inheritable), constraints, restrictions and relations |
| `<project>_VariantModels_iets3.xml` | all variant models: selection state of each feature and how it was selected (by the user or by the solver), attribute values as resolved for the variant |

- The model hierarchy is preserved, including **sub-models** and **instances**.
- Compared to the built-in CSV and XML exports of pure::variants, the format is
  simpler and contains exactly the information needed by IETS3.
- Both files carry the exporter version (`exporterVersion`), so that the importer in
  IETS3 can check that it supports the exported format.

An example of the generated files is available in
[`bundles/de.itemis.purevariants.exporter.examples/projects/AcceptanceTestModel/exporter_output`](bundles/de.itemis.purevariants.exporter.examples/projects/AcceptanceTestModel/exporter_output).

## Requirements

- pure::variants 5 (developed and tested with pure::variants Enterprise 5.0.14) with a
  valid license
- Eclipse 2022-03 (4.23) or a pure::variants distribution based on it
- Java 11 or newer

## Installation

The plugin is installed from a p2 update site like any other Eclipse plugin:

1. In pure::variants / Eclipse, open **Help > Install New Software...**
2. Click **Add...** and select the update site, either
   - **Archive...** for a downloaded update site ZIP, or
   - **Local...** for an update site you built yourself (see [Building from source](#building-from-source)).
3. Select **IETS3 Exporter** and proceed with **Next**.
4. Accept the license (EPL-2.0).
5. The plugin jars are not signed. Confirm the security warning to continue the installation.
6. Restart pure::variants.

## Usage

1. Click the **IETS3** button (tooltip "iets3 export") in the main toolbar.
2. In the folder selection dialog, select the folder of the pure::variants project to export.
3. The exporter writes both XML files into the folder `exporter_output` inside the
   selected project and shows "Export completed." when it is done.

### Example project

The plugin contains an example project with feature models, variant models,
sub-models, instances, constraints, restrictions and relations. To create it, use
**File > New > Example... > IETS3 Examples > Example Model for Acceptance Test**.

## Building from source

The build uses Maven and Eclipse Tycho:

```sh
mvn clean verify
```

The update site is written to `releng/de.itemis.purevariants.exporter.update/target/repository`.
The build needs access to a p2 update site of the pure::variants SDK, which is not
publicly available. See [CONTRIBUTING.md](CONTRIBUTING.md) for the prerequisites,
how to use your own pure::variants update site, and how to run the tests.

## Contributing and support

Bug reports, questions and pull requests are welcome. Please use the issue tracker of
this repository. See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## Authors

purevariants.exporter was originally developed by
[Marcio Oliveira](https://github.com/mfsoliveira) at [itemis AG](https://www.itemis.com/).

## Version History

* 3.0.1
    * Plugin jars are no longer signed
* 3.0.0
    * Export feature model IDs and the exporter version for compatibility checks in IETS3
* 2.0.0
    * Export all Feature and Variant Models from a selected pure::variants project
    * Export attributes, constraints, restrictions, relations, instances and sub-models
    * Based on pure::variants 5.0.14 and Eclipse 2022-03
* 1.0.0
    * Initial Release: Exports pure::variants models to XML files

See [CHANGELOG.md](CHANGELOG.md) for details.

## License

Copyright (c) 2022-2026 itemis AG and others.

This program and the accompanying materials are made available under the terms of the
[Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0/), see [LICENSE](LICENSE).

SPDX-License-Identifier: EPL-2.0

The bundled JAXB and Jakarta Activation libraries are licensed under the
[Eclipse Distribution License 1.0](https://www.eclipse.org/org/documents/edl-v10.php) (BSD-3-Clause).

pure::variants is a trademark of pure-systems GmbH. pure::variants is a commercial
product and is not part of this project. Eclipse is a trademark of the Eclipse Foundation.
