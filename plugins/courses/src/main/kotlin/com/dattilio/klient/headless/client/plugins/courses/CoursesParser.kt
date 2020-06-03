package com.dattilio.klient.headless.client.plugins.courses

import com.dattilio.klient.plugins.courses.CoursesStateMachine.Event
import com.dattilio.klient.plugins.courses.CoursesStateMachine.Event.*
import com.tinder.StateMachine

class CoursesParser(
    private val alertManager: AlertManager,
    private val stateMachine: StateMachine<CoursesStateMachine.State, Event, CoursesStateMachine.SideEffect>
) {
    fun processLine(line: String) {
        when {

            "afar" in line -> {
                alertManager.alertWithSound()
                alertManager.alertWithSound()
                alertManager.alertWithSound()
            }
//            "You arrive at a field with short grass" in line->{
//                stateMachine.transition(Event.NoLongerBusy)
//            }
            "back to the start." in line -> stateMachine.transition(Failed)

            "You are no longer busy." in line -> stateMachine.transition(NoLongerBusy)

            "You start to climb up the rope" in line -> stateMachine.transition(StartedClimbing)

            "You must be standing" in line -> stateMachine.transition(Failed)

            "You stand up" in line ->
                stateMachine.transition(StandSuccess)

            "You arrive at a climbing wall" in line -> stateMachine.transition(ArriveRope)

            "You arrive at a pool" in line -> stateMachine.transition(ArrivePlank)

            "You arrive at a dropping pole" in line -> stateMachine.transition(ArrivePath)

            "You pick a plank and begin to walk" in line -> stateMachine.transition(StartedPlank)

            "You run down the path" in line -> stateMachine.transition(StartedPath)


            "You arrive at a mud pit" in line -> stateMachine.transition(ArriveRope4)
            "You leap at the swinging rope" in line -> stateMachine.transition(StartedRope4)

            "You arrive at a path through swinging weights" in line -> stateMachine.transition(ArrivePath4)
            "You race down the sandy path" in line -> stateMachine.transition(StartedPath4)

            "You arrive at a circular track" in line -> stateMachine.transition(ArriveTrack4)
            "You begin to jog around the circular path" in line -> stateMachine.transition(StartedTrack4)

            "You arrive at a bed of hot coals" in line -> stateMachine.transition(ArriveCoal4)
            "You clear your mind and proceed to walk over the coals" in line -> stateMachine.transition(StartedCoal4)


            "You feel as if you have improved your" in line -> stateMachine.transition(Success(line))

        }

    }

}