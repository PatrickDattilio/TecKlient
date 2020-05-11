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
    var timeouts: MutableMap<String, Deferred<StateMachine.Transition<CombatStateMachine.State, CombatStateMachine.Event, CombatStateMachine.SideEffect>>> =
        mutableMapOf()
    val combatSettings = CombatSettings("settings.json")
    var enabled = false
    private val view = CombatView(this)
    private val state = CombatStateMachine(::handleSideEffect)

    private val parser = CombatParser(this::updateEngaged, combatSettings, AlertManager(), state.stateMachine)
    private val engaged = ArrayList<String>()
    lateinit var send: ByteWriteChannel
    var attackIndex = 0
    private fun previousAttack(): String {
        return combatSettings.rotation()!![attackIndex]
    }

    private fun nextAttack(): String {
        attackIndex = (attackIndex + 1) % combatSettings.rotation()!!.size
        return combatSettings.rotation()!![attackIndex]
    }

    private fun handleSideEffect(sideEffect: CombatStateMachine.SideEffect) {
        println(sideEffect)
        when (sideEffect) {
            CombatStateMachine.SideEffect.Attack -> handleAttack(sideEffect, nextAttack())
            CombatStateMachine.SideEffect.GetWeapon -> handleGetWeapon(sideEffect)
            CombatStateMachine.SideEffect.Wield -> handleWield(sideEffect)
            CombatStateMachine.SideEffect.Release -> handleRelease(sideEffect)
            CombatStateMachine.SideEffect.Kill -> handleKill(sideEffect)
            CombatStateMachine.SideEffect.Status -> handleStatus(sideEffect)
            CombatStateMachine.SideEffect.Retreat -> handleRetreat(sideEffect)
            CombatStateMachine.SideEffect.Lunge -> handleLunge(sideEffect)
            CombatStateMachine.SideEffect.RepeatAttack -> handleAttack(sideEffect, previousAttack())
            is CombatStateMachine.SideEffect.Completed -> handleCompleted(sideEffect)
            is CombatStateMachine.SideEffect.Timeout -> handleTimeout(sideEffect)
            CombatStateMachine.SideEffect.Approach ->  sendCommand("at", sideEffect.event, true)
            CombatStateMachine.SideEffect.Stand ->  sendCommand("stand", sideEffect.event, true)
        }
    }

    private fun handleCompleted(completed: CombatStateMachine.SideEffect.Completed) {
        completed.cancelTimeout.let { timeouts[it::class.java.simpleName]?.cancel()}
        completed.sideEffect?.let{handleSideEffect(it)}
    }

    private fun handleTimeout(timeout: CombatStateMachine.SideEffect.Timeout) {
        timeouts.remove(timeout.sideEffect::class.java.simpleName)?.cancel()
        handleSideEffect(timeout.sideEffect)
    }

    private fun handleLunge(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("zl", sideEffect.event, true)
    }

    private fun handleRetreat(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("ret", sideEffect.event, true)
    }

    private fun handleStatus(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("ss", sideEffect.event)
    }

    private fun handleKill(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("kl", sideEffect.event)
    }

    private fun handleRelease(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("release", sideEffect.event)
    }

    private fun handleWield(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("wie  ${combatSettings.weapon()}", sideEffect.event, true)
    }

    private fun handleGetWeapon(sideEffect: CombatStateMachine.SideEffect) {
        sendCommand("get ${combatSettings.weapon()}", sideEffect.event, true)
    }

    private fun handleAttack(sideEffect: CombatStateMachine.SideEffect, attack: String) {
        sendCommand(attack, sideEffect.event)
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
//        view.engagedText.text = stringBuilder.toString()
    }


    private fun sendCommand(command: String, failureEvent: CombatStateMachine.Event?, randomDelay: Boolean = true) {
        GlobalScope.async {
            if (randomDelay) {
                delay(nextLong(450, 750))
            }
            send.write("$command\r\n")
        }
        failureEvent?.let {
            println("${System.currentTimeMillis()} Timer starte for: $it")
            timeouts.put(it::class.java.simpleName, GlobalScope.async {
                delay(3000)
                state.stateMachine.transition(it)
            })
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
        state.stateMachine.transition(CombatStateMachine.Event.UnderAttack)
    }

    fun killingBlowClicked(newValue: Boolean) {
        state.killingBlow = newValue
    }

    fun saveGapCloser(gapCloser: String) {
        this.combatSettings.updateGapCloser(gapCloser)
    }

    fun gapCloserClicked(newValue: Boolean) {
        state.closeGap = newValue
    }


}