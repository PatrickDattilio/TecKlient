package com.dattilio.klient.client

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}

class App : Application(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
    @Inject
    lateinit var view: View

    @Inject
    lateinit var controller: TecClient

    override fun start(primaryStage: Stage?) {
        com.dattilio.klient.client.DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
            .inject(this)
        view.setupUI(primaryStage, controller)
        controller.start()
    }
}