package com.dattilio.klient.api

import com.dattilio.klient.api.model.Status

interface StatusListener  {

    fun status(status: Status)
}