package com.dattilio.klient

import javafx.application.Application
import javafx.stage.Stage
import javax.inject.Inject
import java.io.File



open class App : Application() {

    @Inject
    lateinit var view: View

    @Inject
    lateinit var controller: TecClient

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(App::class.java)
        }
    }

    override fun start(primaryStage: Stage?) {
        System.setProperty("log4j.configuration", File("resources", "log4j2.properties").toString())
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
                .inject(this)
        view.setupUI(primaryStage,controller)
        controller.start()
    }
}