package com.dattilio.klient

import com.dattilio.klient.widget.Controls
import javafx.application.Application
import javafx.stage.Stage
import org.apache.logging.log4j.LogManager
import java.io.File

class TestApplication : Application() {
//    private val logger = LogManager.getLogger()
//    val controls: Controls = Controls {
//        text: String ->
//        System.out.println(text)
//    }
//    val view = View(controls)
//    val pluginManager = PluginManager(this::send)
//    val parser = TecTextParser(controls, pluginManager)

    override fun start(primaryStage: Stage?) {
//        view.setupUI(primaryStage)
//        val classLoader = javaClass.classLoader
//
//        val file = File(".\\src\\main\\resources\\personalText")
//        var line: ArrayList<TecTextParser.TextAndStyle>
//        file.forEachLine {
//            line = parser.parseLine(it)
//            view.addTextWithStyle(line)
//        }
    }

    fun send(string: String) {
        System.out.println(string)
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TestApplication::class.java)
        }
    }

}