package com.dattilio.scoundrel

import com.dattilio.klient.api.LinePreprocessor
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import ro.fortsoft.pf4j.Extension
import ro.fortsoft.pf4j.Plugin
import ro.fortsoft.pf4j.PluginWrapper

@Extension
class Combat(wrapper: PluginWrapper) : Plugin(wrapper), LinePreprocessor {
    private val logger = LoggerFactory.getLogger(Combat::class.java)
    var enabled = false

    val stage = Stage()
    override fun start() {
        stage.title = "Combat"
        logger.info("Combat.start()")
        val checkBox = CheckBox("Auto Combat")
        checkBox.isSelected = false
        checkBox.selectedProperty().addListener({
            observable, oldValue, newValue ->
            enabled = newValue
        })
        stage.scene = Scene(VBox(checkBox))
        stage.show()
    }

    override fun preProcessLine(line: String): String {
        logger.info("[C] " + line)
        return if (enabled) {
//                processLine()
            line
        } else {
            line
        }
    }

    override fun stop() {
        logger.info("Combat.stop()")
    }

}



