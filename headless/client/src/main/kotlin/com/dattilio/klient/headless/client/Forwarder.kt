package com.dattilio.klient.headless.client

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) = runBlocking {
    val port: Int = args.getOrNull(0)?.toInt() ?: 2323
    val debug: Boolean = args.getOrNull(1)?.toBoolean() ?: false
    PluginManager(port).setup(debug)
}
