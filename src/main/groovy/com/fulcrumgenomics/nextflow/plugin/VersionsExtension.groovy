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
    String bcftoolsVersion() { return VersionsCommand.Bcftools }

    /** Bash command to return the version of bwa-mem2. */
    @Function
    String bwaMem2Version() { return VersionsCommand.BwaMem2 }

    /** Bash command to return the version of fgbio. */
    @Function
    String fgbioVersion() { return VersionsCommand.Fgbio }

    /** Bash command to return the version of falco. */
    @Function
    String falcoVersion() { return VersionsCommand.Falco }

    /** Bash command to return the version of picard. */
    @Function
    String picardVersion() { return VersionsCommand.Picard }

    /** Bash command to return the version of samtools. */
    @Function
    String samtoolsVersion() { return VersionsCommand.Samtools }

    /** Bash command to return the version of splitcode. */
    @Function
    String splitcodeVersion() { return VersionsCommand.Splitcode }

    /** Collect all versions into a file that MultiQC expects. */
    @CompileDynamic
    @Function
    def collateVersions(def ch_versions, String prefix = "", int indentRestBy = 0) {
        return VersionsCommand.collate(ch_versions, prefix, indentRestBy)
    }
}
