package tests

import TecTextParser
import View
import javafx.application.Application
import javafx.stage.Stage
import java.io.File

class TestApplication : Application() {
    val view = View({ text -> System.out.println(text) })
    val parser = TecTextParser(view)
    override fun start(primaryStage: Stage?) {
        view.setupUI(primaryStage)
        val classLoader = javaClass.classLoader
        val file = File(classLoader.getResource("serverText")!!.file)
        file.forEachLine { parser.parseLine(it) }
    }

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TestApplication::class.java)
        }
    }

}