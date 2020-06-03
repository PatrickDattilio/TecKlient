package com.dattilio.klient.headless.client.plugins.courses

import com.tinder.StateMachine

class CoursesStateMachine constructor(
    sideEffectListener: (sideEffect: SideEffect) -> Unit,
    private val threePartCourse: () -> Boolean
) {
    var rope4FailureCount = 0
    var path4FailureCount = 0
    var track4FailureCount = 0
    var coal4FailureCount = 0
    var pathFailureCount = 0
    var ropeFailureCount = 0
    var plankFailureCount = 0

    var successCount = 0



    sealed class State {
        object Idle : State()
        object Rope : State()
        object Plank : State()
        object Path : State()
        object Success : State()
        object Start : State()

        object Rope4 : State()
        object Path4 : State()
        object Track4 : State()
        object Coal4 : State()
    }

    sealed class Event {
        object Failed : Event()
        class Success(val message: String) : Event()

        object ArriveRope : Event()
        object ArrivePlank : Event()
        object ArrivePath : Event()
        object NoLongerBusy : Event()
        class TimeoutEvent(val timeout: Timeout) : Event()

        object StandSuccess : Event()
        object StartedClimbing : Event()
        object StartedPlank : Event()
        object StartedPath : Event()


        object ArriveRope4 : Event()
        object ArrivePath4 : Event()
        object ArriveTrack4 : Event()
        object ArriveCoal4 : Event()
        object StartedRope4 : Event()
        object StartedPath4 : Event()
        object StartedTrack4 : Event()
        object StartedCoal4 : Event()
    }

    sealed class SideEffect(val timeoutEvent: Event.TimeoutEvent? = null) {
        object Stand : SideEffect(Event.TimeoutEvent(Timeout.StandTimeout))
        object East : SideEffect(Event.TimeoutEvent(Timeout.EastTimeout))
        object ClimbRope : SideEffect(Event.TimeoutEvent(Timeout.ClimbTimeout))
        object GoPlank : SideEffect(Event.TimeoutEvent(Timeout.PlankTimeout))
        object GoPath : SideEffect(Event.TimeoutEvent(Timeout.PathTimeout))
        class Success(val successCount: Int, val message: String) : SideEffect()


        object South : SideEffect(Event.TimeoutEvent(Timeout.SouthTimeout))
        object Rope4 : SideEffect(Event.TimeoutEvent(Timeout.Rope4Timeout))
        object Path4 : SideEffect(Event.TimeoutEvent(Timeout.Path4Timeout))
        object Track4 : SideEffect(Event.TimeoutEvent(Timeout.Track4Timeout))
        object Coal4 : SideEffect(Event.TimeoutEvent(Timeout.Coal4Timeout))
        sealed class Failed(val count: Int) : SideEffect() {
            class RopeFailed(failcount: Int) : Failed(failcount)
            class PlankFailed(failcount: Int) : Failed(failcount)
            class PathFailed(failcount: Int) : Failed(failcount)
            class Rope4Failed(failcount: Int) : Failed(failcount)
            class Path4Failed(failcount: Int) : Failed(failcount)
            class Track4Failed(failcount: Int) : Failed(failcount)
            class Coal4Failed(failcount: Int) : Failed(failcount)
        }

        class CancelTimeout(val timeout: Timeout) : SideEffect()
    }

    sealed class Timeout(val ordinal: Int) {
        object StandTimeout : Timeout(0)
        object EastTimeout : Timeout(1)
        object ClimbTimeout : Timeout(2)
        object PlankTimeout : Timeout(3)
        object PathTimeout : Timeout(4)

        object SouthTimeout : Timeout(5)
        object Rope4Timeout : Timeout(6)
        object Path4Timeout : Timeout(7)
        object Track4Timeout : Timeout(8)
        object Coal4Timeout : Timeout(9)
    }

    val stateMachine = StateMachine.create<State, Event, SideEffect> {

        initialState(State.Idle)
        state<State.Idle> {
            on<Event.ArriveRope> { transitionTo(State.Rope, SideEffect.ClimbRope) }
            on<Event.ArrivePlank> { transitionTo(State.Plank, SideEffect.GoPlank) }
            on<Event.ArrivePath> { transitionTo(State.Path, SideEffect.GoPath) }

            on<Event.ArriveRope4> { transitionTo(State.Rope4, SideEffect.Rope4) }
            on<Event.ArrivePath4> { transitionTo(State.Path4, SideEffect.Path4) }
            on<Event.ArriveTrack4> { transitionTo(State.Track4, SideEffect.Track4) }
            on<Event.ArriveCoal4> { transitionTo(State.Coal4, SideEffect.Coal4) }
        }
        state<State.Start> {
            on<Event.NoLongerBusy> {
                if (threePartCourse.invoke()) {
                    transitionTo(State.Rope, SideEffect.East)
                } else {

                    transitionTo(State.Rope4, SideEffect.South)
                }
            }
            on<Event.StandSuccess> { transitionTo(State.Start, SideEffect.CancelTimeout(Timeout.StandTimeout)) }
            on<Event.Success> { transitionTo(State.Rope, SideEffect.East) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Stand) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.StandTimeout) {
                    transitionTo(State.Start, SideEffect.Stand)
                } else {
                    dontTransition()
                }
            }
        }
        state<State.Rope> {
            on<Event.NoLongerBusy> { transitionTo(State.Rope, SideEffect.ClimbRope) }
            on<Event.ArriveRope> { transitionTo(State.Rope, SideEffect.ClimbRope) }
            on<Event.StartedClimbing> { transitionTo(State.Rope, SideEffect.CancelTimeout(Timeout.ClimbTimeout)) }
            on<Event.ArrivePlank> { transitionTo(State.Plank, SideEffect.GoPlank) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Failed.RopeFailed(++ropeFailureCount)) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.ClimbTimeout) {
                    transitionTo(State.Rope, SideEffect.ClimbRope)
                } else {
                    dontTransition()
                }
            }
        }
        state<State.Plank> {
            on<Event.NoLongerBusy> { transitionTo(State.Plank, SideEffect.GoPlank) }
            on<Event.ArrivePlank> { transitionTo(State.Plank, SideEffect.GoPlank) }
            on<Event.StartedPlank> { transitionTo(State.Plank, SideEffect.CancelTimeout(Timeout.PlankTimeout)) }
            on<Event.ArrivePath> { transitionTo(State.Path, SideEffect.GoPath) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Failed.PlankFailed(++plankFailureCount)) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.PlankTimeout) {
                    transitionTo(State.Plank, SideEffect.GoPlank)
                } else {
                    dontTransition()
                }
            }
        }
        state<State.Path> {
            on<Event.NoLongerBusy> { transitionTo(State.Path, SideEffect.GoPath) }
            on<Event.ArrivePath> { transitionTo(State.Path, SideEffect.GoPath) }
            on<Event.StartedPath> { transitionTo(State.Path, SideEffect.CancelTimeout(Timeout.PathTimeout)) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Failed.PathFailed(++pathFailureCount)) }
            on<Event.Success> { transitionTo(State.Start, SideEffect.Success(++successCount, it.message)) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.PathTimeout) {
                    transitionTo(State.Path, SideEffect.GoPath)
                } else {
                    dontTransition()
                }
            }
        }

        state<State.Rope4> {
            on<Event.NoLongerBusy> { transitionTo(State.Rope4, SideEffect.Rope4) }
            on<Event.ArriveRope4> { transitionTo(State.Rope4, SideEffect.Rope4) }
            on<Event.ArrivePath4> { transitionTo(State.Path4, SideEffect.Path4) }
            on<Event.StartedRope4> { transitionTo(State.Rope4, SideEffect.CancelTimeout(Timeout.Rope4Timeout)) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Failed.Rope4Failed(++rope4FailureCount)) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.Rope4Timeout) {
                    transitionTo(State.Rope4, SideEffect.Rope4)
                } else {
                    dontTransition()
                }
            }
        }
        state<State.Path4> {
            on<Event.NoLongerBusy> { transitionTo(State.Path4, SideEffect.Path4) }
            on<Event.ArrivePath4> { transitionTo(State.Path4, SideEffect.Path4) }
            on<Event.ArriveTrack4> { transitionTo(State.Track4, SideEffect.Track4) }
            on<Event.StartedPath4> { transitionTo(State.Path4, SideEffect.CancelTimeout(Timeout.Path4Timeout)) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Failed.Path4Failed(++path4FailureCount)) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.Path4Timeout) {
                    transitionTo(State.Path4, SideEffect.Path4)
                } else {
                    dontTransition()
                }
            }
        }
        state<State.Track4> {
            on<Event.NoLongerBusy> { transitionTo(State.Track4, SideEffect.Track4) }
            on<Event.ArriveTrack4> { transitionTo(State.Track4, SideEffect.Track4) }
            on<Event.StartedTrack4> { transitionTo(State.Track4, SideEffect.CancelTimeout(Timeout.Track4Timeout)) }
            on<Event.ArriveCoal4> { transitionTo(State.Coal4, SideEffect.Coal4) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Failed.Track4Failed(++track4FailureCount)) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.Track4Timeout) {
                    transitionTo(State.Track4, SideEffect.Track4)
                } else {
                    dontTransition()
                }
            }
        }
        state<State.Coal4> {
            on<Event.NoLongerBusy> { transitionTo(State.Coal4, SideEffect.Coal4) }
            on<Event.ArriveCoal4> { transitionTo(State.Coal4, SideEffect.Coal4) }
            on<Event.StartedCoal4> { transitionTo(State.Coal4, SideEffect.CancelTimeout(Timeout.Coal4Timeout)) }
            on<Event.Failed> { transitionTo(State.Start, SideEffect.Failed.Coal4Failed(++coal4FailureCount)) }
            on<Event.Success> { transitionTo(State.Start, SideEffect.Success(++successCount, it.message)) }
            on<Event.TimeoutEvent> {
                if (it.timeout is Timeout.Coal4Timeout) {
                    transitionTo(State.Rope4, SideEffect.Rope4)
                } else {
                    dontTransition()
                }
            }
        }

        onTransition {
            val validTransition = it as? StateMachine.Transition.Valid
            if (validTransition == null) {
                val invalid = it as StateMachine.Transition.Invalid
                println("INVALID: ${invalid.fromState.javaClass.simpleName} with ${invalid.event.javaClass.simpleName}.")
                return@onTransition
            }
            println("${validTransition.fromState.javaClass.simpleName} to ${validTransition.toState.javaClass.simpleName} is valid. With Side Effect: ${validTransition.sideEffect?.javaClass?.simpleName}")
            validTransition.sideEffect?.let { it1 -> sideEffectListener.invoke(it1) }
        }
    }

}
