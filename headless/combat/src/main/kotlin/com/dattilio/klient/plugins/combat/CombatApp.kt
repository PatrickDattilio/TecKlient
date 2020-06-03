package com.dattilio.klient.plugins.combat

import kotlinx.coroutines.runBlocking

fun main(args: Array<String>): Unit = runBlocking<Unit> {
    val combatSettingsPath = args.first()
    val debug: Boolean = args.getOrNull(1)?.toBoolean() ?: false
    CombatPreProcessor(CombatSettings(combatSettingsPath), coroutineContext).run(debug)
}
