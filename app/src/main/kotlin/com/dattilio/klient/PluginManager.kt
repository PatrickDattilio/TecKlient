package com.dattilio.klient

import com.dattilio.klient.api.LinePostprocessor
import com.dattilio.klient.api.LinePreprocessor
import com.dattilio.klient.api.SendCommand
import ro.fortsoft.pf4j.DefaultPluginManager
import javax.inject.Inject


class PluginManager @Inject constructor(sendCommand: SendCommand) {


    val pluginManager = DefaultPluginManager()
    private var preProcessors: MutableList<LinePreprocessor>
    private var postProcessors: MutableList<LinePostprocessor>


    init {
        pluginManager.loadPlugins()
        //We probably need to inject sendCommand using a custom PluginManager? Is it time for dagger?
        pluginManager.startPlugins()
        preProcessors = pluginManager.getExtensions(LinePreprocessor::class.java)
        postProcessors = pluginManager.getExtensions(LinePostprocessor::class.java)

        for (preprocessor in preProcessors) {
            preprocessor.setSendCommand(sendCommand)
        }

    }

    fun preProcessLine(line: String): String {
        var processedLine = line
        for (preProcessor in preProcessors) {
            if (preProcessor != null) {
                processedLine = preProcessor.preProcessLine(processedLine)
                if (line != processedLine) {
                    System.out.println("PreProcessor: " + preProcessor::class.simpleName + " modified line: " + line)
                }
            }
        }
        return line
    }

    fun postProcessLine(line: String) {
        for (postProcessor in postProcessors) {
            postProcessor.postProcessLine(line)
        }

    }

//    class DaggerPluginFactory(val appComponent: AppComponent) : DefaultPluginFactory() {
//        override fun create(pluginWrapper: PluginWrapper?): Plugin {
//            val plugin = super.create(pluginWrapper)
//            if ((plugin != null).and(plugin is DaggerPlugin)) {
//                appComponent.inject(plugin as DaggerPlugin)
//            }
//            return plugin
//        }
//
//    }
}


