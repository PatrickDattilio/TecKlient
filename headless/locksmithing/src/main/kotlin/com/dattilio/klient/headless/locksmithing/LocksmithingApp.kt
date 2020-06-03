package com.dattilio.klient.headless.locksmithing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking<Unit> {
    val locksmithSettingsPath = args.first()
    val debug: Boolean = args.getOrNull(1)?.toBoolean() ?: false
    LocksmithingPreProcessor(Dispatchers.IO,LocksmithSettings(locksmithSettingsPath)).setup(debug)
}