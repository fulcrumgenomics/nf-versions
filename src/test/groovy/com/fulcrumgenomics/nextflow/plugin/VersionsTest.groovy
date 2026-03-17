package com.fulcrumgenomics.nextflow.plugin

import nextflow.Channel
import nextflow.plugin.Plugins
import nextflow.plugin.TestPluginDescriptorFinder
import nextflow.plugin.TestPluginManager
import nextflow.plugin.extension.PluginExtensionProvider
import org.pf4j.PluginDescriptorFinder
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
                include { all } from 'plugin/nf-versions'
                channel.of('hi-mom')
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            result.val == 'hi-mom'
            result.val == Channel.STOP
    }

    def 'bcftools() should return a bash command string for bcftools'() {
        when:
            String SCRIPT = '''
                include { bcftools } from 'plugin/nf-versions'
                channel.of(bcftools())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('bcftools')
            result.val == Channel.STOP
    }

    def 'samtools() should return a bash command string for samtools'() {
        when:
            String SCRIPT = '''
                include { samtools } from 'plugin/nf-versions'
                channel.of(samtools())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('samtools')
            result.val == Channel.STOP
    }

    def 'fgbio() should return a bash command string for fgbio'() {
        when:
            String SCRIPT = '''
                include { fgbio } from 'plugin/nf-versions'
                channel.of(fgbio())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('fgbio')
            result.val == Channel.STOP
    }

    def 'picard() should return a bash command string for picard'() {
        when:
            String SCRIPT = '''
                include { picard } from 'plugin/nf-versions'
                channel.of(picard())
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            (result.val as String).contains('picard')
            result.val == Channel.STOP
    }

    def 'VersionFor.Bcftools should contain the bcftools version command'() {
        expect:
            VersionFor.Bcftools.contains('bcftools --version')
    }

    def 'VersionFor.BwaMem2 should contain the bwa-mem2 version command'() {
        expect:
            VersionFor.BwaMem2.contains('bwa-mem2 version')
    }

    def 'VersionFor.Fgbio should contain the fgbio version command'() {
        expect:
            VersionFor.Fgbio.contains('fgbio --version')
    }

    def 'VersionFor.Falco should contain the falco version command'() {
        expect:
            VersionFor.Falco.contains('falco --version')
    }

    def 'VersionFor.Picard should contain the picard version command'() {
        expect:
            VersionFor.Picard.contains('picard ViewSam')
    }

    def 'VersionFor.Samtools should contain the samtools version command'() {
        expect:
            VersionFor.Samtools.contains('samtools --version')
    }

    def 'VersionFor.Splitcode should contain the splitcode version command'() {
        expect:
            VersionFor.Splitcode.contains('splitcode --version')
    }

    def 'VersionFor.indent should prepend spaces to each line'() {
        expect:
            VersionFor.indent("a\nb", 4) == "    a\n    b"
    }

    def 'VersionFor.indent should use two spaces by default'() {
        expect:
            VersionFor.indent("line") == "  line"
    }
}
