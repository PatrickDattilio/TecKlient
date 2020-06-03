package com.dattilio.klient.headless.locksmithing

import com.tinder.StateMachine


class LocksmithingStateMachine constructor(
    sideEffectListener: (sideEffect: Action) -> Unit,
    private val settings: LocksmithSettings
) {

    sealed class Event {
        object NoLongerBusy : Event()
        class Timeout(val action: Action) : Event()
        object Failed : Event()
        object Success : Event()
    }

    sealed class Action {
        class Retry(val command: String) : Action()
        class PerformAction(val command: String) : Action()
        object Alert : Action()
        object Success: Action()
    }


    sealed class State {
        object Unlock : State()
        object Lock : State()
        object Jam : State()
        object Unjam : State()
        object Study : State()
        object Recall : State()
    }


    val stateMachine = StateMachine.create<State, Event, Action> {

        initialState(State.Unlock)


        state<State.Unlock> {
            on<Event.NoLongerBusy> {
                transitionTo(State.Unlock, Action.PerformAction(settings.unlock))
            }
            on<Event.Timeout> {
                transitionTo(State.Unlock, Action.Retry(settings.unlock))
            }
            on<Event.Failed> {
                transitionTo(State.Unlock, Action.PerformAction(settings.unlock))
            }
            on<Event.Success> {
                transitionTo(State.Lock, Action.Success)
            }
        }
        state<State.Lock> {
            on<Event.NoLongerBusy> {
                transitionTo(State.Lock, Action.PerformAction(settings.lock))
            }
            on<Event.Timeout> {
                transitionTo(State.Lock, Action.Retry(settings.lock))
            }
            on<Event.Failed> {
                transitionTo(State.Lock, Action.PerformAction(settings.lock))
            }
            on<Event.Success> {
                transitionTo(State.Jam, Action.Success)
            }
        }
        state<State.Jam> {
            on<Event.NoLongerBusy> {
                transitionTo(State.Jam, Action.PerformAction(settings.jam))
            }
            on<Event.Timeout> {
                transitionTo(State.Jam, Action.Retry(settings.jam))
            }
            on<Event.Failed> {
                transitionTo(State.Jam, Action.PerformAction(settings.jam))
            }
            on<Event.Success> {
                transitionTo(State.Unjam, Action.Success)
            }
        }
        state<State.Unjam> {
            on<Event.NoLongerBusy> {
                transitionTo(State.Unjam, Action.PerformAction(settings.unjam))
            }
            on<Event.Timeout> {
                transitionTo(State.Unjam, Action.Retry(settings.unjam))
            }
            on<Event.Failed> {
                transitionTo(State.Unjam, Action.PerformAction(settings.unjam))
            }
            on<Event.Success> {
                transitionTo(State.Study, Action.Success)
            }
        }
        state<State.Study> {
            on<Event.NoLongerBusy> {
                transitionTo(State.Study, Action.PerformAction(settings.study))
            }
            on<Event.Timeout> {
                transitionTo(State.Study, Action.Retry(settings.study))
            }
            on<Event.Failed> {
                transitionTo(State.Study, Action.PerformAction(settings.study))
            }
            on<Event.Success> {
                transitionTo(State.Recall, Action.Success)
            }
        }
        state<State.Recall> {
            on<Event.NoLongerBusy> {
                transitionTo(State.Recall, Action.PerformAction(settings.recall))
            }
            on<Event.Timeout> {
                transitionTo(State.Recall, Action.Retry(settings.recall))
            }
            on<Event.Failed> {
                transitionTo(State.Recall, Action.PerformAction(settings.recall))
            }
            on<Event.Success> {
                transitionTo(State.Unlock, Action.Success)
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