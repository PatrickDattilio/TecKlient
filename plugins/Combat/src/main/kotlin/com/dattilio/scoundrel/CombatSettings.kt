package com.dattilio.scoundrel

import com.squareup.moshi.Moshi
import java.io.File

data class CombatSettingsInternal(val weapon: String, val rotation: Array<String>)
class CombatSettings(settingsPath: String) {
    val weapon: String
    val rotation: Array<String>

    init {
        var combatSettingsInternal = Moshi.Builder().build().adapter(CombatSettingsInternal::class.java).fromJson(File(settingsPath).readText())!!
        weapon = combatSettingsInternal.weapon
        rotation = combatSettingsInternal.rotation
    }
}
