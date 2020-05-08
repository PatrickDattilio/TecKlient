package com.dattilio.klient

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class Login @Inject constructor(private val okHttpClient: OkHttpClient) {

    data class Credentials(val username: String, val password: String)

    suspend fun getGameServerCredentialsFromLogin(credentials: Credentials) = withContext(Dispatchers.IO) {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("uname", credentials.username)
            .addFormDataPart("pwd", credentials.password)
            .addFormDataPart("phrase", "")
            .addFormDataPart("submit", "true")
            .build()

        val loginRequest = Request.Builder()
            .url("https://login.eternalcitygame.com/login.php".toHttpUrlOrNull()!!)
            .header(
                "User-Agent",
                "'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36'"
            )
            .header("Cookie", "biscuit=test")
            .post(requestBody)
            .build()
        val response = okHttpClient.newCall(loginRequest).execute()
        val responseHeaders = response.headers
        val cookies = responseHeaders.values("Set-Cookie")
        var user = ""
        var pass = ""
        for (cookie in cookies) {
            val userMatch = Regex("user=(.*?);").find(cookie)
            val passMatch = Regex("pass=(.*?);").find(cookie)
            if (userMatch != null) {
                user = userMatch.groupValues[1]
            }
            if (passMatch != null) {
                pass = passMatch.groupValues[1]
            }
        }
        Credentials(user, pass)
    }
}
