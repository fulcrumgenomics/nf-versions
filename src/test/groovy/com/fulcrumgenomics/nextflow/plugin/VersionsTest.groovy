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

    def 'VersionsCommand.Bcftools should contain the bcftools version command'() {
        expect:
            VersionsCommand.Bcftools.contains('bcftools --version')
    }

    def 'VersionsCommand.BwaMem2 should contain the bwa-mem2 version command'() {
        expect:
            VersionsCommand.BwaMem2.contains('bwa-mem2 version')
    }

    def 'VersionsCommand.Fgbio should contain the fgbio version command'() {
        expect:
            VersionsCommand.Fgbio.contains('fgbio --version')
    }

    def 'VersionsCommand.Falco should contain the falco version command'() {
        expect:
            VersionsCommand.Falco.contains('falco --version')
    }

    def 'VersionsCommand.Picard should contain the picard version command'() {
        expect:
            VersionsCommand.Picard.contains('picard ViewSam')
    }

    def 'VersionsCommand.Samtools should contain the samtools version command'() {
        expect:
            VersionsCommand.Samtools.contains('samtools --version')
    }

    def 'VersionsCommand.Splitcode should contain the splitcode version command'() {
        expect:
            VersionsCommand.Splitcode.contains('splitcode --version')
    }

    def 'VersionsCommand.pyPackageVersion should contain the package name and importlib.metadata'() {
        expect:
            VersionsCommand.pyPackageVersion('cutadapt').contains('cutadapt')
            VersionsCommand.pyPackageVersion('cutadapt').contains('importlib.metadata')
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

    def 'VersionsCommand.pyPackageVersion should reject package names containing shell meta-characters'() {
        when:
            VersionsCommand.pyPackageVersion(name)
        then:
            thrown(IllegalArgumentException)
        where:
            name << ['pkg; rm -rf /', 'pkg$(evil)', 'pkg`evil`', 'pkg|evil', 'pkg>out', 'pkg\nevil']
    }

    def 'VersionsCommand.indent should prepend spaces to each line'() {
        expect:
            VersionsCommand.indent("a\nb", 4) == "    a\n    b"
    }

    def 'VersionsCommand.indent should use two spaces by default'() {
        expect:
            VersionsCommand.indent("line") == "  line"
    }
}
