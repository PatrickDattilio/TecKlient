package com.dattilio.klient//import org.apache.logging.log4j.Logger
import com.dattilio.klient.api.SendCommand
import com.dattilio.klient.widget.Controls
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.javafx.JavaFx
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetSocketAddress
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@ExperimentalCoroutinesApi
class TecClient @Inject constructor(
    sendCommand: SendCommand,
//                                    val logger: Logger,
    private val pluginManager: PluginManager,
    private val okHttp: OkHttpClient,
    controls: Controls,
    val view: View
) : CoroutineScope {

    private lateinit var gameConnection: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    private var socket: Socket? = null
    private var user = ""
    private var pass = ""
    private val parser = TecTextParser(controls)

    private var saveToHistory: Boolean = false
    private var previousCommandIndex: Int = 0
    private val commandHistory = LinkedList<String>()

    init {
        sendCommand.commands.subscribe(this::send) { throwable -> println(throwable) }
    }

    private val usernameHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.ENTER) {
            event.consume()
            getUsername()
        }
    }
    private val passwordHandler: EventHandler<KeyEvent> = EventHandler { event ->
        if (event.code == KeyCode.ENTER) {
            event.consume()
            getPassword()
        }
    }

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
        pass = view.textArea.text
        view.textArea.clear()
        loginToWebsite(user, pass)

    }

    data class Login(val username: String, val password: String)

    private fun loginToWebsite(username: String, password: String) {
        Observable.fromCallable {
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uname", username)
                .addFormDataPart("pwd", password)
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
            val response = okHttp.newCall(loginRequest).execute()
            val responseHeaders = response.headers
            val cookies = responseHeaders.values("Set-Cookie")
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

            return@fromCallable Login(user, pass)
        }.subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())
            .subscribe { (user, pass) ->
                if (user.isEmpty() or pass.isEmpty()) {
                    view.addText("I'm sorry, that username or password was incorrect. Please try again.")
                    getCredentials()
                } else {
                    gameConnect()
                }
            }
    }

    @ExperimentalCoroutinesApi
    var writeToTec = Channel<String>(10)

    private suspend fun setupGameWriter(output: ByteWriteChannel) {
        writeToTec.consumeEach {
            output.writeStringUtf8("$it\r\n")
            println(it)
        }
    }

    private suspend fun setupGameReader(socket: Socket) {
        val input = socket.openReadChannel()
        try {
            while (true) {
                val line = input.readUTF8Line()
                line?.let {
                    println(it)
                    handleMessage(it)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            view.isConnected(false)
        }
    }

    private suspend fun connectToServerAsync() = coroutineScope {
        socket =
            aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
                .connect(InetSocketAddress("tec.skotos.net", 6730))
        socket?.let {
            val output = it.openWriteChannel(autoFlush = true)
            output.writeStringUtf8("SKOTOS Orchil 0.2.3\r\n")
            view.isConnected(true)
            launch { setupGameReader(it) }
            launch { setupGameWriter(output) }
        }
    }

    private fun gameConnect() {
        setupGameInputHandler()
        gameConnection = launch {
            connectToServerAsync()
        }
    }

    private fun handleMessage(line: String) {
        if (line.isNotEmpty()) {
            if (!line.contains("SECRET", ignoreCase = false)) {
                val textAndStyle = parser.parseLine(line)
                view.addTextWithStyle(textAndStyle)
                if (!line.contains("SKOOT ")) {
                    GlobalScope.async {
                        pluginManager.postProcessLine(line)
                    }
                }
            } else {
                clientLogin(line)
            }
        }
    }

    private fun clientLogin(line: String) {
        GlobalScope.async {
            val secret = line.substring(7).trim()
            val hashString = user + pass + secret
            val zealousHash = MessageDigest.getInstance("MD5").digest(hashString.toByteArray()).toHexString()
            saveToHistory = false
            writeToTec.send("USER $user")
            writeToTec.send("SECRET $secret")
            writeToTec.send("HASH $zealousHash")
            writeToTec.send("CHAR ")
//                # After a Zealotry login the server still sends a password prompt.This just responds to
//                # that with a dummy entry.
            saveToHistory = true
            writeToTec.send("")
        }
    }

    private fun send(text: String) {
        if (saveToHistory) {
            commandHistory.addLast(text)
//            previousCommandIndex = commandHistory.lastIndex
            view.addText(text)
        }
        sendToServer(text)
    }

    private fun sendToServer(text: String) {
        GlobalScope.async {
            writeToTec.send(text)
        }
    }

    val gameInputHandler = GameInputHandler { key: KeyEvent ->
        when (key.code) {
            KeyCode.ENTER -> {
                val command = view.textArea.text
                send(command)
                view.textArea.clear()
                key.consume()
                previousCommandIndex = 0
            }
            KeyCode.UP -> {
                saveToHistory = false
                view.textArea.text = getPreviousCommand(1)
            }
            KeyCode.DOWN -> {
                saveToHistory = false
                view.textArea.text = getPreviousCommand(-1)
            }
            else -> {
                saveToHistory = true
            }
        }
    }

    private fun setupGameInputHandler() {
        view.textArea.addEventFilter(KeyEvent.KEY_PRESSED, gameInputHandler)
    }


    private fun getPreviousCommand(direction: Int): String {
        val newPrevIndex = previousCommandIndex + direction
        if (commandHistory.size == 0) return ""
        previousCommandIndex = when {
            newPrevIndex < 0 -> {
                commandHistory.size + newPrevIndex
            }
            newPrevIndex > commandHistory.lastIndex -> {
                newPrevIndex - commandHistory.size
            }
            else -> {
                newPrevIndex
            }
        }
        return commandHistory[previousCommandIndex]
    }

    fun start() {
        getCredentials()
    }

    fun reconnect() {
        view.gameScreen.clear()
        view.textArea.removeEventFilter(KeyEvent.KEY_PRESSED, gameInputHandler)
        writeToTec = Channel<String>(10)
        gameConnect()
    }

    fun disconnect() {
        gameConnection.cancel()
        socket?.close()
        socket = null
        view.isConnected(false)
    }
}

class GameInputHandler(private val function: (KeyEvent) -> Unit) : EventHandler<KeyEvent> {
    override fun handle(event: KeyEvent) {
        function.invoke(event)
    }
}

