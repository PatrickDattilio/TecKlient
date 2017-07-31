package com.dattilio.klient.api

import ro.fortsoft.pf4j.ExtensionPoint

interface SendCommand : ExtensionPoint {

    fun sendCommand(send: (String)->Unit)
}