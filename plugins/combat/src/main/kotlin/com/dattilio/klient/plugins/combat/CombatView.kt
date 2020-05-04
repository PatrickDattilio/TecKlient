package com.dattilio.klient.plugins.combat

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.Stage


class CombatView(presenter: CombatPreProcessor) {

    private val stage = Stage()

    //Options
    private val enabledCheckbox: CheckBox = CheckBox("Enabled")
    private val attackDummyCheckbox: CheckBox = CheckBox("Attack Dummy")
    private val killingBlowCheckBox: CheckBox = CheckBox("Killing blow")
    private val closeGapCheckBox: CheckBox = CheckBox("Close Gap")
    private val gapCloser = TextArea()
    private val gapCloserSaveButton = Button("Save Rotation")

    //
    private val weapon = TextArea()
    private val saveWeapon = Button("Save Weapon")
    private val weaponRotation = TextArea()
    private val saveRotation = Button("Save Rotation")

    init {
        stage.title = "Combat"
        presenter.combatSettings.weapon()?.let { weapon.text = it }
        presenter.combatSettings.rotation()?.let { weaponRotation.text = it.joinToString(",") }
        val weaponBox = HBox(weapon, saveWeapon)
        val rotationBox = HBox(weaponRotation, saveRotation)

        weapon.prefRowCount = 1
        weapon.prefWidth = 200.0
        weaponRotation.prefRowCount = 1
        weaponRotation.prefWidth = 200.0
        saveWeapon.setOnMouseClicked { presenter.saveWeapon(weapon.text) }
        saveRotation.setOnMouseClicked {
            val rotation = weaponRotation.text.split(",").map { it.trim() }
            presenter.saveRotation(rotation)
        }

        enabledCheckbox.isSelected = false
        enabledCheckbox.selectedProperty().addListener { _, _, newValue ->
            presenter.checkBoxClicked(newValue)
        }

        attackDummyCheckbox.isSelected = false
        attackDummyCheckbox.selectedProperty().addListener { _, _, newValue ->
            if (newValue) {
                presenter.moveToAttack()
            }
        }

        killingBlowCheckBox.isSelected = false
        killingBlowCheckBox.selectedProperty().addListener { _, _, newValue ->
            presenter.killingBlowClicked(newValue)
        }

        closeGapCheckBox.selectedProperty().addListener { _, _, newValue ->
            presenter.gapCloserClicked(newValue)
        }

        gapCloser.prefRowCount = 1
        gapCloser.prefWidth = 200.0
        gapCloserSaveButton.setOnMouseClicked { presenter.saveGapCloser(gapCloser.text) }

        val combatBox = VBox(
            enabledCheckbox,
            weaponBox,
            rotationBox,
            attackDummyCheckbox,
            killingBlowCheckBox,
            closeGapCheckBox,
            HBox(gapCloser, gapCloserSaveButton)
        )
        combatBox.minHeight = 300.0
        combatBox.minWidth = 300.0
        stage.scene = Scene(combatBox)
        stage.show()
    }
}