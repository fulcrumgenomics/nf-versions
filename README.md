# nf-versions

[![CI](https://github.com/fulcrumgenomics/nf-versions/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/fulcrumgenomics/nf-versions/actions/workflows/test.yml?query=branch%3Amain)
[![Nextflow](https://img.shields.io/badge/Nextflow%20DSL2-%E2%89%A525.10.0-blue.svg)](https://www.nextflow.io/)
[![Java Versions](https://img.shields.io/badge/java-17_|_21-blue)](https://github.com/fulcrumgenomics/nf-versions)

Collect CLI tool version information from Nextflow processes and collate it for [MultiQC](https://multiqc.info/).

<a href="https://fulcrumgenomics.com">
  <img src=".github/logos/fulcrumgenomics.svg" alt="Fulcrum Genomics" height="100"/>
</a>

[Visit us at Fulcrum Genomics](https://www.fulcrumgenomics.com) to learn more about how we can power your bioinformatics with nf-versions and beyond.

<a href="mailto:contact@fulcrumgenomics.com?subject=[GitHub Inquiry]">
  <img src="https://img.shields.io/badge/Email_us-brightgreen.svg?&style=for-the-badge&logo=gmail&logoColor=white"/>
</a>
<a href="https://www.fulcrumgenomics.com">
  <img src="https://img.shields.io/badge/Visit_Us-blue.svg?&style=for-the-badge&logo=wordpress&logoColor=white"/>
</a>

## Overview

`nf-versions` provides helpers for capturing tool version information in Nextflow workflows.

Version strings are bash `echo` commands designed for use with Nextflow's `eval` output directive.
They produce YAML-formatted lines that MultiQC can read directly.

## Quickstart

Add the plugin to your Nextflow config:

```nextflow
plugins { id 'nf-versions' }
```

### Using `VersionFor` in Nextflow Processes

Once the plugin is declared, the `VersionFor` class is available for use.
Use the constants on `VersionFor` directly in `eval` output directives and if you send their outputs to topic channels, you can collate them later:

```nextflow
process ALIGN {
    output:
    eval(VersionFor.BwaMem2), topic: "versions"
    eval(VersionFor.Samtools), topic: "versions"

    script:
    """
    bwa-mem2 mem ... | samtools sort ...
    """
}
```

### Collating Versions for MultiQC

In your main workflow, mix the `versions` topic channel through `VersionFor.all()` before passing it to MultiQC:

```nextflow
include { MULTIQC } from './modules/multiqc'

workflow {
    // ... pipeline logic ... //

    def qc = channel.empty()
    qc = qc.mix(channel.topic("for_multiqc"))
    qc = qc.mix(VersionFor.all(channel.topic("versions")))

    MULTIQC(qc.collect())
}
```

The helper `VersionFor.all()` collates all emitted version strings into a single `all_mqc_versions.yml` file in a format that MultiQC expects.

## Supported Tools

| `VersionFor` Helper    | Tool                                                 |
| ---                    | ---                                                  |
| `VersionFor.Bcftools`  | [bcftools](https://samtools.github.io/bcftools/)     |
| `VersionFor.BwaMem2`   | [bwa-mem2](https://github.com/bwa-mem2/bwa-mem2)     |
| `VersionFor.Fgbio`     | [fgbio](https://github.com/fulcrumgenomics/fgbio)    |
| `VersionFor.Falco`     | [falco](https://github.com/smithlabcode/falco)       |
| `VersionFor.Picard`    | [picard](https://broadinstitute.github.io/picard/)   |
| `VersionFor.Samtools`  | [samtools](https://www.htslib.org/)                  |
| `VersionFor.Splitcode` | [splitcode](https://github.com/salzmanlab/splitcode) |

## Testing the Plugin Locally

Execute the following to compile and run unit tests for the plugin:

```
make test
```

To install the plugin for use in local workflows (_e.g._ not internet connected), execute the following:

```
make install
```

## Using the Plugin Locally

Test your changes to the plugin on a Nextflow script like:

```bash
nextflow/launch.sh run <script.nf> -plugins nf-versions@#.#.#-dev
```

## Publishing to GitHub

Read and follow the official documentation on setting up your environment for [plugin release](https://nextflow.io/docs/latest/guides/gradle-plugin.html#publishing-a-plugin).

After bumping the version of the plugin in the file [`build.gradle`](./build.gradle) and making a GitHub Release, execute the following:

```
make release
```
