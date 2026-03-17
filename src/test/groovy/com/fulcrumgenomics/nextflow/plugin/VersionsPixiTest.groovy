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
    def 'VersionsCommand.pyPackageVersion("cutadapt") should output cutadapt: "4.9" in the cutadapt pixi environment'() {
        expect:
            runInPixiEnv('cutadapt', VersionsCommand.pyPackageVersion('cutadapt')) == 'cutadapt: "4.9"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('bcftools') })
    def 'VersionsCommand.Bcftools should output bcftools: "1.23" in the bcftools pixi environment'() {
        expect:
            runInPixiEnv('bcftools', VersionsCommand.Bcftools) == 'bcftools: "1.23"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('bwa-mem2') })
    def 'VersionsCommand.BwaMem2 should output bwa-mem2: "2.2.1" in the bwa-mem2 pixi environment'() {
        expect:
            runInPixiEnv('bwa-mem2', VersionsCommand.BwaMem2) == 'bwa-mem2: "2.2.1"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('falco') })
    def 'VersionsCommand.Falco should output falco: "1.2.5" in the falco pixi environment'() {
        expect:
            runInPixiEnv('falco', VersionsCommand.Falco) == 'falco: "1.2.5"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('fgbio') })
    def 'VersionsCommand.Fgbio should output fgbio: "3.1.2" in the fgbio pixi environment'() {
        expect:
            runInPixiEnv('fgbio', VersionsCommand.Fgbio) ==~ /fgbio: "3\.1\.2\s*"/
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('picard') })
    def 'VersionsCommand.Picard should output picard: "3.4.0" in the picard pixi environment'() {
        expect:
            runInPixiEnv('picard', VersionsCommand.Picard) == 'picard: "3.4.0"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('samtools') })
    def 'VersionsCommand.Samtools should output samtools: "1.23" in the samtools pixi environment'() {
        expect:
            runInPixiEnv('samtools', VersionsCommand.Samtools) == 'samtools: "1.23"'
    }

    @IgnoreIf({ !VersionsPixiTest.isPixiEnvAvailable('splitcode') })
    def 'VersionsCommand.Splitcode should output splitcode: "0.31.6" in the splitcode pixi environment'() {
        expect:
            runInPixiEnv('splitcode', VersionsCommand.Splitcode) == 'splitcode: "0.31.6"'
    }
}
