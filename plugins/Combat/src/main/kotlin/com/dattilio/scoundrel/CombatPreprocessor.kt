package com.dattilio.scoundrel

import com.dattilio.klient.api.LinePreprocessor
import com.dattilio.klient.api.SendCommand
import com.squareup.moshi.Moshi
import javafx.scene.Scene
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage
import org.slf4j.LoggerFactory
import ro.fortsoft.pf4j.Extension
import java.io.File
import java.util.*
import java.util.regex.Pattern

@Extension
class CombatPreProcessor : LinePreprocessor {
    lateinit var send: SendCommand

    val moshi = Moshi.Builder().build()
    val combatSettings = moshi.adapter(CombatSettings::class.java).fromJson(File("plugins/settings.json").readText())
    val weapon = combatSettings?.weapon
    val rotation = combatSettings?.rotation
    val logger = LoggerFactory.getLogger(Combat::class.java)
    var enabled = false
    var free = true
    val stage = Stage()
    var inCombat = false


    val queue: PriorityQueue<Action> = PriorityQueue()
    var action = Action.NOTHING
    val random = Random()
    val queueText: Text

    var killPattern: Pattern
    var enemyAttackPattern: Pattern

    val engaged = ArrayList<String>()
    override fun setSendCommand(sendCommand: SendCommand) {
        send = sendCommand
    }

    override fun preProcessLine(line: String): String {
        if (enabled) {
            processLine(line)
        }
        return line
    }

    init {
        killPattern = Pattern.compile("You slit (.*)\'s")
        enemyAttackPattern = Pattern.compile("] [A |An] (.*?) \\S+ you")

        stage.title = "Combat"
        logger.info("Combat.start()")
        val checkBox = CheckBox("Auto Combat")
        checkBox.isSelected = false
        checkBox.selectedProperty().addListener({
            _, _, newValue ->
            enabled = newValue
        })
        queueText = Text()
        val combatBox = VBox(checkBox, queueText)
        combatBox.minHeight = 300.0
        combatBox.minWidth = 3000.0
        stage.scene = Scene(combatBox)
        stage.show()
    }

    fun processLine(line: String) {
        if ("You are no longer busy." in line) {
            free = true
            act()
        } else if ("expires." in line) {
            removeAction(Action.KILL)
            inCombat = false
        } else if ("falls unconscious" in line) {
            removeAction(Action.ATTACK)
            addAction(Action.KILL)
            if (free) {
                act()
            }
        } else if ("You fumble!" in line) {
            recoverNow(false)
        } else if (("You must be wielding a weapon to attack." in line).or("You can't do that right now." in line)) {
            recoverNow(true)
        } else if ("clamped onto you" in line) {
            addAction(Action.RELEASE)
        } else if ("You manage to break free!" in line) {
            removeAction(Action.RELEASE)
        } else if ("must be unconscious first" in line) {
            removeAction(Action.KILL)
            free = true
        }
        // Something is attacking us
        else if (("[" in line).and("Success" in line)) {
            if (("] A" in line).or("] An" in line)) {
                addAction(Action.ATTACK)
//                updateEngaged(line)
                if (free) {
//                    self.combat_print("Free, attacking")
                    act()
                }
            } else if ("You slit" in line) {

                addAction(Action.KILL)
                inCombat = false
                val target = killPattern.matcher(line)
                if (target.matches()) {
//                    combat_print(str(datetime.datetime.now())[11: - 7]+" Killed "+target.group(1))
                    engaged.remove(target.group(1))

                }
            }
        }
    }

    private fun addAction(action: Action) {
        if (action !in queue) {
            queue.add(action)
            updateQueueText()
        }
    }

    private fun removeAction(action: Action) {
        if (queue.remove(action)) {
            updateQueueText()
        }
    }

    private fun updateQueueText() {
        val stringBuilder = StringBuilder()
        for (item in queue) {
            stringBuilder.append(item).append("\n")
        }
        queueText.text = stringBuilder.toString()
    }

    private fun act() {
        if (free.and(queue.size > 0)) {
            action = queue.poll()
            when (action) {
                Action.RECOVER -> {
                    recover()
                    free = false
                }

                Action.RETREAT -> {
                    //TODO retreat()
                }
                Action.KILL -> {
                    free = false
                    addAction(Action.KILL)
                    sendCommand("kl")
                }
                Action.RELEASE -> {
                    sendCommand("release")
                }
                Action.ATTACK -> {
                    val index = random.nextInt(rotation!!.size - 1)
                    sendCommand(rotation[index])
                    addAction(Action.ATTACK)
                }
                else -> {
                    act()
                }

            }
        }
    }

    fun recoverNow(recoverNow: Boolean) {
        addAction(Action.RECOVER)
        if (recoverNow) {
            act()
        }
    }

    private fun recover() {
        sendCommand("get " + weapon)
        Thread.sleep(random.longs(1234, 2512).findFirst().asLong / 1000)
        sendCommand("wie " + weapon)
        free = true
        Thread.sleep(random.longs(1593, 2849).findFirst().asLong / 1000)
        act()
    }


    fun sendCommand(command: String) {
        send.send(command)
    }

    fun updateEngaged(line: String) {
        val opponent = enemyAttackPattern.matcher(line)//find(line)
        if (opponent.matches().and(opponent.group(1) !in engaged)) {
            engaged.add(opponent.group(1))
        }
    }


}