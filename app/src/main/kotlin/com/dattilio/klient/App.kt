package com.dattilio.klient

import javafx.application.Application
import javafx.stage.Stage
import javax.inject.Inject

class App: Application() {

    @Inject
    lateinit var view: View

    @Inject
    lateinit var controller: TecClient

    lateinit var graph: AppComponent

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(App::class.java)
        }
    }

    override fun start(primaryStage: Stage?) {
        graph = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
        view = graph.getView()
        view.setupUI(primaryStage)
        controller = graph.getTecClient()
        controller.start()
    }

    fun getAppComponent(): AppComponent {
        return graph
    }

}