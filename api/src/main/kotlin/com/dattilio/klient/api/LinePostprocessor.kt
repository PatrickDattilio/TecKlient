package com.dattilio.klient.api

import ro.fortsoft.pf4j.ExtensionPoint

interface LinePostprocessor : ExtensionPoint {

    fun postProcessLine(line: String): String
}