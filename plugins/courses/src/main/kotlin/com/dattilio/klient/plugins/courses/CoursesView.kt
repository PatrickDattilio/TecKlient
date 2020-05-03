package com.dattilio.klient.plugins.courses

import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage


class CoursesView(presenter: CoursesPreProcessor) {

    val stage = Stage()
    val checkBox: CheckBox
    val threePartCheckBox= CheckBox()
    val ropeFailText = Text()
    val plankFailText = Text()
    val pathFailText = Text()
    val successText = Text()
    val successMessageText = Text()
    private val fourPartTotalText= Text()
    private val threePartTotalText= Text()
    private val rope4FailText= Text()
    private val path4FailText= Text()
    private val track4FailText= Text()
    private val coal4FailText= Text()

    init {
        stage.title = "Courses"

        checkBox = CheckBox("Auto Courses")
        checkBox.isSelected = false
        checkBox.selectedProperty().addListener { _, _, newValue ->
            presenter.checkBoxClicked(newValue)
        }

        val combatBox = VBox(
            checkBox,
            HBox(Label("IsThreePartCourse"), threePartCheckBox),
            Label("3-Part"),
            HBox(Label("Rope Fails:"), ropeFailText),
            HBox(Label("Plank Fails:"), plankFailText),
            HBox(Label("Rope Fails:"), pathFailText),
            HBox(Label("Success:"), successText),
            HBox(Label("Total:"), threePartTotalText),
            Label("4-Part"),
            HBox(Label("Rope Fails:"), rope4FailText),
            HBox(Label("Path Fails:"), path4FailText),
            HBox(Label("Track Fails:"), track4FailText),
            HBox(Label("Coal Fails:"), coal4FailText),
            HBox(Label("Success:"), successText),
            HBox(Label("Total:"), fourPartTotalText),
            successMessageText
        )
        combatBox.minHeight = 300.0
        combatBox.minWidth = 300.0
        stage.scene = Scene(combatBox)
        stage.show()
    }

    fun updateFail(fail: CoursesStateMachine.SideEffect.Failed, count: Int) {
        when (fail) {
            is CoursesStateMachine.SideEffect.Failed.RopeFailed -> ropeFailText.text = count.toString()
            is CoursesStateMachine.SideEffect.Failed.PlankFailed -> plankFailText.text = count.toString()
            is CoursesStateMachine.SideEffect.Failed.PathFailed -> pathFailText.text = count.toString()

            is CoursesStateMachine.SideEffect.Failed.Rope4Failed -> rope4FailText.text = count.toString()
            is CoursesStateMachine.SideEffect.Failed.Path4Failed -> path4FailText.text = count.toString()
            is CoursesStateMachine.SideEffect.Failed.Track4Failed -> track4FailText.text = count.toString()
            is CoursesStateMachine.SideEffect.Failed.Coal4Failed -> coal4FailText.text = count.toString()
        }
    }

    fun updateSuccess(count: Int, message:String) {
        successText.text = count.toString()
        successMessageText.text= successMessageText.text+ "\n$message"
    }
    fun isThreePartCourse():Boolean{
        return threePartCheckBox.isSelected
    }
}