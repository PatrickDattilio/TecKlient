package com.dattilio.klient.headless.locksmithing

import com.dattilio.klient.headless.locksmithing.LocksmithingStateMachine.Event.*
import com.tinder.StateMachine

class LocksmithingParser(
    private val stateMachine: StateMachine<LocksmithingStateMachine.State, LocksmithingStateMachine.Event, LocksmithingStateMachine.Action>
) {
    fun processLine(line: String) {
        when {
            "You are no longer busy." in line -> stateMachine.transition(NoLongerBusy)

            ("there is very little you are able to remember on the subject" in line)
                .or("but is not completely unjammed" in line) -> stateMachine.transition(Failed)

            ("You hear a click as a tumbler mechanism releases." in line)
                .or("You hear a click as a tumbler mechanism closes." in line)
                .or(" You feel an obstruction release, and you have confidence the lock will operate normally now." in line)
                .or("You carefully study a tumbler mechanism and feel that you have a pretty firm grasp of how its locking mechanism operates" in line)
                .or("The lock mechanism consists of a basic collection of interlocked metal teeth that slide apart when opened with the appropriate key." in line)
                .or("It is already unlocked." in line)
                .or("It is already locked." in line)
                .or("This lock is jammed." in line)
                .or("There is nothing jammed open." in line)
                .or("There is nothing to jam closed." in line)
            -> stateMachine.transition(Success)
        }

    }

}