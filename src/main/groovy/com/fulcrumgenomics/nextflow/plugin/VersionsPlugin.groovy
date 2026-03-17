package com.fulcrumgenomics.nextflow.plugin

import groovy.transform.CompileStatic
import nextflow.plugin.BasePlugin
import org.pf4j.PluginWrapper

/** The nf-versions Nextflow plugin entrypoint. */
@CompileStatic
class VersionsPlugin extends BasePlugin {

    VersionsPlugin(PluginWrapper wrapper) {
        super(wrapper)
    }
}
