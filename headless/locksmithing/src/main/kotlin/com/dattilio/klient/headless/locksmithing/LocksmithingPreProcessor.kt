package com.dattilio.klient.headless.locksmithing

import com.dattilio.klient.headless.locksmithing.LocksmithingStateMachine.Action
import com.dattilio.klient.headless.locksmithing.LocksmithingStateMachine.Event
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random.Default.nextLong
import kotlin.system.exitProcess

class LocksmithingPreProcessor constructor(
    override val coroutineContext: CoroutineContext,
    val settings: LocksmithSettings
) : CoroutineScope {

    var timeouts: MutableMap<String, Job?> = mutableMapOf()

    var enabled = true
    private val state =
        LocksmithingStateMachine(::handleSideEffect, settings)
    private val parser =
        LocksmithingParser(state.stateMachine)

    lateinit var send: ByteWriteChannel

    @ExperimentalCoroutinesApi
    val sendToGame = BroadcastChannel<String>(10)

    private fun handleSideEffect(sideEffect: Action) {
        println(sideEffect)
        when (sideEffect) {
            is Action.Retry -> sendCommand(sideEffect.command, Event.Timeout(Action.Alert))
            is Action.PerformAction -> sendCommand(sideEffect.command, Event.Timeout(sideEffect))
            Action.Alert -> exitProcess(0)
            Action.Success -> cancelTimeouts()
        }
    }

    private fun cancelTimeouts(){
        timeouts.values.forEach { it?.cancel() }
    }

    private fun preProcessLine(line: String): String {
        if (enabled) {
            parser.processLine(line)
        }
        return line
    }

    suspend fun setup(debug: Boolean) =
        withContext(Dispatchers.IO) {
            val socket =
                aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", settings.port))

            println("Accepted connection: ${socket.remoteAddress}")
            send = socket.openWriteChannel(autoFlush = true)

            launch {
                val input = socket.openReadChannel()
                try {
                    while (true) {
                        val line = input.readUTF8Line()
                        line?.let {
                            if (debug) {
                                println(line)
                            }
                            preProcessLine(it)
                        }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.close()
                }
            }
        }

    private fun sendCommand(
        command: String,
        timeout: Event.Timeout?,
        randomDelay: Boolean = true
    ) {
        cancelTimeouts()
        launch {
            if (randomDelay) {
                delay(nextLong(450, 750))
            }
            send.writeStringUtf8("$command\n")
        }
        timeout?.let {
            timeouts.put(command, launch {
                delay(3000)
                state.stateMachine.transition(timeout)
            })
        }

    }
}