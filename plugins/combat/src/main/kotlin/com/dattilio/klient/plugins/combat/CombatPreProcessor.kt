package com.dattilio.klient.plugins.combat

import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.InetSocketAddress
import java.util.*
import kotlin.random.Random.Default.nextLong

class CombatPreProcessor constructor() {

    val combatSettings= CombatSettings("settings.json")
    var enabled = false
    private val view = CombatView(this)
    private val state = CombatStateMachine(::handleSideEffect)

    private val parser = CombatParser(this::updateEngaged,combatSettings, AlertManager(), state.stateMachine)
    var weapon:String=""
    private val engaged = ArrayList<String>()
    lateinit var send: ByteWriteChannel
    var attackIndex = 0
    private var attacks = emptyList<String>()
    private fun nextAttack(): String {
        attackIndex = (attackIndex + 1) % attacks.size
        return attacks[attackIndex]
    }

    private fun handleSideEffect(sideEffect: CombatStateMachine.SideEffect) {
        println(sideEffect)
//        launch {
//            updateActionQueueText()
//        }
        when (sideEffect) {
            CombatStateMachine.SideEffect.Attack -> sendCommand(nextAttack())
            CombatStateMachine.SideEffect.GetWeapon -> sendCommand("get $weapon", true)
            CombatStateMachine.SideEffect.Wield -> sendCommand("wie $weapon", true)
            CombatStateMachine.SideEffect.Release -> sendCommand("release")
            CombatStateMachine.SideEffect.Kill -> sendCommand("kl")
            CombatStateMachine.SideEffect.Look -> sendCommand("l")
            CombatStateMachine.SideEffect.Retreat -> sendCommand("ret", true)
            CombatStateMachine.SideEffect.Lunge -> sendCommand("zl", true)
        }
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


    private fun updateEngagedText() {
        val stringBuilder = StringBuilder()
        for (item in engaged) {
            stringBuilder.append(item).append("\n")
        }
        view.engagedText.text = stringBuilder.toString()
    }


    private fun sendCommand(command: String, randomDelay: Boolean = true) {
        GlobalScope.async {
            if (randomDelay) {
                kotlinx.coroutines.delay(nextLong(450, 750))
            }
            send.write("$command\r\n")
        }
    }

    fun updateEngaged(opponent: String, add: Boolean) {
        if (opponent !in engaged) {
            if (add) {
                engaged.add(opponent)
            } else {
                engaged.remove(opponent)
            }
            updateEngagedText()
        }
    }

    fun checkBoxClicked(newValue: Boolean) {
        enabled = newValue
        if (enabled) {
            state.stateMachine.transition(CombatStateMachine.Event.Idle)
        }
    }

    fun saveWeapon(weapon: String) {
        this.combatSettings.updateWeapon(weapon)
    }

    fun saveRotation(rotation: List<String>) {
        this.combatSettings.updateRotation(rotation)
    }


}