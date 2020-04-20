package com.dattilio.klient.plugins.combat

import javafx.scene.media.AudioClip
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

class AlertManager {

    private val chime = AudioClip(javaClass.classLoader.getResource("sounds/chime.wav").toString())
//    private var mediaPlayer = MediaPlayer(chime)

    fun alertWithSound(){
        chime.play()
    }
}
