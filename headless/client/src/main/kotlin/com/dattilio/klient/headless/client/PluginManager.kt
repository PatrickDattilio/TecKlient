package com.dattilio.klient.headless.client

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import java.net.InetSocketAddress
import kotlin.system.exitProcess

class PluginManager constructor(
    private val localPort: Int
) {


    @ExperimentalCoroutinesApi
    val broadcast = BroadcastChannel<String>(10)

    @ExperimentalCoroutinesApi
    val sendToGame = BroadcastChannel<String>(10)
    var gameIncoming: Socket? = null

    private suspend fun broadcastToApps(line: String) {
        broadcast.send("$line\n")
    }

    suspend fun setup(debug: Boolean) {
        withContext(Dispatchers.IO) {
            val server =
                aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", localPort))
            println("Started echo telnet server at ${server.localAddress}")

            while (true) {
                val socket = server.accept()
                if (gameIncoming == null) {
                    gameIncoming = socket
                    val tec = socket.openWriteChannel(true)
                    val input = socket.openReadChannel()
                    launch {
                        //listen to game
                        try {
                            while (true) {
                                val line = input.readUTF8Line()
                                if (!line.isNullOrEmpty()) {
                                    if(debug){
                                        println("Forwarding: $line")
                                    }
                                    broadcastToApps(line)
                                }
                            }
                        } catch (e: Throwable) {
                            socket.close()
                        }
                    }
                    launch {
                        sendToGame.consumeEach {
                            tec.writeStringUtf8("$it\n")
                        }
                    }

                    println("Accepted Game: ${socket.remoteAddress}")
                } else {


                    println("Accepted connection: ${socket.remoteAddress}")
                    launch {
                        val output = socket.openWriteChannel(true)
                        broadcast.consumeEach {
                            output.writeStringUtf8("$it\n")
                        }
                    }

                    launch {
                        val input = socket.openReadChannel()
                        try {
                            while (true) {
                                val line = input.readUTF8Line()
                                if (!line.isNullOrEmpty()) {
                                    line.let { sendToGame?.send(it) }
                                }
                            }
                        } catch (e: Throwable) {
                            socket.close()
                        }
                    }
                }
            }
        }
        exitProcess(0)
    }

}
