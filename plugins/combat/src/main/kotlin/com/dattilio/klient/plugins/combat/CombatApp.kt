package com.dattilio.klient.plugins.combat

import javafx.application.Application
import javafx.stage.Stage


fun main(args: Array<String>) {
    Application.launch(CombatApp::class.java, *args)
}

class CombatApp : Application() {

    override fun start(primaryStage: Stage?) {
        CombatPreProcessor()
    }
}