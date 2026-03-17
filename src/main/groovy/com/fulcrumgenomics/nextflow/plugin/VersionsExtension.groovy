package com.fulcrumgenomics.nextflow.plugin

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import nextflow.Session
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint

/** An extension to easily collect CLI tool version information in Nextflow processes. */
@CompileStatic
class VersionsExtension extends PluginExtensionPoint {

    @Override
    protected void init(Session session) { }

    /** Bash command to return the version of bcftools. */
    @Function
    String bcftools() { return VersionFor.Bcftools }

    /** Bash command to return the version of bwa-mem2. */
    @Function
    String bwaMem2() { return VersionFor.BwaMem2 }

    /** Bash command to return the version of fgbio. */
    @Function
    String fgbio() { return VersionFor.Fgbio }

    /** Bash command to return the version of falco. */
    @Function
    String falco() { return VersionFor.Falco }

    /** Bash command to return the version of picard. */
    @Function
    String picard() { return VersionFor.Picard }

    /** Bash command to return the version of samtools. */
    @Function
    String samtools() { return VersionFor.Samtools }

    /** Bash command to return the version of splitcode. */
    @Function
    String splitcode() { return VersionFor.Splitcode }

    /** Collect all versions into a file that MultiQC expects. */
    @CompileDynamic
    @Function
    def all(def ch_versions, String prefix = "", int indentRestBy = 0) {
        return VersionFor.all(ch_versions, prefix, indentRestBy)
    }
}
