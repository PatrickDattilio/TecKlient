package com.dattilio.klient.plugins.map

import javafx.application.Application
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(MapApp::class.java, *args)
}

class MapApp : Application() {

    override fun start(primaryStage: Stage?) {
        MapPreProcessor()
    }
}