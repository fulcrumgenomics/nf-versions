package com.fulcrumgenomics.nextflow.plugin

import nextflow.Channel
import nextflow.plugin.Plugins
import nextflow.plugin.TestPluginDescriptorFinder
import nextflow.plugin.TestPluginManager
import nextflow.plugin.extension.PluginExtensionProvider
import org.pf4j.PluginDescriptorFinder
import spock.lang.IgnoreIf
import spock.lang.Shared
import test.Dsl2Spec
import test.MockScriptRunner

import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.Manifest

/** Unit tests for the nf-versions plugin that use virtual file systems and mocking to run. */
class VersionsTest extends Dsl2Spec {

    /** The plugin mode for all running plugins during test time. */
    @Shared String pluginsMode

    /** The root directory for the plugin. */
    Path root = Path.of('.').toAbsolutePath().normalize()
    Path getRoot() { this.root }

    /** Setup the plugin manager and load the nf-versions plugin. */
    def setup() {
        PluginExtensionProvider.reset()

        pluginsMode = System.getProperty('pf4j.mode')
        System.setProperty('pf4j.mode', 'dev')

        def root = this.getRoot()

        def manager = new TestPluginManager(root) {
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                return new TestPluginDescriptorFinder() {
                    @Override
                    protected Manifest readManifestFromDirectory(Path pluginPath) {
                        def manifestPath = getManifestPath(pluginPath)
                        final input = Files.newInputStream(manifestPath)
                        return new Manifest(input)
                    }
                    protected Path getManifestPath(Path pluginPath) {
                        return pluginPath.resolve('build/tmp/jar/MANIFEST.MF')
                    }
                }
            }
        }

