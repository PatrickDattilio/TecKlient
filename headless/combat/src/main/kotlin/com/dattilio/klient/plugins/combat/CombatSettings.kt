package com.dattilio.klient.plugins.combat

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File

@JsonClass(generateAdapter = true)
data class CombatSettingsInternal(
    val weapon: String? = "yourWeapon",
    val rotation: List<String>? = listOf("attackMacro1", "attackMacro2"),
    val gapCloser: String? = "gapCloserMacro",
    val approach: String? = "approachMacro"
)

class CombatSettings(val settingsPath: String) {
    private var settings: CombatSettingsInternal? = null
    fun weapon() = settings?.weapon
    fun rotation() = settings?.rotation
    fun gapCloser() = settings?.gapCloser
    private val moshiAdapter: JsonAdapter<CombatSettingsInternal> = Moshi.Builder()
        .build()
        .adapter(CombatSettingsInternal::class.java)

    init {
        val combatSettingFile = File(settingsPath)
        val isNewFile = combatSettingFile.createNewFile()
        settings = if (isNewFile) {
            CombatSettingsInternal().also { File(settingsPath).writeText(moshiAdapter.toJson(it)) }
        } else {
            moshiAdapter.fromJson(File(settingsPath).readText())
        }
    }

    fun updateWeapon(weapon: String) {
        settings = settings?.copy(weapon = weapon) ?: CombatSettingsInternal(weapon = weapon)
        GlobalScope.async {
            File(settingsPath).writeText(moshiAdapter.toJson(settings))
        }
    }

    fun updateRotation(rotation: List<String>) {
        settings = settings?.copy(rotation = rotation) ?: CombatSettingsInternal(rotation = rotation)
        GlobalScope.async {
            File(settingsPath).writeText(moshiAdapter.toJson(settings))
        }
    }

    fun updateGapCloser(gapCloser: String) {
        settings = settings?.copy(gapCloser = gapCloser) ?: CombatSettingsInternal(gapCloser = gapCloser)
        GlobalScope.async {
            File(settingsPath).writeText(moshiAdapter.toJson(settings))
        }
    }
}
