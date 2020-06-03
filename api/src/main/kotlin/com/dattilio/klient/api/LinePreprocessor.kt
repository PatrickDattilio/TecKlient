package com.dattilio.klient.api

interface LinePreprocessor {

    fun preProcessLine(line: String): String
    fun setSendCommand(sendCommand: com.dattilio.klient.api.SendCommand)
}