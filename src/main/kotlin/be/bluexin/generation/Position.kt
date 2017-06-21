package be.bluexin.generation

import java.util.*

/**
 * Part of tests by bluexin, released under GNU GPLv3.
 *
 * @author Bluexin
 */
data class Position(val x: Int, val y: Int) : Comparable<Position>, (Orientation) -> Position {
    override fun compareTo(other: Position): Int {
        val x = this.x.compareTo(other.x)
        return if (x == 0) this.y.compareTo(other.y) else x
    }

    operator fun plus(other: Position) = this.x + other.x to this.y + other.y
    operator fun minus(other: Position) = this.x - other.x to this.y - other.y

    infix fun to(other: Position) = Math.abs(this.x - other.x) + Math.abs(this.y - other.y)
    infix fun to(that: Tile) = this to that.pos

    override fun invoke(p1: Orientation) = p1(this)

    override fun toString() = "($x; $y)"

    infix fun dir(that: Position) = Orientation.values().first { it(this) == that }
}

operator fun <T> Array<Array<T>>.set(pos: Position, value: T) {
    this[pos.x][pos.y] = value
}

operator fun <T> Array<Array<T>>.get(pos: Position) = this[pos.x][pos.y]

infix fun Int.to(that: Int) = Position(this, that)

enum class Orientation(private val move: (Position) -> Position) : (Position) -> Position {
    N({ Position(it.x, it.y - 1) }),
    E({ Position(it.x + 1, it.y) }),
    S({ Position(it.x, it.y + 1) }),
    W({ Position(it.x - 1, it.y) });

    override fun invoke(p1: Position) = move(p1)

    operator fun not() = when (this) {
        N -> S
        E -> W
        S -> N
        W -> E
    }

    companion object {
        private val rng = Random(4156)

        fun generate(amount: Int = 2, vararg connections: Orientation): Array<Orientation> {
            return when (amount) {
                0 -> emptyArray<Orientation>()
                4 -> Orientation.values()
                else -> {
                    val l = mutableSetOf<Orientation>()
                    var fromMark = -1
                    (0 until amount).forEach {
                        if (it == 0 && connections.isNotEmpty() && rng.nextFloat() > 0.2f) {
                            fromMark = rng.nextInt(connections.size)
                            l.add(connections[fromMark])
                        } else if (fromMark >= 0 && !connections[fromMark] !in l && rng.nextFloat() > 0.33f) l.add(!connections[fromMark])
                        else {
                            var o = Orientation.values()[rng.nextInt(4)]
                            while (o in l) o = Orientation.values()[rng.nextInt(4)]
                            l.add(o)
                        }
                    }
                    l.toTypedArray()
                }
            }

        }
    }
}
