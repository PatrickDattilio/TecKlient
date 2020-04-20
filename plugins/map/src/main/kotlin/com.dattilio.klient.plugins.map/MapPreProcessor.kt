package com.dattilio.klient.plugins.map

import com.dattilio.klient.plugins.map.MapStateMachine.SideEffect
import guru.nidi.graphviz.model.Factory
import guru.nidi.graphviz.model.MutableGraph
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.InetSocketAddress
import java.util.*

class MapPreProcessor {
    var enabled = false
    private lateinit var send: ByteWriteChannel
    private val state = MapStateMachine(::handleSideEffect)
    private val parser = MapParser(this, AlertManager(), state.stateMachine)
    val tecMap: MutableGraph = Factory.mutGraph().setDirected(true)
    val view = MapView(::onEnableClicked) {state.save()}

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

    fun preProcessLine(line: String): String {
        if (enabled) {
            parser.processLine(line)
        }
        return line
    }

    private fun handleSideEffect(position: Node, sideEffect: SideEffect) {

        when (sideEffect) {
            is SideEffect.NewRoom -> createNewRoom(sideEffect)
            is SideEffect.UpdatePosition -> updatePosition()
        }
    }

    private fun updatePosition() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun createNewRoom(sideEffect: SideEffect.NewRoom) {
        state.createRoom(sideEffect.position,sideEffect.direction,sideEffect.line)
    }
    private fun onEnableClicked(newValue: Boolean) {
        enabled = newValue
//        if (enabled) {
//            state.stateMachine.transition(MapStateMachine.State.Start)
//        }
    }

}
