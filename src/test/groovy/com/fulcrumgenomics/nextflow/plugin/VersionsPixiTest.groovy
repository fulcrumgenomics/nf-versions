package com.fulcrumgenomics.nextflow.plugin

import spock.lang.IgnoreIf
import spock.lang.Specification

import java.nio.file.Path

/** Integration tests that execute each tool's bash version command in its pixi environment. */
class VersionsPixiTest extends Specification {

    /** The project root directory (where pixi.toml lives). */
    static final Path projectRoot = Path.of('.').toAbsolutePath().normalize()

    /** Returns true if the named pixi environment directory is present under .pixi/envs/. */
    static boolean isPixiEnvAvailable(String environment) {
        return projectRoot.resolve(".pixi/envs/${environment}").toFile().isDirectory()
    }

    /** Runs a bash script in a named pixi environment and returns trimmed stdout. */
    static String runInPixiEnv(String environment, String script) {
        def proc = new ProcessBuilder(
            'pixi', 'run',
            '--manifest-path', projectRoot.resolve('pixi.toml').toString(),
            '-e', environment,
            'bash', '-c', script
        ).redirectErrorStream(false).start()
        proc.waitFor()
        return proc.inputStream.text.trim()
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('cutadapt') })
    def 'pyPackageVersion("cutadapt") should output cutadapt: "4.9" in the cutadapt pixi environment'() {
        expect:
            runInPixiEnv('cutadapt', new VersionsExtension().pyPackageVersion('cutadapt')) == 'cutadapt: "4.9"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('bcftools') })
    def 'bcftoolsVersion() should output bcftools: "1.23" in the bcftools pixi environment'() {
        expect:
            runInPixiEnv('bcftools', new VersionsExtension().bcftoolsVersion()) == 'bcftools: "1.23"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('bwa-mem2') })
    def 'bwaMem2Version() should output bwa-mem2: "2.2.1" in the bwa-mem2 pixi environment'() {
        expect:
            runInPixiEnv('bwa-mem2', new VersionsExtension().bwaMem2Version()) == 'bwa-mem2: "2.2.1"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('falco') })
    def 'falcoVersion() should output falco: "1.2.5" in the falco pixi environment'() {
        expect:
            runInPixiEnv('falco', new VersionsExtension().falcoVersion()) == 'falco: "1.2.5"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('fgbio') })
    def 'fgbioVersion() should output fgbio: "3.1.2" in the fgbio pixi environment'() {
        expect:
            runInPixiEnv('fgbio', new VersionsExtension().fgbioVersion()) ==~ /fgbio: "3\.1\.2\s*"/
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('picard') })
    def 'picardVersion() should output picard: "3.4.0" in the picard pixi environment'() {
        expect:
            runInPixiEnv('picard', new VersionsExtension().picardVersion()) == 'picard: "3.4.0"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('samtools') })
    def 'samtoolsVersion() should output samtools: "1.23" in the samtools pixi environment'() {
        expect:
            runInPixiEnv('samtools', new VersionsExtension().samtoolsVersion()) == 'samtools: "1.23"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('splitcode') })
    def 'splitcodeVersion() should output splitcode: "0.31.6" in the splitcode pixi environment'() {
        expect:
            runInPixiEnv('splitcode', new VersionsExtension().splitcodeVersion()) == 'splitcode: "0.31.6"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('bedtools') })
    def 'bedtoolsVersion() should output bedtools: "2.31.1" in the bedtools pixi environment'() {
        expect:
            runInPixiEnv('bedtools', new VersionsExtension().bedtoolsVersion()) == 'bedtools: "2.31.1"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('bwa') })
    def 'bwaVersion() should output bwa: "0.7.19-r1273" in the bwa pixi environment'() {
        expect:
            runInPixiEnv('bwa', new VersionsExtension().bwaVersion()) == 'bwa: "0.7.19-r1273"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('fastp') })
    def 'fastpVersion() should output fastp: "1.3.3" in the fastp pixi environment'() {
        expect:
            runInPixiEnv('fastp', new VersionsExtension().fastpVersion()) == 'fastp: "1.3.3"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('fastqc') })
    def 'fastqcVersion() should output fastqc: "0.12.1" in the fastqc pixi environment'() {
        expect:
            runInPixiEnv('fastqc', new VersionsExtension().fastqcVersion()) == 'fastqc: "0.12.1"'
    }

    // The fastqc-rs 0.3.4 conda package ships a binary that self-reports 0.3.3, so we assert the
    // binary's own reported version rather than the package version.
    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('fastqc-rs') })
    def 'fastqcRsVersion() should output fastqc-rs: "0.3.3" in the fastqc-rs pixi environment'() {
        expect:
            runInPixiEnv('fastqc-rs', new VersionsExtension().fastqcRsVersion()) == 'fastqc-rs: "0.3.3"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('mosdepth') })
    def 'mosdepthVersion() should output mosdepth: "0.3.14" in the mosdepth pixi environment'() {
        expect:
            runInPixiEnv('mosdepth', new VersionsExtension().mosdepthVersion()) == 'mosdepth: "0.3.14"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('revtag') })
    def 'revtagVersion() should output revtag: "1.0.0" in the revtag pixi environment'() {
        expect:
            runInPixiEnv('revtag', new VersionsExtension().revtagVersion()) == 'revtag: "1.0.0"'
    }

    // The sambamba 1.0.1 build only runs reliably on linux-64 (it segfaults on macOS, including
    // osx-64 under Rosetta), so its pixi environment is restricted to linux-64 and this test is
    // skipped wherever that environment is absent.
    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('sambamba') })
    def 'sambambaVersion() should output sambamba: "1.0.1" in the sambamba pixi environment'() {
        expect:
            runInPixiEnv('sambamba', new VersionsExtension().sambambaVersion()) == 'sambamba: "1.0.1"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('ichorcna') })
    def 'rLibraryVersion("ichorCNA") should output ichorCNA: "0.5.1" in the ichorcna pixi environment'() {
        expect:
            runInPixiEnv('ichorcna', new VersionsExtension().rLibraryVersion('ichorCNA')) == 'ichorCNA: "0.5.1"'
    }
}
