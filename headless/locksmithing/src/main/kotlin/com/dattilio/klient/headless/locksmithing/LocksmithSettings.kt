package com.dattilio.klient.headless.locksmithing

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.io.File

@JsonClass(generateAdapter = true)
data class LocksmithSettingsInternal(
    val port:Int=2323,
    val lock: String? = "lockMacro",
    val unlock: String? = "unlockMacro",
    val jam: String? = "unlockJam",
    val unjam: String? = "unlockUnjam",
    val study: String? = "unlockStudy",
    val recall: String? = "unlockRecall"
)

class LocksmithSettings(private val settingsPath: String) {
    private val settings: LocksmithSettingsInternal by lazy { initSettings() }
    val port: Int by lazy { settings.port }
    val lock: String by lazy { settings.lock!! }
    val unlock: String by lazy { settings.unlock!! }
    val jam: String by lazy { settings.jam!! }
    val unjam: String by lazy { settings.unjam!! }
    val study: String by lazy { settings.study!! }
    val recall: String by lazy { settings.recall!! }
    private val moshiAdapter: JsonAdapter<LocksmithSettingsInternal> = Moshi.Builder()
        .build()
        .adapter(LocksmithSettingsInternal::class.java)

    private fun initSettings(): LocksmithSettingsInternal {
        val locksmithSettingFile = File(settingsPath)
        val isNewFile = locksmithSettingFile.createNewFile()
        return if (isNewFile) {
            LocksmithSettingsInternal().also { File(settingsPath).writeText(moshiAdapter.toJson(it)) }
        } else {
            moshiAdapter.fromJson(File(settingsPath).readText())!!
        }
    }
}