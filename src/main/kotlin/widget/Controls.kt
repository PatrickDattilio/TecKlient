package widget

import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox

class Controls(val sendCommand: (String) -> Unit) : VBox() {

    val map = Map(120.0, 120.0)
    val compass = Compass(120.0, 120.0, sendCommand)
    val macros = setupMacros()

    init {
        children.addAll(macros, map, compass)
    }

    private fun setupMacros(): GridPane {
        val macros = GridPane()
        fun Button(text: String, command: String): Button {
            val button = Button(text)
            button.setOnAction { sendCommand(command) }
            return button
        }
        macros.add(Button("I", "fe1"), 0, 0)
        macros.add(Button("II", "fe2"), 1, 0)
        macros.add(Button("III", "fe3"), 2, 0)
        macros.add(Button("IV", "fe4"), 3, 0)
        macros.add(Button("V", "fe5"), 4, 0)

        macros.add(Button("VI", "fe6"), 0, 1)
        macros.add(Button("VII", "fe7"), 1, 1)
        macros.add(Button("VIII", "fe8"), 2, 1)
        macros.add(Button("IX", "fe9"), 3, 1)
        macros.add(Button("X", "fe10"), 4, 1)

        macros.add(Button("XI", "fe11"), 0, 2)
        macros.add(Button("XII", "fe12"), 1, 2)
        macros.add(Button("XIII", "fe13"), 2, 2)
        macros.add(Button("XIV", "fe14"), 3, 2)
        macros.add(Button("XV", "fe15"), 4, 2)


        return macros
    }

    fun updateMap(skoot: String) {
        map.updateMap(skoot)
    }

    fun updateExits(skoot: String) {
        map.updateExits(skoot)
    }

    fun updateCompass(skoot: String) {
        compass.updateCompass(skoot)
    }

    fun updatePlayerStatus(skoot: String) {
        val statusData = Regex("\\W+").split(skoot)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateLighting(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}