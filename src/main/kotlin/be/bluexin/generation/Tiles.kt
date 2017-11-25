package be.bluexin.generation

import be.bluexin.generation.Orientation.*


/**
 * Part of tests by bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
abstract class Tile(val pos: Position, vararg orientations: Orientation) {

    val connections = mutableSetOf(*orientations)
    val decorations = mutableSetOf<TileDecoration>()

    abstract val text: Array<String>

    open val replaceable = false

    override fun toString() = "${this::class.simpleName} at $pos"

    operator fun TileDecoration.unaryPlus() {
        this@Tile.decorations += this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tile

        return when {
            pos != other.pos -> false
            connections != other.connections -> false
            decorations != other.decorations -> false
            else -> true
        }
    }

    override fun hashCode() = pos.hashCode()

}

class Room(pos: Position, vararg orientations: Orientation = Orientation.values()) : Tile(pos, *orientations) {

    override val text
        get() = arrayOf(
                "┏━${charTo(N)}━┓",
                "┃   ┃",
                "${charTo(W)} ${centerChar()} ${charTo(E)}",
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

    private fun centerChar() = if (decorations.contains(Stairs.UP)) 'U' else if (decorations.contains(Stairs.DOWN)) 'D' else 'R'
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
