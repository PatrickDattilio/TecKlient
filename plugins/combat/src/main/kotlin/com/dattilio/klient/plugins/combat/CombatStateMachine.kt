package com.dattilio.klient.plugins.combat

import com.tinder.StateMachine

class CombatStateMachine constructor(
    sideEffectListener: (sideEffect: SideEffect) -> Unit
) {

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


        object NoLongerBusy : Event()
        object UnderAttack : Event()

        object Bound : Event()
        object EnemyUnconscious : Event()
        object EnemyKilled : Event()
        object Unbound : Event()
        object TooFar : Event()

        object Retreat : Event()
        object NewOpponent : Event()


        sealed class Timeout(val sideEffect: SideEffect) : Event() {
            object Attack : Timeout(SideEffect.Attack)
            object Retreat : Timeout(SideEffect.Retreat)
            object GetWeapon : Timeout(SideEffect.GetWeapon)
            object Wield : Timeout(SideEffect.Wield)
            object Approach : Timeout(SideEffect.Approach)
        }

        sealed class Completed(val sideEffect: SideEffect) : Event() {
            object Attack : Completed(SideEffect.Attack)
            object Retreat : Completed(SideEffect.Retreat)
            object GetWeapon : Completed(SideEffect.GetWeapon)
            object Wield : Completed(SideEffect.Wield)
            object Approach : Completed(SideEffect.Approach)
        }

    }

    sealed class SideEffect(val event: Event? = null) {
        object Attack : SideEffect(event = Event.Timeout.Attack)
        object RepeatAttack : SideEffect(event = Event.Timeout.Attack)
        object GetWeapon : SideEffect(Event.Timeout.GetWeapon)
        object Wield : SideEffect(Event.Timeout.Wield)
        object Release : SideEffect()
        object Kill : SideEffect()
        object Status : SideEffect()
        object Retreat : SideEffect(event=Event.Timeout.Retreat)
        object Lunge : SideEffect()
        object Approach : SideEffect(event=Event.Timeout.Approach)
        class Completed(
            val sideEffect: SideEffect? = null,
            val cancelTimeout: SideEffect
        ) : SideEffect()

        class Timeout(val sideEffect: SideEffect) : SideEffect()

        fun timeout() = Timeout(this)
        fun completed(cancelTimeout: SideEffect) = Completed(this, cancelTimeout)
    }


    var closeGap: Boolean = false
    var killingBlow: Boolean = false
    var attackDummy: Boolean = false
    val stateMachine = StateMachine.create<State, Event, SideEffect> {
        attackDummy.let {
            if (it) {
                initialState(State.Attack)
            } else {
                initialState(State.Idle)
            }
        }
        state<State.Idle> {
            on<Event.NewOpponent> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.UnderAttack> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.Completed.Attack> { transitionTo(State.Attack) }
            on<Event.Idle> { transitionTo(State.Idle) }
        }

        state<State.Attack> {
            on<Event.NoLongerBusy> { transitionTo(State.Attack, SideEffect.Attack) }
//            on<Event.UnderAttack> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.Completed.Attack> {
                transitionTo(
                    State.Attack,
                    SideEffect.Completed(cancelTimeout = it.sideEffect)
                )
            }
            on<Event.Timeout> { transitionTo(State.Attack, SideEffect.Timeout(it.sideEffect)) }

            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.Timeout.GetWeapon> { transitionTo(State.GetWeapon, SideEffect.GetWeapon.timeout()) }
            on<Event.Completed.GetWeapon> { transitionTo(State.Wield, SideEffect.Wield.completed(it.sideEffect)) }
            on<Event.TooFar> {
                if (closeGap) {
                    transitionTo(State.Attack, SideEffect.Lunge)
                } else {
                    transitionTo(State.Attack, SideEffect.Approach)
                }
            }
            on<Event.Retreat> { transitionTo(State.Retreat, SideEffect.Retreat) }
            on<Event.EnemyUnconscious> {
                if (killingBlow) {
                    transitionTo(State.Kill)
                } else {
                    transitionTo(State.Idle, SideEffect.Completed(cancelTimeout = SideEffect.Attack))
                }

            }
            on<Event.Idle> { transitionTo(State.Idle) }

        }
        state<State.Kill> {
            on<Event.EnemyKilled> { transitionTo(State.Idle, SideEffect.Status) }
            on<Event.NoLongerBusy> { transitionTo(State.Kill, SideEffect.Kill) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.Timeout> { transitionTo(this, it.sideEffect) }
            on<Event.Completed.GetWeapon> { transitionTo(State.Wield, SideEffect.Wield.completed(it.sideEffect)) }
            on<Event.EnemyUnconscious> { transitionTo(State.Kill) }
            on<Event.Idle> { transitionTo(State.Idle) }
        }
        state<State.Retreat> {
            on<Event.NoLongerBusy> { transitionTo(State.Retreat, SideEffect.Retreat) }
            on<Event.Idle> { transitionTo(State.Retreat, SideEffect.Retreat) }
            on<Event.Timeout> { transitionTo(this, it.sideEffect) }
            on<Event.Completed.Retreat> { transitionTo(State.Attack, SideEffect.Lunge) }

            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
        }
        state<State.Release> {
            on<Event.Unbound> { transitionTo(State.Attack, SideEffect.Attack) }
            on<Event.NoLongerBusy> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.Timeout> { transitionTo(this, it.sideEffect) }
            on<Event.Completed.GetWeapon> {
                transitionTo(
                    State.Wield,
                    SideEffect.GetWeapon.completed(SideEffect.Wield)
                )
            }
            on<Event.Idle> { transitionTo(State.Idle) }
        }
        state<State.GetWeapon> {
            on<Event.NoLongerBusy> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.Timeout> { transitionTo(this, it.sideEffect) }
            on<Event.Completed.GetWeapon> {
                transitionTo(
                    State.Wield,
                    SideEffect.Wield.completed(SideEffect.GetWeapon)
                )
            }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.Bound> { transitionTo(State.Release, SideEffect.Release) }
            on<Event.Idle> { transitionTo(State.Idle) }
        }
        state<State.Wield> {
            on<Event.NoLongerBusy> { transitionTo(State.Wield, SideEffect.Wield) }

            on<Event.Timeout.Wield> { transitionTo(State.Wield, SideEffect.Wield.timeout()) }
            on<Event.Completed.Wield> { transitionTo(State.Idle, SideEffect.Wield.completed(it.sideEffect)) }
            on<Event.WeaponDropped> { transitionTo(State.GetWeapon, SideEffect.GetWeapon) }
            on<Event.Timeout.GetWeapon> { transitionTo(State.GetWeapon, SideEffect.GetWeapon.timeout()) }
            on<Event.Completed.GetWeapon> { transitionTo(State.Wield, SideEffect.Wield.completed(it.sideEffect)) }
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
