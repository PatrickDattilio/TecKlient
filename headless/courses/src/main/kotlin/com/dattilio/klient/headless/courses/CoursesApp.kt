package com.dattilio.klient.headless.courses

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking<Unit> {
    val isThreePart = args.firstOrNull()?.toBoolean() ?: true
    val debug: Boolean = args.getOrNull(1)?.toBoolean() ?: false
    CoursesPreProcessor(isThreePart, Dispatchers.IO).setup(debug)
    Unit
}