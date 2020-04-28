package com.dattilio.klient.plugins.combat

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage


class CombatView(presenter: CombatPreProcessor) {

    val stage = Stage()
    val queueText: Text
    val checkBox: CheckBox
    val startAttacking: CheckBox
    var engagedText: Text

    val weapon = TextArea()
    val saveWeapon = Button("Save Weapon")
    val weaponRotation = TextArea()
    val saveRotation = Button("Save Rotation")

    init {
        stage.title = "Combat"
        presenter.combatSettings.weapon()?.let { weapon.text=it}
        presenter.combatSettings.rotation()?.let { weaponRotation.text=it.joinToString(",")}
        val weaponBox = HBox(weapon, saveWeapon)
        val rotationBox = HBox(weaponRotation, saveRotation)

        weapon.prefRowCount = 1
        weapon.prefWidth = 200.0
        weaponRotation.prefRowCount = 1
        weaponRotation.prefWidth = 200.0
        saveWeapon.setOnMouseClicked { presenter.saveWeapon(weapon.text) }
        saveRotation.setOnMouseClicked {
            val rotation = weaponRotation.text.split(",").map { it.trim() }
            presenter.saveRotation(rotation) }


        checkBox = CheckBox("Auto Combat")
        checkBox.isSelected = false
        checkBox.selectedProperty().addListener { _, _, newValue ->
            presenter.checkBoxClicked(newValue)
        }
        startAttacking = CheckBox("Start attacking")
        startAttacking.isSelected = false
        startAttacking.selectedProperty().addListener { _, _, newValue ->
            if(newValue) {
                presenter.moveToAttack()
            }
        }
        val queueLabel = Label("Action Queue:")
        queueText = Text()
        val engagedLabel = Label("Engaged to:")
        engagedText = Text()
        val combatBox = VBox(
            checkBox,
            startAttacking,
            weaponBox,
            rotationBox,
            queueLabel,
            queueText,
            engagedLabel,
            engagedText
        )
        combatBox.minHeight = 300.0
        combatBox.minWidth = 300.0
        stage.scene = Scene(combatBox)
        stage.show()
    }
}