package com.dattilio.scoundrel

import com.dattilio.klient.api.LinePreprocessor
import com.dattilio.klient.api.SendCommand
import org.slf4j.LoggerFactory
import ro.fortsoft.pf4j.Extension
import java.util.*
import java.util.regex.Pattern

@Extension
open class CombatPreProcessor : LinePreprocessor {
    lateinit var send: SendCommand

    lateinit var combatSettings: CombatSettings
    val logger = LoggerFactory.getLogger(Combat::class.java)
    var enabled = false
    var free = true
    var inCombat = false
    val view = CombatView(this)
    val queue = CombatQueue(this)
    val parser = CombatParser(this)
    var action = Action.NOTHING
    val random = Random()

    val engaged = ArrayList<String>()
    override fun setSendCommand(sendCommand: SendCommand) {
        send = sendCommand
    }

    override fun preProcessLine(line: String): String {
        if (enabled) {
            parser.processLine(line)
        }
        return line
    }

    init {
        retrieveCombatSettings()

    }

    private fun retrieveCombatSettings() {
        combatSettings = CombatSettings("plugins/settings.json")
    }


    fun updateActionQueueText() {
        val stringBuilder = StringBuilder()
        for (item in queue) {
            stringBuilder.append(item).append("\n")
        }
        view.queueText.text = stringBuilder.toString()
    }


    private fun updateEngagedText() {
        val stringBuilder = StringBuilder()
        for (item in engaged) {
            stringBuilder.append(item).append("\n")
        }
        view.engagedText.text = stringBuilder.toString()
    }

    private fun act() {
        if (free.and(queue.size > 0)) {
            action = queue.poll()
            when (action) {
                Action.RECOVER -> {
                    recover(true)
                    free = false
                }

                Action.WIELD -> {
                    wield(true)
                    free = false
                }

                Action.RETREAT -> {
                    //TODO retreat()
                }
                Action.KILL -> {
                    free = false
                    queue.addAction(Action.KILL)
                    sendCommand("kl")
                }
                Action.RELEASE -> {
                    sendCommand("release")
                }
                Action.ATTACK -> {
                    val index = random.nextInt(combatSettings.rotation!!.size - 1)
                    sendCommand(combatSettings.rotation[index])
                    queue.addAction(Action.ATTACK)
                }
                else -> {
                    act()
                }

            }
        }
    }

    fun wield(addAction: Boolean) {
        if (addAction) {
            queue.addAction(Action.WIELD)
            sendCommand("wie " + combatSettings.weapon)
        } else {
            queue.removeAction(Action.WIELD)
            act()
        }
    }

    fun recover(addAction: Boolean) {
        if (addAction) {
            queue.addAction(Action.RECOVER)
            sendCommand("get " + combatSettings.weapon)
        } else {
            queue.removeAction(Action.RECOVER)
            queue.addAction(Action.WIELD)
            act()
        }
    }


    fun sendCommand(command: String) {
        send.send(command)
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
    }

    fun notBusy() {
        free = true
        act()

    }

    fun killed(status: CombatParser.KillStatus) {
        when (status) {
            CombatParser.KillStatus.ALIVE -> {
                queue.removeAction(Action.KILL)
                free = true
            }
            CombatParser.KillStatus.YOU -> {
                queue.removeAction(Action.KILL)
                inCombat = false
            }
            CombatParser.KillStatus.OTHER -> {
                queue.removeAction(Action.KILL)
                inCombat = false
            }
        }
    }

    fun bound(playerIsBound: Boolean) {
        if (playerIsBound) {
            queue.addAction(Action.RELEASE)
        } else {
            queue.removeAction(Action.RELEASE)
        }
    }

    fun underAttack() {
        queue.addAction(Action.ATTACK)
        if (free) {
//                    self.combat_print("Free, attacking")
            act()
        }
    }

    fun opponentUnconcious() {
        queue.removeAction(Action.ATTACK)
        queue.addAction(Action.KILL)
        if (free) {
            act()
        }
    }


}