package com.dattilio.klient.plugins.map

import com.dattilio.klient.plugins.map.MapStateMachine.SideEffect.NewRoom
import com.dattilio.klient.plugins.map.MapStateMachine.SideEffect.UpdatePosition
import com.tinder.StateMachine
import guru.nidi.graphviz.attribute.Attributes
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import guru.nidi.graphviz.engine.GraphvizCmdLineEngine
import guru.nidi.graphviz.model.*
import guru.nidi.graphviz.model.Factory.mutNode
import guru.nidi.graphviz.parse.Parser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.File
import java.util.*


class MapStateMachine(sideEffectListener: (position: Node, sideEffect: SideEffect) -> Unit) {
    private val dotFile = File("map.dot")
    val tecMap = TecMap()
    var graphvizInitialized = false
    var map: MutableGraph? = null

    private var position: Node = Node(-1, mutableMapOf(), "Toga", 0, 0, 0)

    init {
        if (dotFile.exists()) {
            map = Parser().read(dotFile)
            map?.let {
                it.nodes().forEach { node ->
                    tecMap[node.name().toString().toLong()] =
                        Node(
                            node.name().toString().toLong(),
                            mutableMapOf(),
                            node.get("tag").toString(),
                            node.get("x").toString().toInt(),
                            node.get("y").toString().toInt(),
                            node.get("z").toString().toInt()
                        )
                }
            }
        } else {
            tecMap[position.id] = position
        }
    }

    sealed class State {
        object Moved : State()

    }

    sealed class Event {
        class SetInitialPosition(long: Long)

        class RoomDescription(line: String) : Event()

        class MovedEast(val line: String) : Event() {

        }

        class MovedWest(val line: String) : Event() {

        }

        class MovedNorth(val line: String) : Event() {

        }

        class MovedSouth(val line: String) : Event() {

        }

        class MovedNorthEast(val line: String) : Event() {

        }

        class MovedSouthEast(val line: String) : Event() {

        }

        class MovedNorthWest(val line: String) : Event() {

        }

        class MovedSouthWest(val line: String) : Event() {

        }
    }

    sealed class SideEffect {
        class NewRoom(val position: Node, val direction: Direction, val line: String) : SideEffect()
        class UpdatePosition(val position: Node, val direction: Direction) : SideEffect()
    }

