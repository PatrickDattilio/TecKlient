package com.dattilio.klient.api

interface LinePostprocessor  {

    fun postProcessLine(line: String): String
}