        Plugins.init(root, 'dev', manager)
        manager.loadPlugins()
        manager.startPlugins()
    }

    /** Cleanup after tests have run. */
    def cleanup() {
        Plugins.stop()
        PluginExtensionProvider.reset()
        pluginsMode ? System.setProperty('pf4j.mode', pluginsMode) : System.clearProperty('pf4j.mode')
    }

    def 'should have the plugin installed but not imported and raise no exception'() {
        when:
            String SCRIPT = '''
                channel.of('hi-mom')
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            result.val == 'hi-mom'
            result.val == Channel.STOP
    }

    def 'should import the plugin and not raise an exception'() {
        when:
            String SCRIPT = '''
                include { collateVersions } from 'plugin/nf-versions'
                channel.of('hi-mom')
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            result.val == 'hi-mom'
            result.val == Channel.STOP
    }

    def 'bcftoolsVersion() should return a bash command string for bcftools'() {
        when:
            String SCRIPT = '''
                include { bcftoolsVersion } from 'plugin/nf-versions'
                channel.of(bcftoolsVersion())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('bcftools')
            result.val == Channel.STOP
    }

    def 'samtoolsVersion() should return a bash command string for samtools'() {
        when:
            String SCRIPT = '''
                include { samtoolsVersion } from 'plugin/nf-versions'
                channel.of(samtoolsVersion())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('samtools')
            result.val == Channel.STOP
    }

    def 'fgbioVersion() should return a bash command string for fgbio'() {
        when:
            String SCRIPT = '''
                include { fgbioVersion } from 'plugin/nf-versions'
                channel.of(fgbioVersion())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('fgbio')
            result.val == Channel.STOP
    }

    def 'picardVersion() should return a bash command string for picard'() {
        when:
            String SCRIPT = '''
                include { picardVersion } from 'plugin/nf-versions'
                channel.of(picardVersion())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('picard')
            result.val == Channel.STOP
    }

    def 'bcftoolsVersion() should contain the bcftools version command'() {
        expect:
            new VersionsExtension().bcftoolsVersion().contains('bcftools --version')
    }

    def 'bwaMem2Version() should contain the bwa-mem2 version command'() {
        expect:
            new VersionsExtension().bwaMem2Version().contains('bwa-mem2 version')
    }

    def 'fgbioVersion() should contain the fgbio version command'() {
        expect:
            new VersionsExtension().fgbioVersion().contains('fgbio --version')
    }

    def 'falcoVersion() should contain the falco version command'() {
        expect:
            new VersionsExtension().falcoVersion().contains('falco --version')
    }

    def 'picardVersion() should contain the picard version command'() {
        expect:
            new VersionsExtension().picardVersion().contains('picard ViewSam')
    }

    def 'samtoolsVersion() should contain the samtools version command'() {
        expect:
            new VersionsExtension().samtoolsVersion().contains('samtools --version')
    }

    def 'splitcodeVersion() should contain the splitcode version command'() {
        expect:
            new VersionsExtension().splitcodeVersion().contains('splitcode --version')
    }

    def 'bedtoolsVersion() should contain the bedtools version command'() {
        expect:
            new VersionsExtension().bedtoolsVersion().contains('bedtools --version')
    }

    def 'bwaVersion() should contain the bwa version command'() {
        expect:
            new VersionsExtension().bwaVersion().contains('bwa 2>&1')
    }

    def 'fastpVersion() should contain the fastp version command'() {
        expect:
            new VersionsExtension().fastpVersion().contains('fastp --version')
    }

    def 'fastqcRsVersion() should contain the fqc version command'() {
        expect:
            new VersionsExtension().fastqcRsVersion().contains('fqc --version')
    }

    def 'mosdepthVersion() should contain the mosdepth version command'() {
        expect:
            new VersionsExtension().mosdepthVersion().contains('mosdepth --version')
    }

    def 'revtagVersion() should contain the revtag version command'() {
        expect:
            new VersionsExtension().revtagVersion().contains('revtag --version')
    }

    def 'sambambaVersion() should contain the sambamba version command'() {
        expect:
            new VersionsExtension().sambambaVersion().contains('sambamba --version')
    }

    def 'revtagVersion() should return a bash command string for revtag'() {
        when:
            String SCRIPT = '''
                include { revtagVersion } from 'plugin/nf-versions'
                channel.of(revtagVersion())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('revtag')
            result.val == Channel.STOP
    }

    def 'sambambaVersion() should return a bash command string for sambamba'() {
        when:
            String SCRIPT = '''
                include { sambambaVersion } from 'plugin/nf-versions'
                channel.of(sambambaVersion())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('sambamba')
            result.val == Channel.STOP
    }

    def 'pyPackageVersion() should contain the package name and importlib.metadata'() {
        expect:
            new VersionsExtension().pyPackageVersion('cutadapt').contains('cutadapt')
            new VersionsExtension().pyPackageVersion('cutadapt').contains('importlib.metadata')
    }

    def 'pyPackageVersion() should return a bash command string containing the package name'() {
        when:
            String SCRIPT = '''
                include { pyPackageVersion } from 'plugin/nf-versions'
                channel.of(pyPackageVersion('cutadapt'))
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('cutadapt')
            result.val == Channel.STOP
    }

    def 'pyPackageVersion() should reject package names containing shell meta-characters'() {
        when:
            new VersionsExtension().pyPackageVersion(name)
        then:
            thrown(IllegalArgumentException)
        where:
            name << ['pkg; rm -rf /', 'pkg$(evil)', 'pkg`evil`', 'pkg|evil', 'pkg>out', 'pkg\nevil']
    }

    def 'rLibraryVersion() should contain the library name and packageVersion command'() {
        expect:
            new VersionsExtension().rLibraryVersion('ichorCNA').contains('ichorCNA')
            new VersionsExtension().rLibraryVersion('ichorCNA').contains('packageVersion')
    }

    def 'rLibraryVersion() should reject library names containing shell meta-characters'() {
        when:
            new VersionsExtension().rLibraryVersion(name)
        then:
            thrown(IllegalArgumentException)
        where:
            name << ['lib; rm -rf /', 'lib$(evil)', 'lib`evil`', 'lib|evil', 'lib>out', 'lib\nevil']
    }

    def 'pyPackageVersion() should discard interpreter stderr rather than merge it into the captured output'() {
        when:
            def command = new VersionsExtension().pyPackageVersion('cutadapt')
        then:
            command.contains('2>/dev/null')
            !command.contains('2>&1')
    }

    def 'rLibraryVersion() should discard interpreter stderr rather than merge it into the captured output'() {
        when:
            def command = new VersionsExtension().rLibraryVersion('ichorCNA')
        then:
            command.contains('2>/dev/null')
            !command.contains('2>&1')
    }
}