    val stateMachine = StateMachine.create<State, Event, SideEffect> {
        initialState(State.Moved)
        state<State.Moved> {
            on<Event.MovedEast> { transitionTo(State.Moved, getTransitionForMove(position, Direction.E, it.line)) }
            on<Event.MovedSouthEast> {
                transitionTo(
                    State.Moved,
                    getTransitionForMove(position, Direction.SE, it.line)
                )
            }
            on<Event.MovedSouth> { transitionTo(State.Moved, getTransitionForMove(position, Direction.S, it.line)) }
            on<Event.MovedSouthWest> {
                transitionTo(
                    State.Moved,
                    getTransitionForMove(position, Direction.SW, it.line)
                )
            }
            on<Event.MovedWest> { transitionTo(State.Moved, getTransitionForMove(position, Direction.W, it.line)) }
            on<Event.MovedNorthWest> {
                transitionTo(
                    State.Moved,
                    getTransitionForMove(position, Direction.NW, it.line)
                )
            }
            on<Event.MovedNorth> { transitionTo(State.Moved, getTransitionForMove(position, Direction.N, it.line)) }
            on<Event.MovedNorthEast> {
                transitionTo(
                    State.Moved,
                    getTransitionForMove(position, Direction.NE, it.line)
                )
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
            validTransition.sideEffect?.let { sideEffect ->
                when (sideEffect) {
                    is NewRoom -> createRoom(sideEffect.position, sideEffect.direction, sideEffect.line)
                    is UpdatePosition -> updatePosition(sideEffect.position, sideEffect.direction)
                }
            }

        }
    }


    private fun updatePosition(newPosition: Node, direction: Direction) {
        if (position.exits[direction]?.confirmed == false) {
            position.exits[direction]?.confirmed = true
        }
        position = newPosition
    }


    private fun getTransitionForMove(position: Node, direction: Direction, line: String): SideEffect {
        val newRoom = position.exits[direction]
        return if (newRoom == null) {
            NewRoom(position, direction, line)
        } else {
            UpdatePosition(tecMap[newRoom.id]!!, direction)
        }
    }

    private fun saveGraph() {

        val map: MutableGraph = Factory.mutGraph().setDirected(true)
        val sortedMap= TreeMap<Point,Node>(kotlin.Comparator { first, second ->
            var delta = first.x - second.x

            if(delta ==0){
                delta = first.y-second.y
            }

            return@Comparator delta
        })
        sortedMap.putAll(tecMap.mapKeys {(id,node) -> Point(node.x,node.y,node.z)  })
        val initialRank = 0
        sortedMap.forEach { (point, node) ->

            val room = mutNode(node.id.toString())
//            var rank = room["rank"]
//            if (rank == null) {
//                rank = initialRank
//            } else {
//                rank as Int
//            }
            room.add("rank", "same ${point.y}")
            node.exits.forEach { (dir, exit) ->
//                var otherRank = rank
                val other = mutNode(exit.id.toString())
                var link: LinkTarget?=null
                when (dir) {
                    Direction.SW -> {
                        link = room.port(Compass.SOUTH_WEST).linkTo(other.port(Compass.NORTH_EAST))
                    }
                    Direction.S -> {
                        link =room.port(Compass.SOUTH).linkTo(other.port(Compass.NORTH))
                    }
                    Direction.SE -> {
                        link =room.port(Compass.SOUTH_EAST).linkTo(other.port(Compass.NORTH_WEST))
                    }
                    Direction.W ->
                        link =room.port(Compass.WEST).linkTo(other.port(Compass.EAST))
                    Direction.E ->
                        link =room.port(Compass.EAST).linkTo(other.port(Compass.WEST))
                    Direction.NW ->
                        link =room.port(Compass.NORTH_WEST).linkTo(other.port(Compass.SOUTH_EAST))

                    Direction.N ->
                        link =room.port(Compass.NORTH).linkTo(other.port(Compass.SOUTH))

                    Direction.NE ->
                        link =room.port(Compass.NORTH_EAST).linkTo(other.port(Compass.SOUTH_WEST))

                    Direction.UP -> {
                    }
                    Direction.DOWN -> {
                    }
                    Direction.OTHER -> {
                    }
                }
                link?.let { room.addLink(it)}

                node.label?.let { room.add(Attributes.attr("tag", node.label)) }
            }
            map.add(room)
        }
        if (!graphvizInitialized) {
            Graphviz.useEngine(GraphvizCmdLineEngine())
            graphvizInitialized = true
        }
        val viz: Graphviz = Graphviz.fromGraph(map)
        val dot = viz.render(Format.DOT).toString()
        File("map.dot").writeText(dot)
    }

    private fun Direction.reverse(): Direction {
        return when (this) {
            Direction.SW -> Direction.NE
            Direction.S -> Direction.N
            Direction.SE -> Direction.NW
            Direction.W -> Direction.E
            Direction.E -> Direction.W
            Direction.NW -> Direction.SE
            Direction.N -> Direction.S
            Direction.NE -> Direction.SW
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.OTHER -> Direction.OTHER
        }
    }

    fun createRoom(position: Node, direction: Direction, line: String) {
        val newRoom = Node(UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE, mutableMapOf(),line,position,direction)
        position.exits[direction] = Exit(newRoom.id)
        newRoom.exits[direction.reverse()] = Exit(id = position.id, confirmed = false)
        tecMap[newRoom.id] = newRoom
        tecMap[position.id] = position

//        tecMap[newRoom.id] = newRoom
        this.position = newRoom
    }

    fun save() {
        GlobalScope.async {
            saveGraph()
        }
    }
}
