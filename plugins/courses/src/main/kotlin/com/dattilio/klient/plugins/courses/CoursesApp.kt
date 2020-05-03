package com.dattilio.klient.plugins.courses

import com.dattilio.klient.plugins.courses.CoursesPreProcessor
import javafx.application.Application
import javafx.stage.Stage


fun main(args: Array<String>) {
    Application.launch(CoursesApp::class.java, *args)
}

class CoursesApp : Application() {

    override fun start(primaryStage: Stage?) {
        CoursesPreProcessor()
    }
}