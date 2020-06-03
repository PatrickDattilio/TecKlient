package com.dattilio.klient.plugins.combat

import com.dattilio.klient.plugins.combat.CombatStateMachine.*
import com.tinder.StateMachine

class CombatParser(
    private val combatSettings: CombatSettings,
    private val stateMachine: StateMachine<State, Event, SideEffect>
) {
    private val rollRegex = "Success: (\\d+), Roll: (\\d+)]".toPattern().toRegex()
    private val statsParser = StatsParser()
    private val droppedWeapon =
        listOf(
            "disarming you!",
            "You can't do that right now.",
            "You must be carrying something to wield it."
        )

    private val weaponIsWielded = listOf(
        "You wield a ",
        "That weapon is too small to wield in two hands.",
        "You are already wielding that"
    )

    private val noLongerBusy= listOf("You are no longer busy.","bow is already loaded.","bow isn't loaded.")

    fun processLine(line: String) {
        when {
            statsParser.shouldParseStats(line) -> {
            }
            noLongerBusy.any { it in line }-> {
                stateMachine.transition(Event.NoLongerBusy)
            }
            "expires." in line -> {
                stateMachine.transition(Event.EnemyKilled)
            }
            ("falls unconscious" in line)
                .or("You don't see any " in line) -> {
                stateMachine.transition(Event.EnemyUnconscious)
            }
            ("You must be wielding a weapon to attack." in line)
                .or("You are already carrying" in line)
                .or(("You take a " in line).and(combatSettings.weapon() ?: "" in line))
                .or(("withdraw a " in line).and(combatSettings.weapon() ?: "" in line)) -> {
                stateMachine.transition(Event.Completed.GetWeapon)
            }
            droppedWeapon.any { it in line }
            -> {
                stateMachine.transition(Event.WeaponDropped)
            }
            "You stand up" in line -> stateMachine.transition(Event.Completed.Stand)
            ("You must be standing." in line)
                .or("on your butt" in line)
                .or("off your feet" in line) -> stateMachine.transition(Event.Prone)
            ("You fumble" in line)
                .and("drop" in line)
                .and(combatSettings.weapon().toString() in line)
                .and("recover" !in line) -> {
                stateMachine.transition(Event.WeaponDropped)
            }

            weaponIsWielded.any { it in line } -> {
                stateMachine.transition(Event.Completed.Wield)
            }
            ("You stop next to" in line)
                .or("stops next to you" in line) -> {
                stateMachine.transition(Event.Completed.Approach)
            }
            "is not close enough." in line -> {
                stateMachine.transition(Event.TooFar)
            }
            "You'll have to retreat first." in line -> {
                stateMachine.transition(Event.Retreat)
            }
            "You retreat." in line -> {
                stateMachine.transition(Event.Completed.Retreat)
            }
            "clamped onto you" in line -> {
                stateMachine.transition(Event.Bound)
            }
            "You manage to break free!" in line -> {
                stateMachine.transition(Event.Unbound)
            }
            "must be unconscious first" in line -> {
                stateMachine.transition(Event.EnemyKilled)
            }
            " arrives." in line -> {
                stateMachine.transition(Event.NewOpponent)
            }
            // Something is attacking us
            "[Success:" in line -> {
                when {
                    (" at a " in line)
                        .or(" at an " in line) -> {
                        val result = rollRegex.find(line)
                        val success = result!!.groupValues[2].toInt() > result.groupValues[1].toInt()
                        stateMachine.transition(Event.Completed.Attack(success))
                    }
                    (" at you" in line)
                        .or(" tries to " in line)
                        .or(" towards you" in line)
                        .or(" into you" in line)
                        .or(" misses you" in line)
                        .or("You block" in line) -> {
                        stateMachine.transition(Event.UnderAttack)
                    }
                    ("You slit" in line)
                        .or(("You bash" in line).and("killing" in line)) -> {
                        stateMachine.transition(Event.EnemyKilled)
                    }
                }
            }
        }
    }
}

private data class Character(
    val name: String? = "",
    val homeland: String? = ""
)

class StatsParser {
    private var parsingStats = false
    private var horizontalRuleCount = 0
    private var name = ""
    private var homeland = ""

    fun shouldParseStats(line: String): Boolean {
        if ("Character Sheet for " in line) {
            parsingStats = true
        } else if (parsingStats) {
            parsingStats = parse(line)
        }
        return parsingStats
    }

    private fun parse(line: String): Boolean {
        if ("<hr>" in line) {
            horizontalRuleCount++
            if (horizontalRuleCount > 1) {
                parsingStats = false
                horizontalRuleCount = 0
            }
        } else if ("Name: " in line) {

            name = line.substringAfter("Name:  ")
            homeland = name.substringAfter("Homeland: ")
            name = name.substringBefore("Homeland:").trim()
            print(Character(name, homeland))
        }

        return parsingStats
    }
}

