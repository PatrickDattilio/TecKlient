package com.dattilio.klient.client//import org.apache.logging.log4j.LogManager
import com.dattilio.klient.client.widget.Controls
import java.util.regex.Pattern

class TecTextParser(private val controls: Controls) {

    //    private val logger = LogManager.getLogger()
    private val pattern: Pattern = Pattern.compile("<(.*?)>")
    private var listDepth = 0

    data class TextAndStyle(val text: String, val style: ArrayList<String>, val alignment: String? = null)

    fun parseLine(line: String): ArrayList<TextAndStyle> {

        val textList = ArrayList<TextAndStyle>()
        if ("SKOOT" in line) {
            parseSkoot(line)
        } else {
            val styleList = ArrayList<String>()
            var currentFontColor = ""
            var currentAlignment: String? = null
            var currentWeight: String? = null
            styleList.add(currentFontColor)

            val segments = segmentLine(line)
            segments
                .asSequence()
                .filterNot { it.trim().isEmpty() }
                .forEach {
                    when {
                        "<font color" in it -> {
                            currentFontColor =
                                "-fx-fill: " + Regex("color=\"(#[0-9a-fA-F]{6})\"").find(it)?.groupValues?.get(1)
                            styleList.add(currentFontColor)
                        }
                        "</font>" in it -> {
                            styleList.remove(currentFontColor)
                        }
                        "<center>" in it -> {
                            currentAlignment = "-fx-text-alignment: center;"
                        }
                        "</center>" in it -> {
                            currentAlignment = null
                        }
                        "<b>" in it -> {
                            currentWeight = "-fx-font-weight: bold;"
                            styleList.add(currentWeight!!)
                        }
                        "</b>" in it -> {
                            styleList.remove(currentWeight)
                        }
                        "<ul>" in it -> {
                            listDepth++
                        }
                        "</ul>" in it -> {
                            listDepth--
                        }
                        "<li>" in it -> {
                            val text = it.replace("<li>", appendTabs())
                            textList.add(
                                TextAndStyle(
                                    text,
                                    ArrayList(styleList),
                                    currentAlignment
                                )
                            )
                        }
                        "<hr>" in it -> {
                            val builder = StringBuilder()
                            for (i in 1..it.length) {
                                builder.append("-")
                            }
                            textList.add(
                                TextAndStyle(
                                    builder.toString(),
                                    ArrayList(styleList),
                                    currentAlignment
                                )
                            )
                        }
                        "<pre>" in it -> { //ignore
                        }
                        "</pre>" in it -> { //ignore

                        }
                        else -> {
                            textList.add(
                                TextAndStyle(
                                    it.replace("&gt;", ">")
                                        .replace("&lt;", "<")
                                        .replace("&quot;", "\""), ArrayList(styleList), currentAlignment
                                )
                            )
                        }
                    }
                }
        }
        return textList
    }


    private fun appendTabs(): String {
        val tabs = StringBuilder()
        for (i in 1..listDepth) {
            tabs.append("    ")

        }
        return tabs.append("* ").toString()
    }


    private fun segmentLine(line: String): ArrayList<String> {
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


    private fun parseSkoot(line: String) {
        val skoot = Regex("SKOOT (\\d+) (.*)").find(line)
        val skootNumber = skoot?.groupValues?.get(1)
        if (skootNumber != null) {
            when (skootNumber) {
                "6" -> controls.updateMap(skoot.groupValues[2])
                "7" -> controls.updateCompass(skoot.groupValues[2])
                "8" -> controls.updatePlayerStatus(skoot.groupValues[2])
                "9" -> controls.updateLighting(skoot.groupValues[2])
                "10" -> controls.updateExits(skoot.groupValues[2])
                else -> {
                    //                logger.debug("Unknown SKOOT $line")
                }
            }
        }

    }


}