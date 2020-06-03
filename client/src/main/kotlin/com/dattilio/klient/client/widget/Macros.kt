package com.dattilio.klient.client.widget

import com.dattilio.klient.api.SendCommand
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javax.inject.Inject

class Macros @Inject constructor(sender: SendCommand):GridPane() {
    
    init {
        fun Button(text: String, command: String): Button {
            val button = Button(text)
            button.setOnAction { sender.send(command) }
            return button
        }
        add(Button("I", "fe1"), 0, 0)
        add(Button("II", "fe2"), 1, 0)
        add(Button("III", "fe3"), 2, 0)
        add(Button("IV", "fe4"), 3, 0)
        add(Button("V", "fe5"), 4, 0)

        add(Button("VI", "fe6"), 0, 1)
        add(Button("VII", "fe7"), 1, 1)
        add(Button("VIII", "fe8"), 2, 1)
        add(Button("IX", "fe9"), 3, 1)
        add(Button("X", "fe10"), 4, 1)

        add(Button("XI", "fe11"), 0, 2)
        add(Button("XII", "fe12"), 1, 2)
        add(Button("XIII", "fe13"), 2, 2)
        add(Button("XIV", "fe14"), 3, 2)
        add(Button("XV", "fe15"), 4, 2)
    }
}