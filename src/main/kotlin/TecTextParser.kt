import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import org.apache.logging.log4j.LogManager
import java.util.regex.Pattern

class TecTextParser(val view: View) {

    private val logger = LogManager.getLogger()
    val pattern = Pattern.compile("<(.*?)>")
    var listDepth = 0

    data class TextAndStyle(val text: String, val style: ArrayList<String>, val alignment: String? = null)

    fun parseLine(line: String): ArrayList<TextAndStyle> {
        val textList = ArrayList<TextAndStyle>()
        if ("SKOOT" in line) {
            parseSkoot(line)
        } else {
            var styleList = ArrayList<String>()
            var currentFontColor: String? = null
            var currentAlignment: String? = null
            var currentWeight: String? = null

            var newParagraph: Boolean = false
            val segments = segmentLine(line)
            if (segments.toList().size > 1) {
                segments
                        .asSequence()
                        .filterNot { it.trim().isEmpty() }
                        .forEach {
                            if ("<font color" in it) {
                                currentFontColor = "-fx-fill: " + Regex("color=\"(#[0-9a-fA-F]{6})\"").find(it)?.groupValues?.get(1)
                                styleList.add(currentFontColor!!)
                            } else if ("</font>" in it) {
                                styleList.remove(currentFontColor)
                            } else if ("<center>" in it) {
                                currentAlignment = "-fx-text-alignment: center;"
                            } else if ("</center>" in it) {
                                currentAlignment = null
                            } else if ("<b>" in it) {
                                currentWeight = "-fx-font-weight: bold;"
                                styleList.add(currentWeight!!)
                            } else if ("</b>" in it) {
                                styleList.remove(currentWeight)
                            } else if ("<ul>" in it) {
                                listDepth++
                            } else if ("</ul>" in it) {
                                listDepth--
                            } else if ("<li>" in it) {
                                val text = it.replace("<li>", appendTabs())
                                textList.add(TextAndStyle(text, ArrayList(styleList), currentAlignment))
                            } else {
                                textList.add(TextAndStyle(it, ArrayList(styleList), currentAlignment))
                            }
                        }
            } else {
                if ("Either that user does not exist or has a different password." !in line) {

                    textList.add(TextAndStyle(line, styleList, currentAlignment))
                }
            }
        }
        return textList
    }

    fun appendTabs(): String {
        val tabs = StringBuilder()
        for (i in 1..listDepth) {
            tabs.append("    ")

        }
        return tabs.append("* ").toString()
    }


    fun segmentLine(line: String): ArrayList<String> {
        val segments = ArrayList<String>()
        val matcher = pattern.matcher(line)
        var position = 0
        var seg: String
        while (matcher.find()) {
            seg = line.slice(IntRange(position, matcher.start() - 1))
            if (seg.isNotEmpty()) segments.add(seg)
            seg = line.slice(IntRange(matcher.start(), matcher.end() - 1))
            if (seg.isNotEmpty()) segments.add(seg)
            position = matcher.end()
        }
        if (position < line.length) {
            segments.add(line.substring(position))
        }
        return segments
    }

    fun countSubstring(s: String, sub: String): Int = s.split(sub).size - 1

    fun parseSkoot(line: String) {
        val skoot = Regex("SKOOT (\\d+) (.*)").find(line)
        val skootNumber = skoot?.groupValues?.get(1)
        if (skootNumber != null) {
            if (skootNumber == "6") {
                updateMap(skoot.groupValues[2])
            } else if (skootNumber == "7") {
                updateCompass(skoot.groupValues[2])
            } else if (skootNumber == "8") {
                updatePlayerStatus(skoot.groupValues[2])
            } else if (skootNumber == "9") {
                updateLighting(skoot.groupValues[2])
            } else if (skootNumber == "10") {
                updateExits(skoot.groupValues[2])
            } else {
                logger.debug("Unknown SKOOT " + line)
            }
        }

    }

    data class MapData(val x: Double, val y: Double, val size: Double, val color: String)

    private fun updateMap(skoot: String) {
        val mapUpdateString = skoot.split(",")
        val mapUpdate = ArrayList<MapData>()
        val offset = view.mapCanvas.width / 2
        (0..mapUpdateString.size - 1 step 5).mapTo(mapUpdate) {
            MapData(mapUpdateString[it].toDouble() + offset,
                    mapUpdateString[it + 1].toDouble() + offset + mapUpdateString[it + 2].toDouble(),
                    mapUpdateString[it + 2].toDouble(),
                    mapUpdateString[it + 3])
        }

        view.map.stroke = Paint.valueOf("000000")
        for ((x, y, size, color) in mapUpdate) {
            view.map.fill = Paint.valueOf(color)
            view.map.fillRect(x, y, size, size)
            view.map.strokeRect(x, y, size, size)
        }

//        val mapItems =
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateCompass(skoot: String) {
        val compassData = Regex("\\W+").split(skoot)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updatePlayerStatus(skoot: String) {
        val statusData = Regex("\\W+").split(skoot)
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateLighting(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                        arrayOf(x - 3, y + 4.0, x + 3.0, y - 4.0),
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

    private fun updateExits(skoot: String) {
        val exitStrings = skoot.split(",")
        val exitList = ArrayList<ExitData>()

        val offset = view.mapCanvas.width / 2
        (0..exitStrings.size - 1 step 4).mapTo(exitList) {
            ExitData(exitStrings[it].toDouble() + offset,
                    exitStrings[it + 1].toDouble() + offset,
                    exitStrings[2],
                    exitStrings[it + 3])
        }

        for (exitData in exitList) {
            if (exitData.pos != null) {
                view.map.lineWidth = 4.0
                view.map.stroke = exitData.color
                view.map.strokeLine(exitData.pos[1][0], exitData.pos[1][1], exitData.pos[1][2], exitData.pos[1][3])
                view.map.lineWidth = 1.0
                view.map.stroke = Color.BLACK
                view.map.strokeLine(exitData.pos[0][0], exitData.pos[0][1], exitData.pos[0][2], exitData.pos[0][3])
                view.map.strokeLine(exitData.pos[0][0], exitData.pos[0][1], exitData.pos[0][2], exitData.pos[0][3])
            }
        }

    }
}