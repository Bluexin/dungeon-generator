package be.bluexin.generation

import be.bluexin.generation.Orientation.*


/**
 * Part of tests by bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class Tile(val pos: Position, vararg orientations: Orientation) {

    val connections = mutableSetOf(*orientations)

    infix fun to(that: Tile) = this.pos to that.pos
    infix fun to(that: Position) = this.pos to that
    abstract val text: Array<String>

    open val replaceable = false

    override fun toString() = "${this::class.simpleName} at $pos"

    override fun equals(other: Any?) = this === other || (other is Tile && pos == other.pos)
    override fun hashCode() = pos.hashCode()

}

class Room(pos: Position, vararg orientations: Orientation = Orientation.values()) : Tile(pos, *orientations) {

    override val text
        get() = arrayOf(
                "┏━${charTo(N)}━┓",
                "┃   ┃",
                "${charTo(W)} r ${charTo(E)}",
                "┃   ┃",
                "┗━${charTo(S)}━┛"
        )

    private fun charTo(orientation: Orientation) = if (orientation in connections) when (orientation) {
        Orientation.N -> '△'
        Orientation.E -> '▷'
        Orientation.S -> '▽'
        Orientation.W -> '◁'
    } else when (orientation) {
        Orientation.N -> '━'
        Orientation.E -> '┃'
        Orientation.S -> '━'
        Orientation.W -> '┃'
    }
}

class Corridor(pos: Position, vararg orientations: Orientation = emptyArray()) : Tile(pos, *orientations) {

    override val text
        get() = arrayOf(
                "░░${charTo(N)}░░",
                "░░${charTo(N)}░░",
                "${charTo(W)}${charTo(W)} ${charTo(E)}${charTo(E)}",
                "░░${charTo(S)}░░",
                "░░${charTo(S)}░░"
        )

    private fun charTo(orientation: Orientation) = if (orientation in connections) ' ' else '░'

    override val replaceable: Boolean
        get() = this.connections.isEmpty()
}

class Wall(pos: Position) : Tile(pos) {
    override val text = arrayOf(
            "░░░░░",
            "░░░░░",
            "░░░░░",
            "░░░░░",
            "░░░░░"
    )
}

class Empty(pos: Position) : Tile(pos, *Orientation.values()) {
    override val text = arrayOf(
            "     ",
            "     ",
            "     ",
            "     ",
            "     "
    )

    override val replaceable = true
}
