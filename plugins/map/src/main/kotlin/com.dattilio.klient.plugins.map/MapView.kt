package com.dattilio.klient.plugins.map

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.stage.Stage

class MapView(
    onEnableBoxClicked: (Boolean) -> Unit,
    onSaveClickListener: () -> Unit
) {

    private val stage = Stage()
    val checkBox: CheckBox
    val button: Button

    init {
        stage.title = "Map"
        checkBox = CheckBox("Enabled")
        checkBox.isSelected = false
        checkBox.selectedProperty().addListener { _, _, newValue ->
            onEnableBoxClicked.invoke(newValue)
        }
        button = Button("Save")
        button.setOnMouseClicked { event ->
            when (event) {
                is MouseEvent -> onSaveClickListener.invoke()
            }
        }

        val mapBox = VBox(checkBox, button)
        mapBox.minHeight = 600.0
        mapBox.minWidth = 600.0
        stage.scene = Scene(mapBox)
        stage.show()
    }
}