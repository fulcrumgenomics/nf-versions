# nf-versions

[![CI](https://github.com/fulcrumgenomics/nf-versions/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/fulcrumgenomics/nf-versions/actions/workflows/test.yml?query=branch%3Amain)
[![Nextflow](https://img.shields.io/badge/Nextflow%20DSL2-%E2%89%A525.10.0-blue.svg)](https://www.nextflow.io/)
[![Java Versions](https://img.shields.io/badge/java-17_|_21-blue)](https://github.com/fulcrumgenomics/nf-versions)

Collect CLI tool version information from Nextflow processes and collate it for [MultiQC](https://multiqc.info/).

## Overview

`nf-versions` provides helpers for capturing tool version information in Nextflow workflows.

Version strings are bash `echo` commands designed for use with Nextflow's `eval` output directive.
They produce YAML-formatted lines that MultiQC can read directly.
These helpers avoid the need to continuously and verbosely specify version strings in both script and stub blocks (see [example here](https://github.com/nf-core/fastquorum/blob/e6414c99ef5eef47a4ac3f124962e3dc5c21ab4b/modules/local/fgbio/fastqtobam/main.nf#L46-L61)).
Contributions for new tool version support are greatly appreciated!

## Quickstart

Add the plugin to your Nextflow config:

```nextflow
plugins { id 'nf-versions' }
```

### Using the Version Functions in Nextflow Processes

Import the version functions and assign them at the module level before the process block.
Nextflow's process body uses delegate-only method resolution, so the functions must be called
outside the process and the resulting command strings captured in variables:

```nextflow
include { bwaMem2Version; samtoolsVersion } from 'plugin/nf-versions'

process ALIGN {
    output:
    eval({ bwaMem2Version() }), topic: "versions"
    eval({ samtoolsVersion() }), topic: "versions"

    script:
    """
    bwa-mem2 mem ... | samtools sort ...
    """
}
```

### Collating Versions for MultiQC

In your main workflow, mix the `versions` topic channel through `collateVersions()` before passing it to MultiQC:

```nextflow
include { MULTIQC } from './modules/multiqc'
include { collateVersions } from 'plugin/nf-versions'

workflow {
    // ... pipeline logic ... //

    def qc = channel.empty()
    qc = qc.mix(channel.topic("for_multiqc"))
    qc = qc.mix(collateVersions(channel.topic("versions")))

    MULTIQC(qc.collect())
}
```

The helper `collateVersions()` collates all emitted version strings into a single `all_mqc_versions.yml` file in a format that MultiQC expects.

## Custom Python Libraries and Tools

If you have a custom Python tool or library, you can collect its version with `pyPackageVersion()`:

```nextflow
include { pyPackageVersion } from 'plugin/nf-versions'

process SECRET_SAUCE {
    output:
    eval({ pyPackageVersion("secret_sauce_lib") }), topic: "versions"

    script:
    """
    secret-sauce.py ...
    """
}
```

## Supported Tools

| Plugin Function     | Tool                                                 |
| ---                 | ---                                                  |
| `bcftoolsVersion()` | [bcftools](https://samtools.github.io/bcftools/)     |
| `bwaMem2Version()`  | [bwa-mem2](https://github.com/bwa-mem2/bwa-mem2)     |
| `falcoVersion()`    | [falco](https://github.com/smithlabcode/falco)       |
| `fgbioVersion()`    | [fgbio](https://github.com/fulcrumgenomics/fgbio)    |
| `picardVersion()`   | [picard](https://broadinstitute.github.io/picard/)   |
| `samtoolsVersion()` | [samtools](https://www.htslib.org/)                  |
| `splitcodeVersion()`| [splitcode](https://github.com/salzmanlab/splitcode) |

## Made by Fulcrum Genomics

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

## Development and Testing

See the [contributing guide](./CONTRIBUTING.md) for more information.