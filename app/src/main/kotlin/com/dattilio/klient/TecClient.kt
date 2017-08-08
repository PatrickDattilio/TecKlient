package com.dattilio.klient

import com.dattilio.klient.api.SendCommand
import com.dattilio.klient.widget.Controls
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import okhttp3.HttpUrl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.logging.log4j.Logger
import toHexString
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject


class TecClient @Inject constructor(val sendCommand: SendCommand,
                                    val logger: Logger,
                                    val okHttp: OkHttpClient,
                                    val controls: Controls,
                                    val view: View) {

    var socket: Socket? = null
    var user = ""
    var pass = ""
    val pluginManager = PluginManager(sendCommand)
    val parser = TecTextParser(controls)


    private var saveToHistory: Boolean = false
    private var previousCommandIndex: Int = 0
    private val commandHistory = LinkedList<String>()

    init {
        sendCommand.commands.subscribe(this::send, logger::error)
    }

    val usernameHandler: EventHandler<KeyEvent> = EventHandler({
        event ->
        if (event.code == KeyCode.ENTER) {
            event.consume()
            getUsername()
        }
    })
    val passwordHandler: EventHandler<KeyEvent> = EventHandler({
        event ->
        if (event.code == KeyCode.ENTER) {
            event.consume()
            getPassword()
        }
    })

    private fun getCredentials() {
        view.addText("Please enter your username:")
        view.textArea.addEventFilter(KeyEvent.KEY_PRESSED, usernameHandler)
    }

    private fun getUsername() {
        view.textArea.removeEventFilter(KeyEvent.KEY_PRESSED, usernameHandler)
        user = view.textArea.text
        view.textArea.clear()
        view.addText("Please enter your password:")
        view.textArea.addEventFilter(KeyEvent.KEY_PRESSED, passwordHandler)
    }

    private fun getPassword() {
        view.textArea.removeEventFilter(KeyEvent.KEY_PRESSED, passwordHandler)
        val password = view.textArea.text
        view.textArea.clear()
        loginToWebsite(user, password)
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe({ (user, pass) ->
                    if (user.isNullOrEmpty() or pass.isNullOrEmpty()) {
                        view.addText("I'm sorry, that username or password was incorrect. Please try again.")
                        getCredentials()
                    } else {
                        gameConnect()
                    }
                })

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
        setupGameInputHandler()
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

                val preProcessedLine = pluginManager.preProcessLine(line)
                val textAndStyle = parser.parseLine(preProcessedLine)
                view.addTextWithStyle(textAndStyle)
                pluginManager.postProcessLine(preProcessedLine)
            } else {
                clientLogin(line)
            }
        }
    }

    private fun clientLogin(line: String) {
        val secret = line.substring(7).trim()
        val hash_string = user + pass + secret
        val zealous_hash = MessageDigest.getInstance("MD5").digest(hash_string.toByteArray()).toHexString()
        saveToHistory = false
        send("USER " + user)
        send("SECRET " + secret)
        send("HASH " + zealous_hash)
        send("CHAR ")
//                # After a Zealotry login the server still sends a password prompt.This just responds to
//                # that with a dummy entry.
        saveToHistory = true
        send("")
    }

    private fun send(text: String) {
        if (saveToHistory) {
            commandHistory.addLast(text)
            previousCommandIndex = commandHistory.lastIndex
        }
        view.addText(text)
        socket?.getOutputStream()?.write((text + "\r\n").toByteArray(Charset.forName("utf-8")))
        socket?.getOutputStream()?.flush()
    }


    private fun setupGameInputHandler() {
        view.textArea.addEventFilter(KeyEvent.KEY_PRESSED, { key ->
            if (key.code == KeyCode.ENTER) {
                val command = view.textArea.text
                send(command)
                view.textArea.clear()
                key.consume()
            } else if (key.code == KeyCode.UP) {
                saveToHistory = false
                view.textArea.text = getPreviousCommand(1)
            } else if (key.code == KeyCode.DOWN) {
                saveToHistory = false
                view.textArea.text = getPreviousCommand(-1)
            } else {
                saveToHistory = true
            }
        })
    }

    private fun getPreviousCommand(direction: Int): String {
        val newPrevIndex = previousCommandIndex + direction

        if (newPrevIndex < 0) {
            previousCommandIndex = commandHistory.size + newPrevIndex
        } else if (newPrevIndex > commandHistory.lastIndex) {
            previousCommandIndex = newPrevIndex - commandHistory.size
        } else {
            previousCommandIndex = newPrevIndex
        }
        return commandHistory[previousCommandIndex]
    }

    fun start() {
        getCredentials()
    }
}

