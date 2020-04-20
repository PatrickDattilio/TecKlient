package com.dattilio.klient.widget

import javafx.scene.layout.VBox
import javax.inject.Inject

class Controls @Inject constructor(val mapWidget: MapWidget,
                                   val compass: Compass,
                                   macros: Macros,
                                   val statusWidget: StatusWidget) : VBox() {

    init {
        children.addAll(macros, mapWidget, compass, statusWidget)
    }

    fun updateMap(skoot: String) {
        mapWidget.updateMap(skoot)
    }

    fun updateExits(skoot: String) {
        mapWidget.updateExits(skoot)
    }

    fun updateCompass(skoot: String) {
        compass.updateCompass(skoot)
    }

    fun updatePlayerStatus(skoot: String) {
        val statusData = Regex("\\W+").split(skoot)
        statusWidget.update(statusData)
    }

    fun updateLighting(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}