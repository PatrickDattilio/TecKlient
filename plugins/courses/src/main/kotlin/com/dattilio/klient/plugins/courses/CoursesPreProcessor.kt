package com.dattilio.klient.plugins.courses

import com.tinder.StateMachine
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.*
import java.net.InetSocketAddress
import kotlin.random.Random.Default.nextLong

class CoursesPreProcessor constructor() {

    var timeouts: MutableList<Deferred<StateMachine.Transition<CoursesStateMachine.State, CoursesStateMachine.Event, CoursesStateMachine.SideEffect>>?> =
        MutableList(10) {null}

    //    val combatSettings = CoursesSettings("settings.json")
    var enabled = false
    private val view = CoursesView(this)
    private val state = CoursesStateMachine(::handleSideEffect, view::isThreePartCourse)

    private val parser = CoursesParser(AlertManager(), state.stateMachine)

    //    private val engaged = ArrayList<String>()
    lateinit var send: ByteWriteChannel
    var attackIndex = 0
//    private fun nextAttack(): String {
//        attackIndex = (attackIndex + 1) % combatSettings.rotation()!!.size
//        return combatSettings.rotation()!![attackIndex]
//    }

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
            CoursesStateMachine.SideEffect.South ->  sendCommand("s", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Rope4 -> sendCommand("jump rope", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Path4 -> sendCommand("go path", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Track4 -> sendCommand("go track", sideEffect.timeoutEvent, true)
            CoursesStateMachine.SideEffect.Coal4 ->  sendCommand("go coal", sideEffect.timeoutEvent, true)
        }
    }

    private fun handleCancelTimeout(cancelTimeout: CoursesStateMachine.SideEffect.CancelTimeout) {
        timeouts[cancelTimeout.timeout.ordinal]?.cancel()
    }

    private fun handleFailed(failure: CoursesStateMachine.SideEffect.Failed) {
        view.updateFail(failure, failure.count)
        state.stateMachine.transition(CoursesStateMachine.Event.Failed)
    }

    private fun handleSuccess(success: CoursesStateMachine.SideEffect.Success) {
        view.updateSuccess(success.successCount, success.message)
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


    fun preProcessLine(line: String): String {
        if (enabled) {
            parser.processLine(line)
        }
        return line
    }

    init {
        GlobalScope.async {

            val socket =
                aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))

            println("Accepted connection: ${socket.remoteAddress}")
            send = socket.openWriteChannel(autoFlush = true)
            GlobalScope.async {
                val input = socket.openReadChannel()
                try {
                    while (true) {
                        val line = input.readUTF8Line()
                        line?.let { println(it) }
                        line?.let { preProcessLine(it) }
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.close()
                }
            }
        }
    }


    private fun sendCommand(
        command: String,
        timeoutEvent: CoursesStateMachine.Event.TimeoutEvent?,
        randomDelay: Boolean = true
    ) {

        GlobalScope.async {
            if (randomDelay) {
                delay(nextLong(450, 750))
            }
            send.write("$command\r\n")
        }
        timeoutEvent?.let {
            timeouts.add(it.timeout.ordinal, GlobalScope.async {
                delay(3000)
                state.stateMachine.transition(it)
            })
        }
    }

    fun checkBoxClicked(newValue: Boolean) {
        enabled = newValue
        if (enabled) {
            state.stateMachine.transition(CoursesStateMachine.Event.NoLongerBusy)
        }
    }


}