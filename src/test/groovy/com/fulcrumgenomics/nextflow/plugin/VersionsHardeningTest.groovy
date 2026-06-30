package com.fulcrumgenomics.nextflow.plugin

import spock.lang.IgnoreIf
import spock.lang.Specification

/**
 * Regression tests for the interpreter-backed version helpers (pyPackageVersion, rLibraryVersion).
 *
 * Nextflow runs an {@code eval} output command by embedding it inline inside a double-quoted
 * {@code bash -c "<command>"} that an outer shell parses, so the outer shell performs the
 * {@code $(...)} substitution before the inner bash ever sees the text. When a helper merged the
 * interpreter's stderr into the captured substitution ({@code 2>&1}) and the package was absent,
 * the interpreter's error text (which contains parentheses and quotes) was spliced into the inner
 * command and broke its parse with {@code syntax error near unexpected token '('}, failing the
 * whole task. These tests pin the hardened behavior: a missing package yields {@code name: ""}
 * with a zero exit even through that double-eval wrapper.
 */
class VersionsHardeningTest extends Specification {

    /** True when an executable is resolvable on the PATH. */
    static boolean isOnPath(String executable) {
        return ['bash', '-c', "command -v ${executable}"].execute().waitFor() == 0
    }

    /**
     * Run a version command exactly as Nextflow does: embed it inline inside {@code bash -c "..."}
     * (escaping only the double quotes, as Nextflow does) and let an outer bash parse and execute
     * that line, so the {@code $(...)} is substituted by the outer shell first. Returns
     * {@code [exitCode, trimmedStdout]}.
     */
    static List runThroughNextflowEvalWrapper(String command) {
        def escaped = command.replace('"', '\\"')
        def proc = ['bash', '-c', "bash -c \"${escaped}\""].execute()
        def out = new StringBuilder()
        def err = new StringBuilder()
        proc.waitForProcessOutput(out, err)
        return [proc.exitValue(), out.toString().trim()]
    }

    @IgnoreIf({ !VersionsHardeningTest.isOnPath('python3') })
    def 'pyPackageVersion() emits name: "" for a missing package without breaking the eval wrapper'() {
        when:
            def (int code, String stdout) = runThroughNextflowEvalWrapper(
                new VersionsExtension().pyPackageVersion('nf_versions_absent_pkg')
            )
        then:
            code == 0
            stdout == 'nf_versions_absent_pkg: ""'
    }

    @IgnoreIf({ !VersionsHardeningTest.isOnPath('Rscript') })
    def 'rLibraryVersion() emits name: "" for a missing library without breaking the eval wrapper'() {
        when:
            def (int code, String stdout) = runThroughNextflowEvalWrapper(
                new VersionsExtension().rLibraryVersion('nfVersionsAbsentLib')
            )
        then:
            code == 0
            stdout == 'nfVersionsAbsentLib: ""'
    }
}
