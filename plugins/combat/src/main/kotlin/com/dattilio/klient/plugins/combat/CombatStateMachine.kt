package com.dattilio.klient.plugins.combat

import com.tinder.StateMachine

class CombatStateMachine(sideEffectListener: (sideEffect: SideEffect) -> Unit) {

    sealed class State {
        object Attack : State()
        object GetWeapon : State()
        object Wield : State()
        object Kill : State()
        object Release : State()
        object Retreat : State()
        object Idle : State()
    }

    sealed class Event {
        object Idle : Event()
        object WeaponDropped : Event()
        object WeaponRecovered : Event()
        object WeaponWielded : Event()
        object NoLongerBusy : Event()
        object EnemyHitYou : Event()
        object EnemyMissedYou : Event()
        object Bound : Event()
        object EnemyUnconscious : Event()
        object EnemyKilled : Event()
        object Unbound : Event()
        object TooFar : Event()
        object Retreat : Event()
        object SuccessfulRetreat : Event()

    }

    sealed class SideEffect() {
        object Attack : SideEffect()
        object GetWeapon : SideEffect()
        object Wield : SideEffect()
        object Release : SideEffect()
        object Kill : SideEffect()
        object Look : SideEffect()
        object Retreat : SideEffect()
        object Lunge : SideEffect()
    }

    val stateMachine = StateMachine.create<State, Event, SideEffect> {
        initialState(State.Idle)
        state<State.Idle> {
            on<Event.EnemyHitYou> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.EnemyMissedYou> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.Idle> { transitionTo(State.Idle) }
        }
        state<State.Attack> {
            on<Event.NoLongerBusy> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.WeaponRecovered> { transitionTo(State.Wield, SideEffect.Wield) }
            on<Event.TooFar> { transitionTo(State.Attack, SideEffect.Lunge) }
            on<Event.Retreat> { transitionTo(State.Retreat, SideEffect.Retreat) }
            on<Event.EnemyUnconscious> { transitionTo(State.Kill) }
            on<Event.Idle> { transitionTo(State.Idle) }
            on<Event.EnemyHitYou> { transitionTo(State.Attack) }
            on<Event.EnemyMissedYou> { transitionTo(State.Attack) }

        }
        state<State.Kill> {
            on<Event.EnemyKilled> { transitionTo(State.Idle, SideEffect.Look) }
            on<Event.NoLongerBusy> { transitionTo(State.Kill, SideEffect.Kill) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.WeaponRecovered> { transitionTo(State.Wield, SideEffect.Wield) }
            on<Event.EnemyUnconscious> { transitionTo(State.Kill) }
            on<Event.Idle> { transitionTo(State.Idle) }
        }
        state<State.Retreat> {
            on<Event.NoLongerBusy> { transitionTo(State.Retreat, SideEffect.Retreat) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.SuccessfulRetreat> { transitionTo(State.Attack, SideEffect.Lunge) }
            on<Event.Idle> { transitionTo(State.Retreat, SideEffect.Retreat) }
        }
        state<State.Release> {
            on<Event.Unbound> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.NoLongerBusy> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.WeaponRecovered> { transitionTo(State.Wield, SideEffect.Wield) }
            on<Event.Idle> { transitionTo(State.Idle) }
        }
        state<State.GetWeapon> {
            on<Event.NoLongerBusy> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.WeaponRecovered> { transitionTo(State.Wield, SideEffect.Wield) }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.Idle> { transitionTo(State.Idle) }
        }
        state<State.Wield> {
            on<Event.WeaponWielded> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.NoLongerBusy> { transitionTo(State.Wield, SideEffect.Wield) }
            on<Event.WeaponRecovered> { transitionTo(State.Wield, SideEffect.Wield) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.Idle> { transitionTo(State.Idle) }
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
