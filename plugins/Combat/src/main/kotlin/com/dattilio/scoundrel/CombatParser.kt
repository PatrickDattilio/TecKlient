package com.dattilio.scoundrel

import java.util.regex.Pattern

class CombatParser(val presenter: CombatPreProcessor) {
    enum class KillStatus {YOU, OTHER, ALIVE }

    private var enemyAttackPattern: Pattern = Pattern.compile("] [A |An] (.*?) \\S+ you")

    fun processLine(line: String) {
        if ("You are no longer busy." in line) {
            presenter.notBusy()
        } else if ("expires." in line) {
            presenter.killed(KillStatus.OTHER)
        } else if ("falls unconscious" in line) {
            presenter.opponentUnconcious()
        } else if ("You fumble!" in line) {
            presenter.recoverNow(false)
        } else if (("You must be wielding a weapon to attack." in line).or("You can't do that right now." in line)) {
            presenter.recoverNow(true)
        } else if ("clamped onto you" in line) {
            presenter.bound(true)
        } else if ("You manage to break free!" in line) {
            presenter.bound(false)
        } else if ("must be unconscious first" in line) {
            presenter.killed(KillStatus.ALIVE)
        }
        // Something is attacking us
        else if (("[" in line).and("Success" in line)) {
            if (("] A" in line).or("] An" in line)) {
                presenter.underAttack()
                parseOpponent(line, true)
            } else if ("You slit" in line) {
                presenter.killed(KillStatus.YOU)
                parseOpponent(line, false)
            }
        }
    }

    fun parseOpponent(line: String, add: Boolean) {
        val opponent = enemyAttackPattern.matcher(line)
        if (opponent.find()) {
            presenter.updateEngaged(opponent.group(1), add)

        }
    }
}