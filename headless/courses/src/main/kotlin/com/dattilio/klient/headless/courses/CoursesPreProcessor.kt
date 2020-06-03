package com.dattilio.klient.headless.courses

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import java.net.InetSocketAddress
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random.Default.nextLong

class CoursesPreProcessor constructor(
    isThreePart: Boolean,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {

    var timeouts: MutableList<Job?> =
        MutableList(10) { null }

    var enabled = true
    private val state =
        CoursesStateMachine(::handleSideEffect, isThreePart)
    private val parser =
        CoursesParser(state.stateMachine)

    lateinit var send: ByteWriteChannel
    @ExperimentalCoroutinesApi
    val sendToGame = BroadcastChannel<String>(10)

    private fun handleSideEffect(sideEffect: CoursesStateMachine.SideEffect) {
        println(sideEffect)
        when (sideEffect) {
            CoursesStateMachine.SideEffect.Stand -> handleStand(sideEffect)
            CoursesStateMachine.SideEffect.East -> handleEast(sideEffect)
            CoursesStateMachine.SideEffect.ClimbRope -> handleRope(sideEffect)
            CoursesStateMachine.SideEffect.GoPlank -> handlePlank(sideEffect)
            CoursesStateMachine.SideEffect.GoPath -> handlePath(sideEffect)
            is CoursesStateMachine.SideEffect.Failed -> handleFailed(sideEffect)
            is CoursesStateMachine.SideEffect.Success -> handleSuccess(sideEffect)
            is CoursesStateMachine.SideEffect.CancelTimeout -> handleCancelTimeout(sideEffect)
            CoursesStateMachine.SideEffect.South -> sendCommand("s", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Rope4 -> sendCommand("jump rope", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Path4 -> sendCommand("go path", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Track4 -> sendCommand("go track", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Coal4 -> sendCommand("go coal", sideEffect.timeoutEvent, true)
        }
    }

    private fun handleCancelTimeout(cancelTimeout: CoursesStateMachine.SideEffect.CancelTimeout) {
        timeouts[cancelTimeout.timeout.ordinal]?.cancel()
    }

    private fun handleFailed(failure: CoursesStateMachine.SideEffect.Failed) {
        state.stateMachine.transition(CoursesStateMachine.Event.Failed)
    }

    private fun handleSuccess(success: CoursesStateMachine.SideEffect.Success) {
        state.stateMachine.transition(CoursesStateMachine.Event.NoLongerBusy)
    }

    private fun handlePath(sideEffect: CoursesStateMachine.SideEffect) {
        sendCommand("go path", sideEffect.timeoutEvent, true)
    }

    private fun handlePlank(sideEffect: CoursesStateMachine.SideEffect) {
        sendCommand("go plank", sideEffect.timeoutEvent, true)
    }

    private fun handleRope(sideEffect: CoursesStateMachine.SideEffect) {
        sendCommand("climb rope", sideEffect.timeoutEvent, true)
    }

    private fun handleEast(sideEffect: CoursesStateMachine.SideEffect) {
        sendCommand("e", sideEffect.timeoutEvent, true)
    }

    private fun handleStand(sideEffect: CoursesStateMachine.SideEffect) {

        sendCommand("stand", sideEffect.timeoutEvent, true)
    }


    private fun preProcessLine(line: String): String {
        if (enabled) {
            parser.processLine(line)
        }
        return line
    }

    suspend fun setup(debug: Boolean) =
        withContext(Dispatchers.IO){
        val socket =
            aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))

        println("Accepted connection: ${socket.remoteAddress}")
        send = socket.openWriteChannel(autoFlush = true)

        launch {
            val input = socket.openReadChannel()
            try {
                while (true) {
                    val line = input.readUTF8Line()
                    line?.let {
                        if(debug) { println(line)}
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
        timeoutEvent: CoursesStateMachine.Event.TimeoutEvent?,
        randomDelay: Boolean = true
    ) {

        launch {
            if (randomDelay) {
                delay(nextLong(450, 750))
            }
            send.writeStringUtf8("$command\n")
        }
        timeoutEvent?.let {
            timeouts.add(it.timeout.ordinal, launch {
                delay(3000)
                state.stateMachine.transition(it)
            })
        }
    }
}