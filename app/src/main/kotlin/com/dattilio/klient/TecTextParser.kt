package com.dattilio.klient

import org.apache.logging.log4j.LogManager
import widget.Controls
import java.util.regex.Pattern

class TecTextParser(val controls: Controls) {

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
                controls.updateMap(skoot.groupValues[2])
            } else if (skootNumber == "7") {
                controls.updateCompass(skoot.groupValues[2])
            } else if (skootNumber == "8") {
                controls.updatePlayerStatus(skoot.groupValues[2])
            } else if (skootNumber == "9") {
                controls.updateLighting(skoot.groupValues[2])
            } else if (skootNumber == "10") {
                controls.updateExits(skoot.groupValues[2])
            } else {
                logger.debug("Unknown SKOOT " + line)
            }
        }

    }


}