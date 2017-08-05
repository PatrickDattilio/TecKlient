package com.dattilio.klient.widget

import javafx.scene.layout.VBox
import javax.inject.Inject

class Controls @Inject constructor(val map: Map,
                                   val compass: Compass,
                                   macros: Macros) : VBox() {

    init {
        children.addAll(macros, map, compass)
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