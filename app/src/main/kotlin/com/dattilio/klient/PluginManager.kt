package com.dattilio.klient

import com.dattilio.klient.api.LinePreprocessor
import ro.fortsoft.pf4j.DefaultPluginManager

class PluginManager {


    val pluginManager = DefaultPluginManager()
    private var preProcessors: MutableList<LinePreprocessor>

    init {
        pluginManager.loadPlugins()
        pluginManager.startPlugins()
        preProcessors = pluginManager.getExtensions(LinePreprocessor::class.java)
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
}