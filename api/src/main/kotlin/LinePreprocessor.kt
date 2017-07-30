package api

import ro.fortsoft.pf4j.ExtensionPoint

interface LinePreprocessor : ExtensionPoint {

    fun preProcessLine(line: String): String
}