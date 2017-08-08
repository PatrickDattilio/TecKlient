package com.dattilio.klient.widget

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javax.inject.Inject

class Map @Inject constructor(): Canvas() {
    val map: GraphicsContext = graphicsContext2D

    init {
        height = 120.0
        width = 120.0
        map.fill = Paint.valueOf("000000")
        map.fillRect(0.0, 0.0, width, height)
    }

    data class MapData(val x: Double, val y: Double, val size: Double, val color: String)

    fun updateMap(skoot: String) {
        map.fill = Color.BLACK
        map.fillRect(0.0, 0.0, width, height)
        val mapUpdateString = skoot.split(",")
        val mapUpdate = ArrayList<MapData>()
        val offset = width / 2
        (0..mapUpdateString.size - 1 step 5).mapTo(mapUpdate) {
            MapData(mapUpdateString[it].toDouble() + offset,
                    mapUpdateString[it + 1].toDouble() + offset,
                    mapUpdateString[it + 2].toDouble(),
                    mapUpdateString[it + 3])
        }

        map.stroke = Paint.valueOf("000000")
        for ((x, y, size, color) in mapUpdate) {
            map.fill = Paint.valueOf(color)
            map.fillRect(x, y, size, size)
            map.strokeRect(x, y, size, size)
        }
    }

    data class ExitData(val x: Double, val y: Double, val orient: String, val colorString: String) {
        val color: Color = if (colorString == "1") Color.WHITE else Color.BLACK
        val pos: Array<Array<Double>>? = computeExitPositions(x, y, orient)


        fun computeExitPositions(x: Double, y: Double, orient: String): Array<Array<Double>>? {
            if (orient == "ver") {
                return arrayOf(
                        arrayOf(x - 1.0, y + 5.0, x - 1.0, y - 5.0),
                        arrayOf(x, y + 5.0, x, y - 5.0),
                        arrayOf(x + 1.0, y + 5.0, x + 1.0, y - 5.0))
            } else if (orient == "hor") {

                return arrayOf(
                        arrayOf(x + 5.0, y - 1.0, x - 5.0, y - 1.0),
                        arrayOf(x + 5.0, y, x - 5.0, y),
                        arrayOf(x + 5.0, y + 1.0, x - 5.0, y + 1.0))
            } else if ((orient == "ne").or(orient == "sw")) {
                return arrayOf(
                        arrayOf(x - 3.0, y + 4.0, x + 3.0, y - 4.0),
                        arrayOf(x - 3.0, y + 3.0, x + 3.0, y - 3.0),
                        arrayOf(x - 3.0, y + 1.0, x + 3.0, y - 1.0))
            } else if ((orient == "nw").or(orient == "se")) {
                return arrayOf(
                        arrayOf(x - 3.0, y - 4.0, x + 3.0, y + 4.0),
                        arrayOf(x - 3.0, y - 3.0, x + 3.0, y + 3.0),
                        arrayOf(x - 3.0, y - 1.0, x + 3.0, y + 1.0))
            }
            return null
        }
    }

    fun updateExits(skoot: String) {
        val exitStrings = skoot.split(",")
        val exitList = ArrayList<ExitData>()

        val offset = width / 2
        (0..exitStrings.size - 1 step 4).mapTo(exitList) {
            ExitData(exitStrings[it].toDouble() + offset,
                    exitStrings[it + 1].toDouble() + offset,
                    exitStrings[it + 2],
                    exitStrings[it + 3])
        }

        for (exitData in exitList) {
            if (exitData.pos != null) {
                map.lineWidth = 2.0
                map.stroke = exitData.color
                map.strokeLine(exitData.pos[1][0], exitData.pos[1][1], exitData.pos[1][2], exitData.pos[1][3])
                map.lineWidth = 1.0
                map.stroke = Color.BLACK
                map.strokeLine(exitData.pos[0][0], exitData.pos[0][1], exitData.pos[0][2], exitData.pos[0][3])
                map.strokeLine(exitData.pos[2][0], exitData.pos[2][1], exitData.pos[2][2], exitData.pos[2][3])
            }
        }

    }
}
