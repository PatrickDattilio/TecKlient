package com.dattilio.klient.plugins.map

import com.dattilio.klient.plugins.map.MapStateMachine.*
import com.tinder.StateMachine

class MapParser(
    val mapPreProcessor: MapPreProcessor,
    val alertManager: AlertManager,
    private val stateMachine: StateMachine<State, Event, SideEffect>
) {
    fun processLine(line: String) {
        line.apply {
            when {
                contains(", to the northwest.") -> stateMachine.transition(Event.MovedNorthWest(line))
                contains(", to the north.") -> stateMachine.transition(Event.MovedNorth(line))
                contains(", to the northeast.") -> stateMachine.transition(Event.MovedNorthEast(line))
                contains(", to the east.") -> stateMachine.transition(Event.MovedEast(line))
                contains(", to the southeast.") -> stateMachine.transition(Event.MovedSouthEast(line))
                contains(", to the south.") -> stateMachine.transition(Event.MovedSouth(line))
                contains(", to the southwest.") -> stateMachine.transition(Event.MovedSouthWest(line))
                contains(", to the west.") -> stateMachine.transition(Event.MovedWest(line))
//                contains("You arrive at") -> stateMachine.transition(Event.RoomDescription(line))
            }
        }
    }
}
