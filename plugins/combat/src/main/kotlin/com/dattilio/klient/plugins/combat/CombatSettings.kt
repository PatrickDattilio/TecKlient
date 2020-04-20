package com.dattilio.klient.plugins.combat

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File

@JsonClass(generateAdapter = true)
data class CombatSettingsInternal(val weapon: String?, val rotation: List<String>?)
class CombatSettings(val settingsPath: String) {
   private var settings : CombatSettingsInternal? = null
    val weapon = settings?.weapon
    val rotation = settings?.rotation
    val moshiAdapter = Moshi.Builder()
        .build()
        .adapter(CombatSettingsInternal::class.java)

    init {
        println("Path: ${File(".").absolutePath}")
        settings = moshiAdapter.fromJson(
            File(
                settingsPath
            ).readText()
        )
    }

    fun updateWeapon(weapon: String) {
        settings = settings?.copy(weapon=weapon)?: CombatSettingsInternal(weapon= weapon, rotation = null)
        GlobalScope.async {
            File(settingsPath).writeText(moshiAdapter.toJson(settings))
        }
    }

    fun updateRotation(rotation: List<String>) {
        settings = settings?.copy(rotation=rotation)?: CombatSettingsInternal(null, rotation=rotation)
        GlobalScope.async {
            File(settingsPath).writeText(moshiAdapter.toJson(settings))
        }
    }
}
