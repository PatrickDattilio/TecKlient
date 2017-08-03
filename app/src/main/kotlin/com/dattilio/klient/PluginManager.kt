package com.dattilio.klient

import com.dattilio.klient.api.DaggerPlugin
import com.dattilio.klient.api.LinePostprocessor
import com.dattilio.klient.api.LinePreprocessor
import java.nio.file.Paths
import dagger.internal.MembersInjectors.injectMembers
import ro.fortsoft.pf4j.*
import javax.inject.Inject


class PluginManager @Inject constructor(val appComponent: AppComponent) {


    val pluginManager= object: DefaultPluginManager(Paths.get("plugins")) {
        override fun createPluginFactory():PluginFactory {
            return DaggerPluginFactory(appComponent)
        }

    }
    private var preProcessors: MutableList<LinePreprocessor>
    private var postProcessors: MutableList<LinePostprocessor>


    init {
        pluginManager.loadPlugins()
        //We probably need to inject sendCommand using a custom PluginManager? Is it time for dagger?
        pluginManager.startPlugins()
        preProcessors = pluginManager.getExtensions(LinePreprocessor::class.java)
        postProcessors = pluginManager.getExtensions(LinePostprocessor::class.java)

    }

    fun preProcessLine(line: String): String {
        var processedLine = line
        for (preProcessor in preProcessors) {
            processedLine = preProcessor.preProcessLine(processedLine)
            if (line != processedLine) {

            }
        }
        return line
    }

    fun postProcessLine(line: String) {
        for (postProcessor in postProcessors) {
            postProcessor.postProcessLine(line)
        }

    }

    class DaggerPluginFactory(val appComponent: AppComponent) : DefaultPluginFactory() {
        override fun create(pluginWrapper: PluginWrapper?): Plugin {
            val plugin = super.create(pluginWrapper)
            if ((plugin != null).and(plugin is DaggerPlugin) ){
                plugin.inject(appComponent)
            }
            return plugin
        }

    }
}


