package com.dattilio.klient

import javafx.stage.Stage
import java.io.File

class TestApp : App() {

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TestApp::class.java)
        }
    }

    override fun start(primaryStage: Stage?) {
        System.setProperty("log4j.configuration", File("resources", "log4j2.properties").toString())
//        DaggerAppComponent.builder()
//                .appModule(AppModule(this))
//                .build()
//                .inject(this)
//        view.setupUI(primaryStage,controller)
//        controller.start()
    }

}