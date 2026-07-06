package com.fulcrumgenomics.nextflow.plugin

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import nextflow.Session
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint

/** An extension to easily collect CLI tool version information in Nextflow processes. */
@CompileStatic
class VersionsExtension extends PluginExtensionPoint {

    private static final String Bcftools = """
        echo 'bcftools: "'\$( bcftools --version | head -n1 | sed -e 's/bcftools //g' )'"'
    """.stripIndent()

    private static final String Bedtools = """
        echo 'bedtools: "'\$( bedtools --version | sed -e 's/bedtools v//g' )'"'
    """.stripIndent()

    private static final String Bwa = """
        echo 'bwa: "'\$( bwa 2>&1 | sed -n 's/^Version: //p' )'"'
    """.stripIndent()

    private static final String BwaMem2 = """
        echo 'bwa-mem2: "'\$( bwa-mem2 version )'"'
    """.stripIndent()

    private static final String Falco = """
        echo 'falco: "'\$( falco --version | sed -e 's/falco //g' )'"'
    """.stripIndent()

    private static final String Fastp = """
        echo 'fastp: "'\$( fastp --version 2>&1 | sed -e 's/fastp //g' )'"'
    """.stripIndent()

    private static final String Fastqc = """
        echo 'fastqc: "'\$( fastqc --version | sed -e 's/FastQC v//g' )'"'
    """.stripIndent()

    private static final String FastqcRs = """
        echo 'fastqc-rs: "'\$( fqc --version 2>&1 | sed -e 's/fastqc-rs //g' )'"'
    """.stripIndent()

    private static final String Fgbio = """
        echo 'fgbio: "'\$(
            fgbio --version 2>&1 \
            | tr -d '[:cntrl:]' \
            | sed -e 's/^.*Version: //' -e 's/\\[.*\$//'
        )'"'
    """.stripIndent()

    private static final String Mosdepth = """
        echo 'mosdepth: "'\$( mosdepth --version | sed -e 's/mosdepth //g' )'"'
    """.stripIndent()

    private static final String Picard = """
        echo 'picard: "'\$( picard ViewSam --version true 2>&1 | grep -v 'cannot change locale' | sed -e 's/Version://g' )'"'
    """.stripIndent()

    private static final String Revtag = """
        echo 'revtag: "'\$( revtag --version | sed -e 's/revtag //g' )'"'
    """.stripIndent()

    private static final String Sambamba = """
        echo 'sambamba: "'\$( sambamba --version 2>&1 | sed -n 's/^sambamba //p' | head -n1 )'"'
    """.stripIndent()

    private static final String Samtools = """
        echo 'samtools: "'\$( samtools --version | head -n1 | sed -e 's/samtools //g' )'"'
    """.stripIndent()

    private static final String Splitcode = """
        echo 'splitcode: "'\$( splitcode --version | sed -e 's/splitcode, version //g' | sed 's/\\.\$//' )'"'
    """.stripIndent()

    @Override
    protected void init(Session session) { }

    /** Bash command to return the version of bcftools. */
    @Function
    String bcftoolsVersion() { return Bcftools }

    /** Bash command to return the version of bedtools. */
    @Function
    String bedtoolsVersion() { return Bedtools }

    /** Bash command to return the version of bwa. */
    @Function
    String bwaVersion() { return Bwa }

    /** Bash command to return the version of bwa-mem2. */
    @Function
    String bwaMem2Version() { return BwaMem2 }

    /** Bash command to return the version of falco. */
    @Function
    String falcoVersion() { return Falco }

    /** Bash command to return the version of fastp. */
    @Function
    String fastpVersion() { return Fastp }

    /** Bash command to return the version of fastqc. */
    @Function
    String fastqcVersion() { return Fastqc }

    /** Bash command to return the version of fastqc-rs. */
    @Function
    String fastqcRsVersion() { return FastqcRs }

    /** Bash command to return the version of fgbio. */
    @Function
    String fgbioVersion() { return Fgbio }

    /** Bash command to return the version of mosdepth. */
    @Function
    String mosdepthVersion() { return Mosdepth }

    /** Bash command to return the version of picard. */
    @Function
    String picardVersion() { return Picard }

    /** Bash command to return the version of revtag. */
    @Function
    String revtagVersion() { return Revtag }

    /** Bash command to return the version of sambamba. */
    @Function
    String sambambaVersion() { return Sambamba }

    /** Bash command to return the version of samtools. */
    @Function
    String samtoolsVersion() { return Samtools }

    /** Bash command to return the version of splitcode. */
    @Function
    String splitcodeVersion() { return Splitcode }

    /**
     * Returns a bash command that emits the version of a Python package using importlib.metadata.
     * The output is formatted as a YAML string: {@code package-name: "x.y.z"}.
     *
     * Interpreter stderr is discarded so that a missing package (or any other interpreter error)
     * yields {@code package-name: ""} rather than error text. Nextflow runs an {@code eval} output
     * inline inside a double-quoted {@code bash -c "..."}, so any captured stderr containing shell
     * metacharacters would otherwise break that command's parse and fail the task.
     *
     * @param packageName the importlib-resolvable distribution name (e.g. {@code "cutadapt"})
     * @return a bash command string suitable for use in a Nextflow process {@code eval} directive
     */
    @Function
    String pyPackageVersion(String packageName) {
        if (!packageName.matches(/[A-Za-z0-9._-]+/)) {
            throw new IllegalArgumentException(
                "Invalid Python package name '${packageName}': only letters, digits, hyphens, underscores, and dots are allowed (PEP 508)."
            )
        }
        return """
            echo '${packageName}: "'\$( python3 -c 'import sys; from importlib.metadata import version; print(version(sys.argv[1]))' ${packageName} 2>/dev/null )'"'
        """.stripIndent()
    }

    /**
     * Returns a bash command that emits the version of an R library using packageVersion().
     * The output is formatted as a YAML string: {@code library-name: "x.y.z"}.
     *
     * Interpreter stderr is discarded so that a missing library (or any other interpreter error)
     * yields {@code library-name: ""} rather than error text. Nextflow runs an {@code eval} output
     * inline inside a double-quoted {@code bash -c "..."}, so any captured stderr containing shell
     * metacharacters would otherwise break that command's parse and fail the task.
     *
     * @param libraryName the installed R library name (e.g. {@code "ichorCNA"})
     * @return a bash command string suitable for use in a Nextflow process {@code eval} directive
     */
    @Function
    String rLibraryVersion(String libraryName) {
        if (!libraryName.matches(/[A-Za-z0-9._-]+/)) {
            throw new IllegalArgumentException(
                "Invalid R library name '${libraryName}': only letters, digits, hyphens, underscores, and dots are allowed."
            )
        }
        return """
            echo '${libraryName}: "'\$( Rscript -e 'cat(as.character(packageVersion(commandArgs(trailingOnly=TRUE)[1])))' ${libraryName} 2>/dev/null )'"'
        """.stripIndent()
    }

    /** Prepend a string prefix and indent all later elements by a given number of spaces. */
    private static String indent(String element, int indentBy = 2) {
        return element.split('\n').collect { " " * indentBy + it }.join("\n")
    }

    /** Collect all versions into a file that MultiQC expects. */
    @CompileDynamic
    @Function
    def collateVersions(def ch_versions, String prefix = "", int indentRestBy = 0) {
        return ch_versions
            .map { indent(it as String, indentRestBy) }
            .collectFile(name: "all_mqc_versions.yml", newLine: true, seed: prefix)
    }
}
