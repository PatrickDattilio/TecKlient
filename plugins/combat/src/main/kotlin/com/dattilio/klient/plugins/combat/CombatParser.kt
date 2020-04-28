package com.dattilio.klient.plugins.combat

import com.tinder.StateMachine
import java.util.regex.Pattern

class CombatParser(
    private val updateEngaged: (engaged:String, add:Boolean) ->Unit,
    private val combatSettings: CombatSettings,
    private val alertManager: AlertManager,
    private val stateMachine: StateMachine<CombatStateMachine.State, CombatStateMachine.Event, CombatStateMachine.SideEffect>
) {

    private val enemyAttackPattern: Pattern = Pattern.compile("] [A |An ] (.*?)(leaps up at|\\S+) you")
    private val killPattern = Pattern.compile("You slit a (.*)\'s")


    fun processLine(line: String) {
        if ("to you" in line) {
            alertManager.alertWithSound()
        } else if ("You are no longer busy." in line) {
            stateMachine.transition(CombatStateMachine.Event.NoLongerBusy)
        } else if ("expires." in line) {
            stateMachine.transition(CombatStateMachine.Event.EnemyKilled)
        } else if ("falls unconscious" in line) {
            stateMachine.transition(CombatStateMachine.Event.EnemyUnconscious)
        } else if (("You must be wielding a weapon to attack." in line).or("You are already carrying" in line)) {
            stateMachine.transition(CombatStateMachine.Event.SuccessfulGetWeapon)
        } else if (("You fumble" in line)
                .or("You can't do that right now." in line)
                .or("You must be carrying something to wield it." in line)
        ) {
            stateMachine.transition(CombatStateMachine.Event.WeaponDropped)
        } else if (("You wield a " + combatSettings.weapon() in line)
                .or("That weapon is too small to wield in two hands." in line)
                .or("You are already wielding that" in line)
        ) {
            stateMachine.transition(CombatStateMachine.Event.SuccessfulWield)
        } else if ("You take a " + combatSettings.weapon() in line) {
            stateMachine.transition(CombatStateMachine.Event.SuccessfulGetWeapon)
        } else if ("is not close enough." in line) {
            stateMachine.transition(CombatStateMachine.Event.TooFar)
        } else if ("You'll have to retreat first." in line) {
            stateMachine.transition(CombatStateMachine.Event.Retreat)
        } else if ("You retreat." in line) {
            stateMachine.transition(CombatStateMachine.Event.SuccessfulRetreat)
        } else if ("clamped onto you" in line) {
            stateMachine.transition(CombatStateMachine.Event.Bound)
        } else if ("You manage to break free!" in line) {
            stateMachine.transition(CombatStateMachine.Event.Unbound)
        } else if ("must be unconscious first" in line) {
            stateMachine.transition(CombatStateMachine.Event.EnemyKilled)
        }
        // Something is attacking us
        else if (("[" in line).and("Success" in line)) {
            if (("] A" in line).or("] An" in line)) {
                stateMachine.transition(CombatStateMachine.Event.EnemyHitYou)
                parseOpponent(line, true)
            } else if ("You slit" in line) {
                stateMachine.transition(CombatStateMachine.Event.EnemyKilled)
                parseOpponent(line, false)
            }
        }
    }

    private fun parseOpponent(line: String, add: Boolean) {
        val opponent = if (add) {
            enemyAttackPattern.matcher(line)
        } else {
            killPattern.matcher(line)
        }
        if (opponent.find()) {
            updateEngaged.invoke(opponent.group(1), add)
        }
    }
}