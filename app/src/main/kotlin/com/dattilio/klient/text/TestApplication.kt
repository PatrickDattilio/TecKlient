package com.dattilio.klient.text

import com.dattilio.klient.PluginManager
import com.dattilio.klient.TecTextParser
import com.dattilio.klient.View
import javafx.application.Application
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import widget.Controls
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class TestApplication : Application() {
    private val logger = LogManager.getLogger()
    val controls: Controls = Controls {
        text: String ->
        System.out.println(text)
    }
    val view = View(controls)
    val pluginManager = PluginManager()
    val parser = TecTextParser(controls, pluginManager)

    override fun start(primaryStage: Stage?) {
        view.setupUI(primaryStage)
        val classLoader = javaClass.classLoader

        val file = File(".\\app\\src\\main\\resources\\personalText")
        var line:ArrayList<TecTextParser.TextAndStyle>
        file.forEachLine {
            line = parser.parseLine(it)
            view.addTextWithStyle(line)}
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TestApplication::class.java)
        }
    }

}