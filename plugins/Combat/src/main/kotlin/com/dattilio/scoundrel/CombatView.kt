package com.dattilio.scoundrel

import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage

class CombatView(presenter: CombatPreProcessor) {

    val stage = Stage()
    val queueText: Text
    val checkBox: CheckBox
    var engagedText: Text

    init {
        stage.title = "Combat"
        checkBox = CheckBox("Auto Combat")
        checkBox.isSelected = false
        checkBox.selectedProperty().addListener({
            _, _, newValue ->
            presenter.checkBoxClicked(newValue)
        })
        val queueLabel = Label("Action Queue:")
        queueText = Text()
        val engagedLabel = Label("Engaged to:")
        engagedText = Text()
        val combatBox = VBox(checkBox, queueLabel, queueText, engagedLabel, engagedText)
        combatBox.minHeight = 300.0
        combatBox.minWidth = 300.0
        stage.scene = Scene(combatBox)
        stage.show()
    }
}