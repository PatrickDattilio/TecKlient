package com.dattilio.scoundrel

import com.dattilio.klient.api.LinePreprocessor
import com.dattilio.klient.api.SendCommand
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import ro.fortsoft.pf4j.Extension
import ro.fortsoft.pf4j.Plugin
import ro.fortsoft.pf4j.PluginWrapper
import java.util.*

@Extension
class Combat(wrapper: PluginWrapper) : Plugin(wrapper), LinePreprocessor, SendCommand {


    private val logger = LoggerFactory.getLogger(Combat::class.java)
    var enabled = false
    var free = true
    var send: ((String) -> Unit)? = null
    val stage = Stage()

    override fun start() {
        stage.title = "Combat"
        logger.info("Combat.start()")
        val checkBox = CheckBox("Auto Combat")
        checkBox.isSelected = false
        checkBox.selectedProperty().addListener({
            _, _, newValue ->
            enabled = newValue
        })
        stage.scene = Scene(VBox(checkBox))
        stage.show()
    }

    override fun sendCommand(send: (String) -> Unit) {
        this.send = send
    }

    override fun preProcessLine(line: String): String {
        logger.info("[C] " + line)
        return if (enabled) {
            processLine(line)
            line
        } else {
            line
        }
    }

    private fun processLine(line: String) {
        if ("You are no longer busy." in line) {
            free = true
            act()
        }
    }

    val queue: PriorityQueue<Action> = PriorityQueue()
    var action = Action.NOTHING
    private fun act() {
        if (free.and(queue.size > 0)) {
            action = queue.poll()
            when (action) {
                Action.RECOVER -> {
                    recover()
                    free = false
                }

                Action.RETREAT -> {
                    //TODO retreat()
                }
                Action.KILL -> {
                    free = false
                    queue.add(Action.KILL)
                    sendCommand("kl")
                }
                Action.RELEASE -> {
                    sendCommand("release")
                }
                else -> {
                    act()
                }

            }
        }
    }

    private fun recover() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun stop() {
        logger.info("Combat.stop()")
    }

    fun sendCommand(command: String) {
        send?.invoke(command)
    }
}



