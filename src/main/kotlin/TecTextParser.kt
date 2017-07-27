import mu.KotlinLogging
import java.util.regex.Pattern

class TecTextParser {

    private val logger = KotlinLogging.logger {}

    val pattern = Pattern.compile("<(.*?)>")
    //    val tagRegex = Regex()
    var listDepth = 0
    val fontColorPattern = Pattern.compile("font color=\"(#[0-9a-fA-F]{6})\"")

    data class TextAndStyle(val text: String, val style: String?)

    fun parseLine(line: String): ArrayList<TextAndStyle> {
        val textList = ArrayList<TextAndStyle>()
        if ("SKOOT" in line) {
            parseSkoot(line)
        } else {
            if (listDepth > 0) {
                listDepth -= countSubstring(line, "</ul>")
            }
            val lineNoEndTags = line.replace(Regex("</.*?>"), "")
            val tags = ArrayList<String>()
            var currentFontColor: String? = null

            val segments = segmentLine(line)
            if (segments.toList().size > 1) {
                segments
                        .asSequence()
                        .filterNot { it.trim().isEmpty() }
                        .forEach {
                            if ("<font color" in it) {
                                currentFontColor = Regex("color=\"(#[0-9a-fA-F]{6})\"").find(it)?.groupValues?.get(1)
                            } else if ("</font>" in it) {
                                currentFontColor = null
                            } else {
                                var style: String? = null
                                if (currentFontColor != null) {
                                    style = "-fx-fill: " + currentFontColor +";"
                                }
                                textList.add(TextAndStyle(it, style))
                            }
                        }
            } else {
                if ("Either that user does not exist or has a different password." !in line) {
                    var style: String? = null
                    if (currentFontColor != null) {
                        style = "-fx-fill: " + currentFontColor + currentFontColor +";"
                    }
                    textList.add(TextAndStyle(line, style))
                }
            }
        }
        return textList
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
                logger.debug { "Unknown SKOOT $line" }
            }
        }

    }


    private fun updateMap(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateCompass(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updatePlayerStatus(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateLighting(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateExits(skoot: String) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}