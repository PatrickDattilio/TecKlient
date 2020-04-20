package  com.dattilio.klient.plugins.map

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer

class AlertManager {

    private val chime = Media(javaClass.classLoader.getResource("sounds/chime.wav").toString())
    private var mediaPlayer = MediaPlayer(chime)

    fun alertWithSound(){
        mediaPlayer.play()
    }
}
