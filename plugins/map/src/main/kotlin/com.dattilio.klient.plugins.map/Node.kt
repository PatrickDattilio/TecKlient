package com.dattilio.klient.plugins.map

import java.util.*

data class Point(val x: Int, val y: Int, val z: Int)
class TecMap : TreeMap<Long, Node>()
private fun xDelta(direction: Direction): Int {
    return when (direction) {
        Direction.SW, Direction.W, Direction.NW -> -1
        Direction.E, Direction.NE, Direction.SE -> 1
        else -> 0
    }
}
private fun yDelta(direction: Direction): Int {
    return when (direction) {
        Direction.NW, Direction.N, Direction.NE -> 1
        Direction.SW, Direction.S, Direction.SE -> -1
        else -> 0
    }
}


private fun zDelta(direction: Direction): Int {
    return when (direction) {
        Direction.UP -> 1
        Direction.DOWN-> -1
        else -> 0
    }
}

data class Node constructor(
    val id: Long,
    val exits: MutableMap<Direction, Exit>,
    val label: String? = null,
    val x: Int,
    val y: Int,
    val z: Int
) {
    constructor(id: Long, exits: MutableMap<Direction, Exit>, label: String?, oldPosition: Node, direction: Direction) :
            this(id,
                exits,
                label,
                oldPosition.x + xDelta(direction),
                oldPosition.y+ yDelta(direction),
                oldPosition.z + zDelta(direction))


}

data class Exit(val id: Long, var confirmed: Boolean = true)

enum class Direction {
    SW,
    S,
    SE,
    W,
    E,
    NW,
    N,
    NE,
    UP,
    DOWN,
    OTHER
}


//sealed class Exit(val direction: Direction, open val command: String, open val node: Node? = null) {
//
//    class SW(override val node: Node) : Exit("1", node)
//    class S(override val node: Node) : Exit("2", node)
//    class SE(override val node: Node) : Exit("3", node)
//    class W(override val node: Node) : Exit("4", node)
//    class E(override val node: Node) : Exit("6", node)
//    class NW(override val node: Node) : Exit("7", node)
//    class N(override val node: Node) : Exit("8", node)
//    class NE(override val node: Node) : Exit("9", node)
//    class UP(override val node: Node) : Exit("u", node)
//    class DOWN(override val node: Node) : Exit("d", node)
//    data class OTHER(override val command: String, override val node: Node) : Exit(command, node)
//}
