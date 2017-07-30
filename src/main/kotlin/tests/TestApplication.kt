package tests

import Api
import TecTextParser
import View
import javafx.application.Application
import javafx.stage.Stage
import widget.Controls
import java.io.File

class TestApplication : Application() {
    val controls: Controls = Controls {
        text: String ->
        System.out.println(text)
    }
    val view = View(controls)
    val api = Api()
    val parser = TecTextParser(controls, api)
    override fun start(primaryStage: Stage?) {
        view.setupUI(primaryStage)
        val classLoader = javaClass.classLoader
        val file = File(classLoader.getResource("map")!!.file)
        file.forEachLine { parser.parseLine(it) }
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TestApplication::class.java)
        }
    }

}