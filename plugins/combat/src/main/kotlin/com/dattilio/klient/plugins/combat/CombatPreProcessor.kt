package com.dattilio.klient.plugins.combat

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
import java.util.*
import kotlin.random.Random.Default.nextLong

class CombatPreProcessor constructor() {

    var repeater: Deferred<StateMachine.Transition<CombatStateMachine.State, CombatStateMachine.Event, CombatStateMachine.SideEffect>> ?=null
    val combatSettings= CombatSettings("settings.json")
    var enabled = false
    private val view = CombatView(this)
    private val state = CombatStateMachine(::handleSideEffect)

    private val parser = CombatParser(this::updateEngaged,combatSettings, AlertManager(), state.stateMachine)
    private val engaged = ArrayList<String>()
    lateinit var send: ByteWriteChannel
    var attackIndex = 0
    private fun nextAttack(): String {
        attackIndex = (attackIndex + 1) % combatSettings.rotation()!!.size
        return combatSettings.rotation()!![attackIndex]
    }

    private fun handleSideEffect(sideEffect: CombatStateMachine.SideEffect) {
        println(sideEffect)
        when (sideEffect) {
            is CombatStateMachine.SideEffect.Attack -> handleAttack(sideEffect)
            is CombatStateMachine.SideEffect.GetWeapon -> handleGetWeapon(sideEffect)
            is CombatStateMachine.SideEffect.Wield -> handleWield(sideEffect)
            is CombatStateMachine.SideEffect.Release -> handleRelease(sideEffect)
            is CombatStateMachine.SideEffect.Kill -> handleKill(sideEffect)
            is CombatStateMachine.SideEffect.Status -> handleStatus(sideEffect)
            is CombatStateMachine.SideEffect.Retreat -> handleRetreat(sideEffect)
            is CombatStateMachine.SideEffect.Lunge -> handleLunge(sideEffect)
        }
    }

    private fun handleLunge(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("zl", sideEffect.failureEvent, true)
    }

    private fun handleRetreat(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("ret", sideEffect.failureEvent, true)
    }

    private fun handleStatus(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("ss", sideEffect.failureEvent)
    }

    private fun handleKill(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("kl", sideEffect.failureEvent)
    }

    private fun handleRelease(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("release", sideEffect.failureEvent)
    }

    private fun handleWield(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("wie  ${combatSettings.weapon()}", sideEffect.failureEvent, true)
    }

    private fun handleGetWeapon(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("get ${combatSettings.weapon()}", sideEffect.failureEvent, true)
    }

    private fun handleAttack(sideEffect: CombatStateMachine.SideEffect.Attack) {
        sendCommand(nextAttack(), sideEffect.failureEvent)
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


    private fun sendCommand(command: String, failureEvent:CombatStateMachine.Event?, randomDelay: Boolean = true) {
        repeater?.cancel()
        GlobalScope.async {
            if (randomDelay) {
                delay(nextLong(450, 750))
            }
            send.write("$command\r\n")
        }
        failureEvent?.let {
            repeater = GlobalScope.async {
                delay(3000)
                state.stateMachine.transition(it)
            }
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
        attackIndex = 0
    }

    fun moveToAttack() {
        state.stateMachine.transition(CombatStateMachine.Event.EnemyHitYou)
    }

    fun killingBlowClicked(newValue: Boolean) {
        state.killingBlow = newValue
    }


}