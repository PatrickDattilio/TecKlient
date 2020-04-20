package com.dattilio.klient

import com.dattilio.klient.api.SendCommand
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import java.net.InetSocketAddress
import javax.inject.Inject

class PluginManager @Inject constructor(
    val sendCommand: SendCommand
) {
    @ExperimentalCoroutinesApi
    val broadcast = BroadcastChannel<String>(10)

    init {
        server()
    }

    fun postProcessLine(line: String) {
        GlobalScope.async {
            broadcast.send("$line\r\n")
        }
    }

    private fun server() {
        GlobalScope.async {
            val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
            println("Started echo telnet server at ${server.localAddress}")

            while (true) {
                val socket = server.accept()
                println("Accepted connection: ${socket.remoteAddress}")
                GlobalScope.async {
                    val output = socket.openWriteChannel(true)
                    broadcast.consumeEach {
                        output.writeStringUtf8(it)
                    }
                }

                GlobalScope.async {
                    val input = socket.openReadChannel()
                    try {
                        while (true) {
                            val line = input.readUTF8Line()
                            println("Recieved: $line")
                            line?.let { sendCommand.send(it) }
                        }
                    } catch (e: Throwable) {
                        socket.close()
                    }
                }
            }
        }
    }
}
