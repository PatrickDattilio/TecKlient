package com.dattilio.klient.client

import javafx.application.Application
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlin.coroutines.CoroutineContext

fun main(args: Array<String>) {
    Application.launch(Forwarder::class.java, *args)
}

class Forwarder : Application(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx
//    @Inject
//    lateinit var view: View
//
//    @Inject
//    lateinit var controller: TecClient

    override fun start(primaryStage: Stage?) {
//        DaggerAppComponent.builder()
//            .appModule(AppModule(this))
//            .build()
//            .inject(this)
//
        val port: Int = parameters.raw.getOrNull(0)?.toInt() ?: 2323
        PluginManager(port)
//        controller.start()
    }
}