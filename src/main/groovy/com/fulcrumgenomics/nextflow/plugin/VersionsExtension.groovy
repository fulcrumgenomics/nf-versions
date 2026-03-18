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

    private static final String BwaMem2 = """
        echo 'bwa-mem2: "'\$( bwa-mem2 version )'"'
    """.stripIndent()

    private static final String Falco = """
        echo 'falco: "'\$( falco --version | sed -e 's/falco //g' )'"'
    """.stripIndent()

    private static final String Fgbio = """
        echo 'fgbio: "'\$(
            fgbio --version 2>&1 \
            | tr -d '[:cntrl:]' \
            | sed -e 's/^.*Version: //' -e 's/\\[.*\$//'
        )'"'
    """.stripIndent()

    private static final String Picard = """
        echo 'picard: "'\$( picard ViewSam --version true 2>&1 | grep -v 'cannot change locale' | sed -e 's/Version://g' )'"'
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

    /** Bash command to return the version of bwa-mem2. */
    @Function
    String bwaMem2Version() { return BwaMem2 }

    /** Bash command to return the version of falco. */
    @Function
    String falcoVersion() { return Falco }

    /** Bash command to return the version of fgbio. */
    @Function
    String fgbioVersion() { return Fgbio }

    /** Bash command to return the version of picard. */
    @Function
    String picardVersion() { return Picard }

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
            echo '${packageName}: "'\$( python3 -c 'import sys; from importlib.metadata import version; print(version(sys.argv[1]))' ${packageName} 2>&1 )'"'
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
