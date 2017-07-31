package com.dattilio.klient

import com.dattilio.klient.api.LinePreprocessor
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.logging.log4j.LogManager
import ro.fortsoft.pf4j.DefaultPluginManager
import toHexString
import widget.Controls
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.Paths
import java.security.MessageDigest


class TecClient : Application() {
    private val logger = LogManager.getLogger()

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            launch(TecClient::class.java)
        }
    }

    val okHttp = OkHttpClient.Builder()
            .followRedirects(false)
            .build()

    var socket: Socket? = null
    var user = ""
    var pass = ""
    val controls = Controls(this::send)
    val view = View(controls)
    val parser = TecTextParser(controls)
    val pluginManager = DefaultPluginManager(Paths.get("app/plugins"))

    override fun start(primaryStage: Stage?) {
        pluginManager.loadPlugins()
        pluginManager.startPlugins()

        System.out.println("Plugindirectory: ");
        System.out.println("\t" + System.getProperty("pf4j.pluginsDir", "plugins") + "\n")
        val greetings = pluginManager.getExtensions(LinePreprocessor::class.java)
        view.setupUI(primaryStage)
        getCredentials()
    }

    private fun getCredentials() {
        askForCredentials()
    }

    fun askForCredentials() {
        view.addText("Please enter your username:")
        view.textArea.onKeyPressed = usernameInputHandler()
        view.textArea.onKeyReleased = clearHandler()
    }

    private fun usernameInputHandler(): EventHandler<KeyEvent> {
        return EventHandler { event ->
            if (event.code == KeyCode.ENTER) {
                val username = view.textArea.getText()
                view.textArea.clear()
                view.addText("Please enter your password:")
                view.textArea.onKeyPressed = passwordInputHandler(username)
            }
        }
    }

    private fun clearHandler(): EventHandler<in KeyEvent>? {
        return EventHandler { event ->
            if (event.code == KeyCode.ENTER) {
                view.textArea.clear()
            }
        }
    }

    private fun passwordInputHandler(username: String): EventHandler<KeyEvent> {
        return EventHandler { event ->
            if (event.code == KeyCode.ENTER) {
                val password = view.textArea.getText()
                view.textArea.clear()
                loginToWebsite(username, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe({ (user, pass) ->
                            if (user.isNullOrEmpty() or pass.isNullOrEmpty()) {
                                view.addText("I'm sorry, that username or password was incorrect. Please try again.")
                                askForCredentials()
                            } else {
                                gameConnect()
                            }
                        })

            }
        }
    }

    data class Login(val username: String, val password: String)

    private fun loginToWebsite(username: String, password: String): Observable<Login> {
        return Observable.fromCallable {
            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("uname", username)
                    .addFormDataPart("pwd", password)
                    .addFormDataPart("phrase", "")
                    .addFormDataPart("submit", "true")
                    .build()


            val loginRequest = Request.Builder()
                    .url(HttpUrl.parse("https://www.skotos.net/user/login.php"))
                    .header("User-Agent", "'Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36'")
                    .header("Cookie", "biscuit=test")
                    .post(requestBody)
                    .build()
            val response = okHttp.newCall(loginRequest).execute()
            val responseHeaders = response.headers()
            val cookies = responseHeaders.values("Set-Cookie")
            for (cookie in cookies) {
                val userMatch = Regex("user=(.*?);").find(cookie)
                val passMatch = Regex("pass=(.*?);").find(cookie)
                if (userMatch != null) {
                    user = userMatch.groupValues.get(1)
                }
                if (passMatch != null) {
                    pass = passMatch.groupValues.get(1)
                }
            }

            return@fromCallable Login(user, pass)
        }
    }

    fun gameConnect() {
        view.textArea.onKeyPressed = gameInputHandler()
        Observable.create({ emitter: ObservableEmitter<String> ->
            socket = Socket("tec.skotos.net", 6730)
            send("SKOTOS Zealous 0.7.12.2\n")
            var line: String
            val reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
            try {
                line = reader.readLine()
                while (true) {
                    if (!line.isNullOrEmpty()) {
                        emitter.onNext(line)
                        logger.debug(line)
                    }
                    line = reader.readLine()
                }
            } catch (e: IOException) {
                emitter.onError(e)
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(this::handleMessage, { t -> t.printStackTrace() })
    }

    private fun handleMessage(line: String) {
        if (line.isNotEmpty()) {
            if (!line.contains("SECRET", ignoreCase = false)) {
//                view.addText(line)

//                val preProcesssedLine = api.preProcessLine(line)
                val textAndStyle = parser.parseLine(line)
                view.addTextWithStyle(textAndStyle)
//                api.postProcessLine(preProcesssedLine)
            } else {
                clientLogin(line)
            }
        }
    }

    private fun clientLogin(line: String) {
        val secret = line.substring(7).trim()
        val hash_string = user + pass + secret
        val zealous_hash = MessageDigest.getInstance("MD5").digest(hash_string.toByteArray()).toHexString()
        send("USER " + user)
        send("SECRET " + secret)
        send("HASH " + zealous_hash)
        send("CHAR ")
//                # After a Zealotry login the server still sends a password prompt.This just responds to
//                # that with a dummy entry.
        send("")
    }

    private fun send(text: String) {
        view.addText(text)
        socket?.getOutputStream()?.write((text + "\r\n").toByteArray(Charset.forName("utf-8")))
        socket?.getOutputStream()?.flush()
    }

    private fun gameInputHandler(): EventHandler<KeyEvent> {
        return EventHandler { event ->
            if (event.code == KeyCode.ENTER) {
                send(view.textArea.text)
                view.textArea.clear()
            }
        }
    }
}

