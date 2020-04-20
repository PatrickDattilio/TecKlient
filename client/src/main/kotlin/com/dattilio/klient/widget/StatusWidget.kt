package com.dattilio.klient.widget

import com.dattilio.klient.api.SendCommand
import javafx.event.EventHandler
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color

class StatusWidget(sendCommand: SendCommand) : Canvas(100.0, 100.0) {
    val canvas = graphicsContext2D
    var health = 0.0
    var fatigue = 0.0
    var encumbrance = 0.0
    var satiation = 0.0

    init {
        onMouseClicked = EventHandler { sendCommand.send("condition") }
    }

    fun update(statusData: List<String>) {
        val value = 100.0 - statusData[1].toDouble()
        if (statusData[0] == "Health") {
            health = value
        } else if (statusData[0] == "Fatigue") {
            fatigue = value
        } else if (statusData[0] == "Encumbrance") {
            encumbrance = value
        } else if (statusData[0] == "Satiation") {
            satiation = value
        }
        draw()
    }

    private fun draw() {
        canvas.fill = Color.WHITE
        canvas.fillRect(0.0, 0.0, width, height)
        //Health
        canvas.fill = Color.valueOf("#3c0203")
        canvas.fillRect(0.0, 0.0, 20.0, 100.0)
        canvas.fill = Color.valueOf("#e30101")
        canvas.fillRect(0.0, health, 20.0, 100.0)

        //Fatigue
        canvas.fill = Color.valueOf("#3d3f04")
        canvas.fillRect(20.0, 0.0, 20.0, 100.0)
        canvas.fill = Color.valueOf("#e2e201")
        canvas.fillRect(20.0, fatigue, 20.0, 100.0)

        //Encumbrance
        canvas.fill = Color.valueOf("#023f3f")
        canvas.fillRect(40.0, 0.0, 20.0, 100.0)
        canvas.fill = Color.valueOf("#00e2e2")
        canvas.fillRect(40.0, encumbrance, 20.0, 100.0)

        //Satiation
        canvas.fill = Color.valueOf("#044006")
        canvas.fillRect(60.0, 0.0, 20.0, 100.0)
        canvas.fill = Color.valueOf("#00e201")
        canvas.fillRect(60.0, satiation, 20.0, 100.0)
    }


}