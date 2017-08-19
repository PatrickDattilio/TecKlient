package com.dattilio.klient.api

import com.dattilio.klient.api.model.Status
import ro.fortsoft.pf4j.ExtensionPoint

interface StatusListener : ExtensionPoint {

    fun status(status: Status)
}