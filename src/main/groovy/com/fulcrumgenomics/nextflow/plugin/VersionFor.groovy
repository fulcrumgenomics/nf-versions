package com.fulcrumgenomics.nextflow.plugin

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

/** Command strings for determining the versions of various tools. */
@CompileStatic
class VersionFor {

    /** Bash command to return the version of bcftools. */
    public static final String Bcftools = """
        echo 'bcftools: "'\$( bcftools --version | head -n1 | sed -e 's/bcftools //g' )'"'
    """.stripIndent()

    /** Bash command to return the version of bwa-mem2. */
    public static final String BwaMem2 = """
        echo 'bwa-mem2: "'\$( bwa-mem2 version )'"'
    """.stripIndent()

    /** Bash command to return the version of fgbio. */
    public static final String Fgbio = """
        echo 'fgbio: "'\$(
            fgbio --version 2>&1 \
            | tr -d '[:cntrl:]' \
            | sed -e 's/^.*Version: //' -e 's/\\[.*\$//'
        )'"'
    """.stripIndent()

    /** Bash command to return the version of falco. */
    public static final String Falco = """
        echo 'falco: "'\$( falco --version | sed -e 's/falco //g' )'"'
    """.stripIndent()

    /** Bash command to return the version of picard. */
    public static final String Picard = """
        echo 'picard: "'\$( picard ViewSam --version true 2>&1 | grep -v 'cannot change locale' | sed -e 's/Version://g' )'"'
    """.stripIndent()

    /** Bash command to return the version of samtools. */
    public static final String Samtools = """
        echo 'samtools: "'\$( samtools --version | head -n1 | sed -e 's/samtools //g' )'"'
    """.stripIndent()

    /** Bash command to return the version of splitcode. */
    public static final String Splitcode = """
        echo 'splitcode: "'\$( splitcode --version | sed -e 's/splitcode, version //g' | sed 's/\\.\$//' )'"'
    """.stripIndent()

    /** Prepend a string prefix and indent all later elements by a given number of spaces. */
    static String indent(String element, int indentBy = 2) {
        return element.split('\n').collect { " " * indentBy + it }.join("\n")
    }

    /** Collect all versions into a file that MultiQC expects. */
    @CompileDynamic
    static def all(def ch_versions, String prefix = "", int indentRestBy = 0) {
        return ch_versions
            .map { indent(it as String, indentRestBy) }
            .collectFile(name: "all_mqc_versions.yml", newLine: true, seed: prefix)
    }
}
