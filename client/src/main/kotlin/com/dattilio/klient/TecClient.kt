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
import javafx.event.EventHandler
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.javafx.JavaFx
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


interface Presenter {

}

@ExperimentalCoroutinesApi
class TecClient @Inject constructor(
    sendCommand: SendCommand,
    private val pluginManager: PluginManager,
    private val okHttp: OkHttpClient,
    val login: Login = Login(okHttp),
    controls: Controls,
    val view: View
) : Presenter, CoroutineScope {

    private lateinit var gameCredentials: Login.Credentials
    private lateinit var gameConnection: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.JavaFx

    private var socket: Socket? = null
//    private var user = ""
//    private var pass = ""
    private val parser = TecTextParser(controls)

    private var saveToHistory: Boolean = false
    private var previousCommandIndex: Int = 0
    private val commandHistory = LinkedList<String>()

    init {
        sendCommand.commands.subscribe(this::send) { throwable -> println(throwable) }
    }


    @ExperimentalCoroutinesApi
    var writeToTec = Channel<String>(10)

    private suspend fun setupGameWriter(output: ByteWriteChannel) = withContext(Dispatchers.IO) {
        writeToTec.consumeEach {
            output.writeStringUtf8("$it\r\n")
            println(it)
        }
    }

    private suspend fun setupGameReader(socket: Socket)= withContext(Dispatchers.IO) {
        val input = socket.openReadChannel()
        try {
            while (isActive) {
                val line = input.readUTF8Line()
                line?.let {
//                    println(it)
                    handleMessage(it)
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            socket.close()
            view.isConnected(false)
        }
    }

    private suspend fun connectToServerAsync() =  withContext(Dispatchers.IO){
        socket =
            aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
                .connect(InetSocketAddress("tec.skotos.net", 6730))
        socket?.let {
            val output = it.openWriteChannel(autoFlush = true)
            output.writeStringUtf8("SKOTOS Orchil 0.2.3\r\n")
            launch { setupGameReader(it) }
            launch { setupGameWriter(output) }
            true
        }?:false
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
                clientLogin(gameCredentials)
            }
        }
    }

    private fun clientLogin(credentials: Login.Credentials) {
        GlobalScope.async {
//            val secret = line.substring(7).trim()
            val hashString = credentials.username + credentials.password + "NONE"
            val zealousHash = MessageDigest.getInstance("MD5").digest(hashString.toByteArray()).toHexString()
            saveToHistory = false
            writeToTec.send("USER ${credentials.username}")
            writeToTec.send("SECRET NONE")
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
        val loginCredentials = view.getLoginCredentials()
        gameCredentials = runBlocking {
             login.getGameServerCredentialsFromLogin(loginCredentials)
        }
        launch {
            if (gameCredentials.username.isEmpty() or gameCredentials.password.isEmpty()) {
                view.failedLogin()
            } else {
                gameConnect()
            }
        }
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

